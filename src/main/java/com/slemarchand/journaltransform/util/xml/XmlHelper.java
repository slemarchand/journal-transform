package com.slemarchand.journaltransform.util.xml;

import com.slemarchand.journaltransform.properties.Content2PropertiesXmlConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlHelper {

	private XPath xPath = XPathFactory.newInstance().newXPath();

	public Document parse(InputStream is) throws XmlException {

		Document document;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db;

			db = dbf.newDocumentBuilder();

			document = db.parse(is);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new XmlException(e);
		}

		return document;
	}

	public void write(Document document, OutputStream os) throws XmlException {
		try {
			DOMSource domSource = new DOMSource(document);
			StreamResult result = new StreamResult(os);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(domSource, result);
		} catch (TransformerException e) {
			throw new XmlException(e);
		}
	}

	public <T> T unmarshall(InputStream is, Class<T> clazz) throws XmlException {

		T object;

		try {
			JAXBContext context = JAXBContext
					.newInstance(Content2PropertiesXmlConfig.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			@SuppressWarnings("unchecked")
			T o = (T) unmarshaller.unmarshal(is);

			object = o;

		} catch (JAXBException e) {
			throw new XmlException(e);
		}

		return object;

	}

	public NodeList selectNodes(Object item, String expression) throws XmlException {

		try {
			
			return (NodeList) xPath.compile(expression).evaluate(item,
					XPathConstants.NODESET);
			
		} catch (XPathExpressionException e) {
			throw new XmlException(e);
		}
	}

	public Node selectSingleNode(Object item, String expression) throws XmlException {

		try {
			
			return (Node) xPath.compile(expression).evaluate(item,
					XPathConstants.NODE);
			
		} catch (XPathExpressionException e) {
			throw new XmlException(e);
		}
	}
}
