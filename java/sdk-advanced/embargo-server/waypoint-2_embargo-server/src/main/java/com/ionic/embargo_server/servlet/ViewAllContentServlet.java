/**
* (c) 2020-2021 Ionic Security Inc.  All rights reserved.
* By using this code, I agree to the Privacy Policy (https://www.ionic.com/privacy-notice/),
* and the License Agreement (https://dev.ionic.com/license).
*/

package com.ionic.embargo_server.servlet;

import com.ionic.embargo_server.common.Webapp;
import com.ionic.sdk.core.date.DateTime;
import com.ionic.sdk.core.io.Stream;
import com.ionic.sdk.core.res.Resource;
import com.ionic.sdk.error.IonicException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewAllContentServlet extends HttpServlet {

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
        final byte[] html = Stream.read(Resource.resolve("html/ViewAllEmbargoContent.html"));
        final byte[] htmlContent = addContent(html);

        // Populate response.
        response.setStatus(HttpURLConnection.HTTP_OK);
        response.setContentType("text/html; charset=utf-8");
        response.getOutputStream().write(htmlContent);
    }

    private byte[] addContent(final byte[] html) throws IOException {
        Logger logger = Logger.getLogger(getClass().getName());
        try {
            // Load HTML template document into DOM and insert table.
            final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            final Document document = builder.parse(new ByteArrayInputStream(html));

            // Find insert point.
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final Element elementContent = (Element) xpath.evaluate("//*[@id = 'content']", document, XPathConstants.NODE);

            // Insert table.
            final Element table = addChild(elementContent, "table", null);
            final Element thead = addChild(table, "thead", null);
            final Element trhead = addChild(thead, "tr", null);
            addChild(trhead, "th", "File");
            addChild(trhead, "th", "Size");
            addChild(trhead, "th", "Upload Date");
            addChild(trhead, "th", "Embargo Date");
            addChild(trhead, "th", "Is Embargoed");
            final Element tbody = addChild(table, "tbody", null);

            // Iterate through registered content, one table row for each.
            final File fileEmbargoMetadata = Webapp.getFileEmbargoMetadata();
            final Properties propertiesEmbargoData = new Properties();
            if (fileEmbargoMetadata.exists()) {
                propertiesEmbargoData.load(new ByteArrayInputStream(Stream.read(fileEmbargoMetadata)));
            }
            final Date now = new Date();
            final TreeSet<String> resourceNames = new TreeSet<String>(propertiesEmbargoData.stringPropertyNames());

            // Iterate through the registered files in the embargo list and verify content exists..
            for (String resourceName : resourceNames) {
                final String resourceValue = propertiesEmbargoData.getProperty(resourceName);
                final Matcher matcher = Pattern.compile("\\[(.+?)\\]\\[(.+?)\\]").matcher(resourceValue);
                if (matcher.matches()) {
                    final Element tr = addChild(tbody, "tr", null);
                    final File fileResource = new File(Webapp.getFolderEmbargoContent(), new File(resourceName).getName());

                    // If content (file) exists, display it.
                    if (fileResource.exists()) {
                        final Element td = addChild(tr, "td", null);
                        final Element a = addChild(td, "a", fileResource.getName());
                        a.setAttribute("href", resourceName);
                        addChild(tr, "td", Long.toString(fileResource.length()));
                        addChild(tr, "td", Webapp.toString(new Date(fileResource.lastModified())));

                        // Display content upload time.
                        try {
                            addChild(tr, "td", Webapp.toString(Webapp.toDate(matcher.group(2))));
                        } catch (IonicException e) {
                            addChild(tr, "td", "?");
                        }

                        // Display seconds the content until released from embargo.
                        try {
                            final Date embargoDate = Webapp.toDate(matcher.group(2));
                            long milliseconds = embargoDate.getTime() - now.getTime();
                            addChild(tr, "td", (milliseconds > 0) ?
                                    String.format("%d seconds", milliseconds / DateTime.ONE_SECOND_MILLIS) : "-");
                        } catch (IonicException e) {
                            addChild(tr, "td", "?");
                        }
                    }
                }
            }

            // Convert DOM back into byte stream.
            final DocumentType documentType = document.getDoctype();
            final Source source = new DOMSource(document);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final Result result = new StreamResult(bos);
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");  // i18n internal
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");  // i18n internal
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");  // i18n internal
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // i18n internal
            transformer.transform(source, result);
            return bos.toByteArray();
        } catch (ParserConfigurationException | SAXException | XPathExpressionException | TransformerException e) {
            throw new IOException(e);
        }
    }

    private Element addChild(final Element parent, final String childName, final String childText) {
        final Document document = parent.getOwnerDocument();
        final Element child = (Element) parent.appendChild(document.createElement(childName));
        if (childText != null) {
            child.setTextContent(childText);
        }
        return child;
    }
}
