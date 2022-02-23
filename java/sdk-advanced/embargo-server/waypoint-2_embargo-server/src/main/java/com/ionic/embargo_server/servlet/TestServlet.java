/**
* (c) 2020-2021 Ionic Security Inc.  All rights reserved.
* By using this code, I agree to the Privacy Policy (https://www.ionic.com/privacy-notice/),
* and the License Agreement (https://dev.ionic.com/license).
*/

package com.ionic.embargo_server.servlet;

import com.ionic.embargo_server.common.Webapp;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.core.codec.Transcoder;
import com.ionic.sdk.core.io.Stream;
import com.ionic.sdk.core.res.Resource;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.error.IonicException;

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
import java.util.Arrays;

/**
 * Handle display of HTML form used to upload content to embargo.  Handle upload of content.
 */
public class TestServlet extends HttpServlet {

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
        // response template html
        byte[] html = Stream.read(Resource.resolve("html/RunTests.html"));

        // show alert (if present)
        final String alert = System.getProperty(getClass().getName());
        if (alert != null) {
            System.getProperties().remove(getClass().getName());
            final String htmlText = Transcoder.utf8().encode(html)
                    .replace("display: none", "display: block")  // CSS enable display
                    .replace("$ALERT", alert);  // CSS show alert text
            html = Transcoder.utf8().decode(htmlText);
        }

        // populate response
        response.setStatus(HttpURLConnection.HTTP_OK);
        response.setContentType("text/html; charset=utf-8");
        response.getOutputStream().write(html);
    }

    @Override
    protected final void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        System.setProperty(getClass().getName(), runTests());

        // instruct user agent to discard HTML form POST state
        response.setHeader("Location", request.getRequestURI());
        response.setStatus(HttpURLConnection.HTTP_MOVED_TEMP);

    }

    private final String makeTableRow(String test, String testResult) {
      return "  <tr><td>" + test + "</td><td>" + testResult + "</td></tr>\n";
    }

    private final String runTests() {
        String ret = "Running tests: \n";
        ret += "<br/>\n";
        ret += "<table>\n";
        ret += makeTableRow("Check for SEP", checkSEP());
        ret += makeTableRow("Validate SEP", validateSEP());
        ret += makeTableRow("Check Tenant", testTenant());
        ret += makeTableRow("Test Allow", testAllow());
        ret += makeTableRow("Test Deny", testDeny());
        ret += "</table>\n";
        return ret;
    }

    private final String checkSEP() {
        if (Webapp.getFileSEP().exists()) {
            return "SUCCESS";
        } else {
            return "FAILED: SEP not present.";
        }
    }

    private final String validateSEP() {
        try {
            final Agent agent = new Agent(new DeviceProfilePersistorPlainText(Webapp.getFileSEP().getPath()));
            if (agent.hasAnyProfiles() && agent.hasActiveProfile()) {
                return "SUCCESS";
            } else {
                return "FAILED: SEP invalid.";
            }
        } catch (IonicException e) {
            return "FAILED: " + e.getMessage();
        }
    }

    private final String testTenant() {
        try {
            final Agent agent = new Agent(new DeviceProfilePersistorPlainText(Webapp.getFileSEP().getPath()));
            String keyspace = agent.getActiveProfile().getKeySpace();
            agent.getKey(keyspace + "0000000");
            return "FAILED: False key was successfully returned. (???)";
        } catch (IonicException e) {
            if (e.getReturnCode() == 40024) {
                return "SUCCESS";
            }
            return "FAILED: " + e.getMessage();
        }
    }

    private final String testAllow() {
        try {
            final Agent agent = new Agent(new DeviceProfilePersistorPlainText(Webapp.getFileSEP().getPath()));
            KeyAttributesMap attributes = new KeyAttributesMap();
            attributes.put("Ionic-Embargo", Arrays.asList("2019-11-14T00:55:31.820Z"));
            String kid = agent.createKey(attributes).getFirstKey().getId();
            agent.getKey(kid);
            return "SUCCESS";
        } catch (IonicException e) {
            return "FAILED: " + e.getMessage();
        }
    }

    private final String testDeny() {
        try {
            final Agent agent = new Agent(new DeviceProfilePersistorPlainText(Webapp.getFileSEP().getPath()));
            KeyAttributesMap attributes = new KeyAttributesMap();
            attributes.put("Ionic-Embargo", Arrays.asList("2099-11-14T00:55:31.820Z"));
            String kid = agent.createKey(attributes).getFirstKey().getId();
            agent.getKey(kid);
        } catch (IonicException e) {
            if (e.getReturnCode() == 40024) {
                return "SUCCESS";
            }
            return "FAILED: " + e.getMessage();
        }
        return "FAILED: Key was allowed.";
    }
}
