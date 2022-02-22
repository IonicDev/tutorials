/**
* (c) 2020-2021 Ionic Security Inc.  All rights reserved.
* By using this code, I agree to the Privacy Policy (https://www.ionic.com/privacy-notice/),
* and the License Agreement (https://dev.ionic.com/license).
*/

package com.ionic.embargo_server.servlet;

import com.ionic.embargo_server.common.Webapp;
import com.ionic.sdk.core.codec.Transcoder;
import com.ionic.sdk.core.io.Stream;
import com.ionic.sdk.core.res.Resource;
import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.json.JsonIO;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;

/**
 * Handle display of HTML form used to populate Machina SEP into Embargo webapp.  Handle update of Machina SEP.
 */
public class ProfileServlet extends HttpServlet {

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
        byte[] html = Stream.read(Resource.resolve("html/SecureEnrollmentProfile.html"));

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
        // location of Secure Enrollment Profile
        final File fileSEP = Webapp.getFileSEP();

        // retrieve SEP update from HTML form
        final String downloadedSEP = request.getParameter("content");
        try {
            // ensure JSON
            JsonIO.readObject(Transcoder.utf8().decode(downloadedSEP));
            // update SEP file
            Stream.write(fileSEP, Transcoder.utf8().decode(downloadedSEP));
            // populate alert
            System.setProperty(getClass().getName(), String.format(
                    "Secure Enrollment Profile updated at %s.", new Date().toString()));
        } catch (IonicException e) {
            // populate alert
            System.setProperty(getClass().getName(), String.format(
                    "[X] Secure Enrollment Profile not updated at %s; JSON expected.", new Date().toString()));
        }

        // instruct user agent to discard HTML form POST state
        response.setHeader("Location", request.getRequestURI());
        response.setStatus(HttpURLConnection.HTTP_MOVED_TEMP);
    }
}
