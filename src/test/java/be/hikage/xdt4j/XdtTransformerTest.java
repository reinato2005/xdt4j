package be.hikage.xdt4j;

import be.hikage.xdt4j.util.TestUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.intellij.lang.annotations.Language;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static be.hikage.xdt4j.util.TestUtils.loadXml;

/**
 * Created by IntelliJ IDEA.
 * User: hikage
 * Date: 21/12/11
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
public class XdtTransformerTest {

    private Document baseDocument;

    @Before
    public void setUp() throws Exception {

        baseDocument = loadXml("SampleBase.xml");
    }

    @Test
    public void TestIdentityTransform() throws DocumentException, IOException, SAXException, XpathException {
        @Language("XML")
        String transformInstruction = "<configuration xmlns:xdt=\"http://schemas.microsoft.com/XML-Document-Transform\" />";
        Document transformDocument = TestUtils.loadXmlFromString(transformInstruction);


        XdtTransformer transformer = new XdtTransformer();
        Document result = transformer.transform(baseDocument, transformDocument);

        XMLAssert.assertXMLEqual(baseDocument.asXML(), result.asXML());

    }

    @Test
    public void TestRemoveTransform() throws DocumentException, IOException, SAXException, XpathException {
        @Language("XML")
        String transformInstruction = "<configuration xmlns:xdt=\"http://schemas.microsoft.com/XML-Document-Transform\">\n    <system.web xdt:Transform=\"Remove\"/>\n</configuration>";
        Document transformDocument = TestUtils.loadXmlFromString(transformInstruction);


        XdtTransformer transformer = new XdtTransformer();
        Document result = transformer.transform(baseDocument, transformDocument);

        XMLAssert.assertXpathEvaluatesTo("3", "count(/configuration/*)", result.asXML());


    }

    @Test
    public void TestSetAttributesTransform() throws DocumentException, IOException, SAXException, XpathException {
        @Language("XML")
        String transformInstruction = "<configuration xmlns:xdt=\"http://schemas.microsoft.com/XML-Document-Transform\">\n    <system.web>\n        <compilation debug=\"false\" xdt:Transform=\"SetAttributes\"/>\n    </system.web>\n</configuration>";
        Document transformDocument = TestUtils.loadXmlFromString(transformInstruction);

        XdtTransformer transformer = new XdtTransformer();
        Document result = transformer.transform(baseDocument, transformDocument);

        XMLAssert.assertXpathEvaluatesTo("false", "/configuration/system.web/compilation/@debug", result.asXML());


    }


    @Test
    public void TestInsertTransform() throws DocumentException, IOException, SAXException, XpathException {
        @Language("XML")
        String transformInstruction = "<configuration xmlns:xdt=\"http://schemas.microsoft.com/XML-Document-Transform\">\n    <appSettings>\n        <add key=\"key4\" value=\"value4\" xdt:Transform=\"Insert\"/>\n    </appSettings>\n</configuration>";
        Document transformDocument = TestUtils.loadXmlFromString(transformInstruction);


        XdtTransformer transformer = new XdtTransformer();
        Document result = transformer.transform(baseDocument, transformDocument);

        XMLAssert.assertXpathEvaluatesTo("4", "count(/configuration/appSettings/add)", result.asXML());
        XMLAssert.assertXpathEvaluatesTo("value4", "/configuration/appSettings/add[@key=\"key1\"]/@value", result.asXML());


    }


}
