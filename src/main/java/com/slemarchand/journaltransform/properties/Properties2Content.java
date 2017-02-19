package com.slemarchand.journaltransform.properties;

import com.slemarchand.journaltransform.util.xml.XmlException;
import com.slemarchand.journaltransform.util.xml.XmlHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Properties2Content {
	
	private String keyPrefix = null;

	private InputStream contentInput;
	
	private File contentInputFile;
	
	private OutputStream contentOutputStream;
	
	private File contentOutputFile;

	private Map<Locale, Properties> propertiesByLocale;

	private XmlHelper xml;
	
	private Document document;
	
	private int updatedFieldsCount;
	
	public Properties2Content() {
		this(new XmlHelper());
	}
	
	public Properties2Content(XmlHelper xml) {
		super();
		this.xml = xml;
	}
	
	public Properties2Content propertiesByLocale(
			Map<Locale, Properties> propertiesByLocale) {

		this.propertiesByLocale = propertiesByLocale;

		return this;
	}
	
	public Properties2Content keyPrefix(String keyPrefix) {
		
		this.keyPrefix = keyPrefix;
		
		return this;
	}
	
	public Properties2Content contentInput(InputStream contentInput) {
		
		this.contentInput = contentInput;
		
		this.contentInputFile = null;
		
		return this;
	}
	
	public Properties2Content contentInput(File contentInput) throws FileNotFoundException {
		
		this.contentInputFile = contentInput;
		
		this.contentInput = new FileInputStream(contentInput);
	
		return this;
	}
	
	public Properties2Content contentOutput(OutputStream contentOutput) {
		
		this.contentOutputStream = contentOutput;
		
		this.contentOutputFile = null;
		
		return this;
	}
	
	public Properties2Content contentOutput(File contentOutput) throws FileNotFoundException {
		
		this.contentOutputFile = contentOutput;
		
		this.contentOutputStream = null;
		
		return this;
	}
	
	public int getUpdatedFieldsCount() {
		return updatedFieldsCount;
	}
	
	public void execute() throws XmlException, IOException {
		
		updatedFieldsCount = 0;
		
		document  = xml.parse(contentInput);
		contentInput.close();
		
		for (Entry<Locale, Properties> e: propertiesByLocale.entrySet()) {
			Locale locale = e.getKey();
			Properties properties = e.getValue();
			processLocaleProperties(locale, properties);
		}
		
		document.setXmlStandalone(true);
		
		ByteArrayOutputStream contentByteArrayOutputStream = null;
		
		if(contentOutputStream == null) {
			contentByteArrayOutputStream = new ByteArrayOutputStream();
			
			contentOutputStream = contentByteArrayOutputStream;
		}
		
		// Write output XML document
		xml.write(document, contentOutputStream);
		contentOutputStream.flush();
		contentOutputStream.close();
		
		System.out.println(updatedFieldsCount);
		
		if(contentOutputFile != null) {
			if(updatedFieldsCount > 0 || !contentOutputFile.equals(contentInputFile)) {
				FileOutputStream contentFileOutputStream = new FileOutputStream(contentOutputFile);
				try {
					System.out.println("Writing " + contentOutputFile + "...");
					contentFileOutputStream.write(contentByteArrayOutputStream
							.toByteArray());
					contentFileOutputStream.flush();
				} finally {
					contentFileOutputStream .close();
				}
			} 
		}
	}
	
	protected void processLocaleProperties(Locale locale, Properties properties) throws XmlException  {
		
		for (Entry<Object, Object> e: properties.entrySet()) {
			String key = e.getKey().toString();
			String value = e.getValue().toString();
			
			if(keyPrefix != null && keyPrefix.trim().length() > 0) {
				if(key.startsWith(keyPrefix)) {
					key = key.substring(keyPrefix.length());
				} else {
					continue;
				}
			}
			
			processLocaleProperty(locale, key, value);
		}
		
	}

	protected void processLocaleProperty(Locale locale, String key, String value) throws XmlException {
		
		System.out.println("Processing " + key + " [" + locale + "]");
		
		Element root = (Element) xml.selectSingleNode(document, "root");
		
		Element dynamicElement = findDynamicElement(root, key);
		
		Element dynamicContent = (Element)xml.selectSingleNode(dynamicElement,
				"dynamic-content[@language-id='" + locale.toString() + "']");
		
		if(dynamicContent == null) {
			throw new IllegalStateException("<dynamic-content language-id=\"" 
			+ locale + "\"> child is missing");
		}
		
		String oldValue = dynamicContent.getTextContent();
		
		dynamicContent.setTextContent("");
		
		CDATASection cdata = document.createCDATASection(value);
		
		dynamicContent.appendChild(cdata);
		
		if(!value.equals(oldValue)) {
			updatedFieldsCount++;
		}
	}
	
	protected Element findDynamicElement(Element ancestor, String key) throws XmlException  {
		
		String childName;
		
		String nextKey;	
			
		int firstPeriodIndex = key.indexOf('.');
		
		if(firstPeriodIndex != -1) { 
			
			childName = key.substring(0, firstPeriodIndex);
		
			nextKey = key.substring(firstPeriodIndex + 1);	
			
		} else {
			 childName = key;
			 
			 nextKey = null;
		}
		
		String index = "0";
		
		int openBracketIndex = childName.lastIndexOf('[');
		
		if(openBracketIndex != -1 && childName.endsWith("]")) {
			index = childName.substring(
					openBracketIndex + 1, childName.length() - 1);
			childName = childName.substring(0, openBracketIndex);
		}
		
		String expression = "dynamic-element[@name='" + childName + "' and @index='" + index + "']";
		
		Element child = (Element) xml.selectSingleNode(
				ancestor, expression);
		
		if(child != null) {
			
			if(nextKey == null) {
				
				return child;
				
			} else {
					
				return findDynamicElement(child, nextKey);
			
			} 
		} else {
			throw new IllegalStateException("<dynamic-element name=\""
					+ childName + "\"> is missing");
		}
	}
}
