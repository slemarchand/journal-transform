package com.slemarchand.journaltransform.locales;

import com.slemarchand.journaltransform.util.xml.XmlException;
import com.slemarchand.journaltransform.util.xml.XmlHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AlterLocales {

	protected static class LocaleCopy {
		
		private Locale source;
		
		private Locale target;
		
		public LocaleCopy(Locale source, Locale target) {
			super();
			this.source = source;
			this.target = target;
		}
		
		public Locale getSource() {
			return source;
		}
		public void setSource(Locale source) {
			this.source = source;
		}
		public Locale getTarget() {
			return target;
		}
		public void setTarget(Locale target) {
			this.target = target;
		}
	}
	
	private List<LocaleCopy> toCopy = new LinkedList<LocaleCopy>();

	private List<Locale> toRemove = new LinkedList<Locale>();

	private Locale defaultLocale;

	private OutputStream contentOutput;

	private XmlHelper xml;

	private Document document;

	public AlterLocales(XmlHelper xml) {
		super();
		this.xml = xml;
	}


	public AlterLocales contentInput(InputStream contentInput) throws XmlException {

		document = xml.parse(contentInput);
		
		return this;
	}

	public AlterLocales contentInput(File contentInput) throws XmlException, FileNotFoundException {

		return contentInput(new FileInputStream(contentInput));
	}

	public AlterLocales contentOutput(OutputStream contentOutput) {

		this.contentOutput = contentOutput;

		return this;
	}

	public AlterLocales contentOutput(File contentOutput)
			throws FileNotFoundException {

		return contentOutput(new FileOutputStream(contentOutput));
	}

	public AlterLocales copyLocale(Locale sourceLocale,
			Locale targetLocale) {

		this.toCopy.add(new LocaleCopy(sourceLocale, targetLocale));

		return this;
	}

	public AlterLocales removeLocale(Locale locale) {

		this.toRemove.add(locale);

		return this;
	}

	public AlterLocales defaultLocale(Locale defaultLocale) {

		this.defaultLocale = defaultLocale;

		return this;
	}

	public void execute() throws XmlException, IOException {

		// Copy

		for (LocaleCopy e : toCopy) {
			Locale source = e.getSource();
			Locale target = e.getTarget();
			copyLocaleIntoDocument(source, target);
		}

		// Remove

		for (Locale locale : toRemove) {
			removeLocaleFromDocument(locale);
		}

		// Default locale

		Attr defaultLocaleAttr = (Attr) xml.selectSingleNode(document,
				"root/@default-locale");

		if (defaultLocale == null) {

			defaultLocale = makeLocale(defaultLocaleAttr.getValue());

			List<Locale> availableLocales = getAvailableLocales();

			if (!availableLocales.contains(defaultLocale)) {
				defaultLocale = availableLocales.get(0);
			}
		}

		defaultLocaleAttr.setValue(defaultLocale.toString());

		document.setXmlStandalone(true);

		xml.write(document, contentOutput);
		
		contentOutput.flush();
		contentOutput.close();
	}

	private void removeLocaleFromDocument(Locale locale) throws XmlException {

		NodeList contentNodes = fetchContentNodesByLocale(locale);

		for (int i = 0; i < contentNodes.getLength(); i++) {
			Element contentNode = (Element) contentNodes.item(i);
			contentNode.getParentNode().removeChild(contentNode);
		}

		List<Locale> availableLocales = getAvailableLocales();

		availableLocales.remove(locale);

		setAvailableLocales(availableLocales);
	}

	protected void copyLocaleIntoDocument(Locale sourceLocale,
			Locale targetLocale) throws XmlException {

		NodeList contentNodes = fetchContentNodesByLocale(sourceLocale);

		for (int i = 0; i < contentNodes.getLength(); i++) {
			Element sourceContentNode = (Element) contentNodes
					.item(i);

			Node parent = sourceContentNode.getParentNode();

			Element targetContentNode = (Element) xml.selectSingleNode(
					document, "dynamic-content[@language-id='" + targetLocale
							+ "']");

			if (targetContentNode == null) {

				targetContentNode = (Element) sourceContentNode
						.cloneNode(true);

				parent.appendChild(targetContentNode);
			}

			targetContentNode.setAttribute("language-id",
					targetLocale.toString());
		}

		List<Locale> availableLocales = getAvailableLocales();

		if (!availableLocales.contains(targetLocale)) {
			availableLocales.add(targetLocale);
			setAvailableLocales(availableLocales);
		}
	}

	protected Attr getAvailablesLocalesAttr() throws XmlException {
		return (Attr) xml.selectSingleNode(document, "root/@available-locales");
	}

	protected List<Locale> getAvailableLocales() throws XmlException
			 {

		Attr availablesLocalesAttr = getAvailablesLocalesAttr();

		String[] availablesLocalesStringArray = availablesLocalesAttr
				.getValue().split(",");

		List<Locale> availablesLocales = new LinkedList<Locale>();

		for (String localeAsString : availablesLocalesStringArray) {

			Locale locale = makeLocale(localeAsString);

			availablesLocales.add(locale);
		}

		return availablesLocales;
	}

	protected void setAvailableLocales(List<Locale> availableLocales) throws XmlException  {

		Attr availablesLocalesAttr = getAvailablesLocalesAttr();

		StringBuilder availableLocalesBuffer = new StringBuilder();

		for (Locale someAvailableLocale : availableLocales) {
			availableLocalesBuffer.append(someAvailableLocale).append(',');
		}

		availableLocalesBuffer.setLength(availableLocalesBuffer.length() - 1);

		availablesLocalesAttr.setValue(availableLocalesBuffer.toString());
	}

	protected NodeList fetchContentNodesByLocale(Locale locale) throws XmlException {

		NodeList dynamicContentNodes = xml.selectNodes(document,
				"//*[@language-id='" + locale + "']");

		return dynamicContentNodes;
	}
	
	protected Locale makeLocale(String localeAsString) {
		
		Locale locale;
		
		String[] localeParts = localeAsString.split("_");

		if (localeParts.length < 2) {
			locale = new Locale(localeParts[0]);
		} else {
			locale = new Locale(localeParts[0], localeParts[1]);
		}
		return locale;
	}
}
