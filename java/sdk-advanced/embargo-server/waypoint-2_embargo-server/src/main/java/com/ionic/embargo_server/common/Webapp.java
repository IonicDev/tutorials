/**
* (c) 2020-2021 Ionic Security Inc.  All rights reserved.
* By using this code, I agree to the Privacy Policy (https://www.ionic.com/privacy-notice/),
* and the License Agreement (https://dev.ionic.com/license).
*/

package com.ionic.embargo_server.common;

import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.error.SdkError;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Webapp file management utility functions.
 */
public class Webapp {

    public static File getFileSEP() {
        final File folderCatalinaBase = new File(System.getProperty("catalina.base"));
        final File folderRepo = new File(folderCatalinaBase, "../..");
        return new File(folderRepo, "sep.json");
    }

    public static File getFileEmbargoMetadata() {
        final File folderCatalinaBase = new File(System.getProperty("catalina.base"));
        final File folderRepo = new File(folderCatalinaBase, "../..");
        return new File(folderRepo, "embargo.properties");
    }

    public static File getFolderEmbargoContent() {
        final File folderCatalinaBase = new File(System.getProperty("catalina.base"));
        return new File(folderCatalinaBase, "../../ionic");
    }

    public static String getEmbargoURI(final String filename) {
        return String.format("/ionic/embargo/%s", filename);
    }

    public static String toString(final Date date) {
        // remove milliseconds
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(date);
    }

    public static Date toDate(final String dateString) throws IonicException {
        // remove milliseconds
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new IonicException(SdkError.ISAGENT_INVALIDVALUE, e);
        }
    }
}
