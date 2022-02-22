/**
* (c) 2020-2021 Ionic Security Inc.  All rights reserved.
* By using this code, I agree to the Privacy Policy (https://www.ionic.com/privacy-notice/),
* and the License Agreement (https://dev.ionic.com/license).
*/

package com.ionic.embargo_server.filter;

import com.ionic.embargo_server.common.Webapp;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.core.codec.Transcoder;
import com.ionic.sdk.device.DeviceUtils;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.error.SdkData;
import com.ionic.sdk.error.SdkError;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmbargoFilter extends GenericFilter {

    /**
     * Class scoped logger.
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("init()");
        super.init(filterConfig);
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        final boolean isHttpServletRequest = (servletRequest instanceof HttpServletRequest);
        final boolean isHttpServletResponse = servletResponse instanceof HttpServletResponse;

        if (isHttpServletRequest && isHttpServletResponse) {
            try {
                doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
            } catch (IonicException e) {
                throw new ServletException(e);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void doFilter(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse,
                          final FilterChain filterChain) throws IOException, ServletException, IonicException {
        logger.info("doFilter()::" + servletRequest.getRequestURI());

        // read webapp embargo data
        final File fileEmbargoMetadata = Webapp.getFileEmbargoMetadata();
        final Properties propertiesEmbargoData = new Properties();
        if (fileEmbargoMetadata.exists()) {
            propertiesEmbargoData.load(new ByteArrayInputStream(DeviceUtils.read(fileEmbargoMetadata)));
        } else {
            logger.severe(">>>> embargo.properties does not exist");
        }

        final String requestURI = servletRequest.getRequestURI();
        final String requestURIDecode = URLDecoder.decode(requestURI, "UTF-8");
        final String valueRequestURI = propertiesEmbargoData.getProperty(requestURIDecode);
        logger.info(String.format("PROPERTY ENTRY: %s %s", servletRequest.getRequestURI(), valueRequestURI));

        if (valueRequestURI == null) {
            // for content with no Ionic embargo key, default to OPEN
            logger.info("no content with embargo keys");
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            final Matcher matcher = Pattern.compile("\\[(.+?)\\]\\[(.+?)\\]").matcher(valueRequestURI);
            SdkData.checkTrue(matcher.matches(), SdkError.ISAGENT_INVALIDVALUE, fileEmbargoMetadata.getName());
            final String keyIdEmbargo = matcher.group(1);
            final String embargoDate = matcher.group(2);
            logger.info(String.format("PROPERTY VALUE (TOKENIZED): [%s] [%s]", keyIdEmbargo, embargoDate));
            try {
                final Agent agent = new Agent(new DeviceProfilePersistorPlainText(Webapp.getFileSEP().getPath()));
                final GetKeysResponse getKeysResponse = agent.getKey(keyIdEmbargo);
                logger.info(String.format("ALLOW, EMBARGO DATE IS %s",
                        getKeysResponse.getFirstKey().getAttributesMap().get("ionic-embargo")));
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (IonicException e) {
                // populate response
                final String denyText = String.format("This content is embargoed, and is not available for" +
                        " viewing at this time.  Try back at [%s].", embargoDate);
                servletResponse.setStatus(HttpURLConnection.HTTP_OK);
                servletResponse.setContentType("text/plain; charset=utf-8");
                servletResponse.getOutputStream().write(Transcoder.utf8().decode(denyText));
            }
        }
    }

    @Override
    public void destroy() {
        logger.info("destroy()");
    }
}
