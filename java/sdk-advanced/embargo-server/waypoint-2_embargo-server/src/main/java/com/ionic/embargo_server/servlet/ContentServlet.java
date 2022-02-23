/**
* (c) 2020-2021 Ionic Security Inc.  All rights reserved.
* By using this code, I agree to the Privacy Policy (https://www.ionic.com/privacy-notice/),
* and the License Agreement (https://dev.ionic.com/license).
*/

package com.ionic.embargo_server.servlet;

import com.ionic.embargo_server.common.Webapp;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.key.KeyAttribute;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.core.date.DateTime;
import com.ionic.sdk.device.DeviceUtils;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.error.IonicException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Handle display of HTML form used to upload content to embargo.  Handle upload of content.
 */
public class ContentServlet extends HttpServlet {

    /**
     * Class scoped logger.
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public final void init(final ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    @Override
    public final void destroy() {
        super.destroy();
    }

    @Override
    protected final void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        // Response HTML template.
        String templateFileName = "html/SubmitEmbargoContent.html";
        ClassLoader classLoader = getClass().getClassLoader();
        File templateFile = new File(classLoader.getResource(templateFileName).getFile());

        // Put the embargo date in the request.
        byte[] html = Files.readAllBytes(templateFile.toPath());
        String htmlText = new String(html).replace("$DATE", Webapp.toString(new Date()));
        
        // Show alert (if present)
        final String alert = System.getProperty(getClass().getName());
        if (alert != null) {
            logger.info("processing alert");
            System.getProperties().remove(getClass().getName());

            htmlText = htmlText
                .replace("display: none", "display: block")  // CSS enable display
                .replace("$ALERT", alert);  // CSS show alert text
        }

        // Back to bytes.
        html = htmlText.getBytes(StandardCharsets.UTF_8);

        // Populate the HTML response.
        response.setStatus(HttpURLConnection.HTTP_OK);
        response.setContentType("text/html; charset=utf-8");
        response.getOutputStream().write(html);
    }

    @Override
    protected final void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String filename = null;
            byte[] content = null;
            String embargoDate = null;

            // From maven/commons-fileupload/commons-fileupload/1.4
            List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : multiparts) {
                String name = item.getFieldName();
                String value = item.getString();
                if (!item.isFormField()) {
                    filename = item.getName();
                    content = item.get();
                } else if ("embargoDate".equals(name)) {
                    embargoDate = value;
                }
            }
            uploadContent(filename, content, embargoDate, request);
        } catch (Exception e) {
            // Populate alert for doGet().
            System.setProperty(getClass().getName(), String.format(
                    "Content not uploaded at %s [%s].", new Date().toString(), e.getMessage()));
            logger.severe(">>>> parameter or upload Exception: " + e.getMessage());
        }

        // instruct user agent to discard HTML form POST state
        response.setHeader("Location", request.getRequestURI());
        response.setStatus(HttpURLConnection.HTTP_MOVED_TEMP);
    }

    private void uploadContent(final String filename, final byte[] content, final String embargoDate, final HttpServletRequest request)
            throws IonicException, IOException {

        if (filename == null) {
            throw new IOException("Missing value: filename");
        } else if (content == null) {
            throw new IOException("Missing value: content");
        } else if (embargoDate == null) {
            throw new IOException("Missing value: embargo date");
        }

        // Get the Machina Agent
        final Agent agent = new Agent(new DeviceProfilePersistorPlainText(Webapp.getFileSEP().getPath()));           
        logger.info("Device ID: " + agent.getActiveProfile().getDeviceId());

        // Get the key attribute ionic-embargo.
        final KeyAttributesMap cattrsEmbargo = new KeyAttributesMap(new KeyAttribute("ionic-embargo", embargoDate));
        final CreateKeysResponse createKeysResponse = agent.createKey(cattrsEmbargo);
        final CreateKeysResponse.Key createKey = createKeysResponse.getFirstKey();
        logger.info("Created Key: " + createKey.getId() + " from Device ID: " + agent.getActiveProfile().getDeviceId());

        // Update webapp embargo data
        final File fileEmbargoMetadata = Webapp.getFileEmbargoMetadata();
        final Properties propertiesEmbargoData = new Properties();
        if (fileEmbargoMetadata.exists()) {
            propertiesEmbargoData.load(new ByteArrayInputStream(DeviceUtils.read(fileEmbargoMetadata)));
        } else {
            logger.severe(">>>> embargo.properties does not exist");
        }

        final String value = String.format("[%s][%s]", createKey.getId(), embargoDate);
        propertiesEmbargoData.setProperty(Webapp.getEmbargoURI(filename), value);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        propertiesEmbargoData.store(os, "embargo-data");
        DeviceUtils.write(fileEmbargoMetadata, os.toByteArray());

        // Upload content to persistent location.
        final File fileInfo = new File(Webapp.getFolderEmbargoContent(), filename);
        FileUtils.writeByteArrayToFile(fileInfo, content, false);
        logger.info("File: " + filename + " uploaded");

        // Populate alert
        final String uriContent = Webapp.getEmbargoURI(filename);
        final String urlContent = request.getRequestURL().toString().replace(request.getRequestURI(), uriContent);
        final Date now = new Date();
        final Date dateEmbargo = Webapp.toDate(embargoDate);
        System.setProperty(getClass().getName(), String.format(
                "Content uploaded at %s - Embargoed until %s - (%d seconds) - %s",
                new Date().toString(), embargoDate,
                (dateEmbargo.getTime() - now.getTime()) / DateTime.ONE_SECOND_MILLIS, urlContent));
    }

}
