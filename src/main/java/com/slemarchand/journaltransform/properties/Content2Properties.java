package com.slemarchand.journaltransform.properties;

import com.slemarchand.journaltransform.util.xml.XmlException;
import com.slemarchand.journaltransform.util.xml.XmlHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Content2Properties {
	
	public static interface Property {
		
		public String getFieldName();
		
		public String getFieldType();
		
		public String getKey();
		
		public String getValue();
		
		public Locale getLocale();
	}
	
	public static interface PropertyFilter {
		
		boolean accept(Property context);
	}

	private String keyPrefix = "";

	private InputStream content;
	
	private PropertyFilter filter;

	private Map<String, Boolean> multiples;

	private Map<Locale, Properties> propertiesByLocale;
	
	private XmlHelper xml;
	
	public Content2Properties() {
		this(new XmlHelper());
	}
	
	public Content2Properties(XmlHelper xmlHelper) {
		super();
		this.xml = xmlHelper;
	}

	public Content2Properties keyPrefix(String keyPrefix) {

		this.keyPrefix = keyPrefix;

		return this;
	}

	public Content2Properties propertiesByLocale(
			Map<Locale, Properties> propertiesByLocale) {

		this.propertiesByLocale = propertiesByLocale;

		return this;
	}

	public Content2Properties content(InputStream article) {

		this.content = article;

		return this;
	}

	public Content2Properties content(File file)
			throws FileNotFoundException {

		this.content = new FileInputStream(file);

		return this;
	}
	
	public Content2Properties filter(PropertyFilter filter) {

		if(filter == null) {
			throw new IllegalArgumentException("filter cannot be null");
		}
		
		this.filter = filter;

		return this;
	} 

	public void execute() throws XmlException {

		multiples = new HashMap<String, Boolean>();

		Document document = xml.parse(content);

		NodeList nodes = xml.selectNodes(document, "//dynamic-element");

		for (int i = 0; i < nodes.getLength(); i++) {
			
			Element dynamicElement = (Element) nodes.item(i);
			
			processDynamicElement(dynamicElement);
		}
	}

	protected void processDynamicElement(Element dynamicElement) throws XmlException
{
		
		String name = dynamicElement.getAttribute("name");
		
		String type = dynamicElement.getAttribute("type");
	
		String key = getKey(dynamicElement);
		
		PropertyImpl propertyContext = makePropertyImpl();
		propertyContext.setFieldName(name);
		propertyContext.setFieldType(type);
		propertyContext.setKey(key);
		
		NodeList dynamicContentNodes = xml.selectNodes(dynamicElement,
				"dynamic-content");
		
		for (int j = 0; j < dynamicContentNodes.getLength(); j++) {
			
			Element dynamicContent = (Element) dynamicContentNodes.item(j);
			
			processDynamicContent(dynamicContent, propertyContext);
		}
	}

	protected void processDynamicContent(Element dynamicContent, PropertyImpl propertyContext) {
	
		String key = propertyContext.getKey();
		
		String value = dynamicContent.getTextContent();
		
		Locale locale = new Locale(
				dynamicContent.getAttribute("language-id"));

		propertyContext.setKey(key);
		propertyContext.setValue(value);
		propertyContext.setLocale(locale);
		
		if(filter == null || filter.accept(propertyContext)) {
			
			Properties props = getOrCreateProperties(locale);
			
			props.put(key, value);
		}
	}

	protected String getKey(Element dynamicElement) throws XmlException {

		int index = Integer.parseInt(dynamicElement.getAttribute("index"));

		StringBuilder key = new StringBuilder();

		Node parent = dynamicElement.getParentNode();

		if (parent != null && parent instanceof Element
				&& parent.getNodeName().equals("dynamic-element")) {

			String parentKey = getKey((Element) parent);

			key.append(parentKey);
			key.append('.');
		} else {
			key.append(keyPrefix);
		}

		String name = dynamicElement.getAttribute("name");

		if (name != null && name.trim().length() > 0) {

			key.append(name);

			Boolean multiple = multiples.get(key);

			if (multiple == null) {
				multiple = index > 0
						|| (xml.selectNodes(
								(Element) dynamicElement.getParentNode(),
								"child::*[@name='" + name + "']").getLength() > 1);
			}

			if (multiple) {
				key.append('[');
				key.append(index);
				key.append(']');
			}
		}

		return key.toString();
	}

	protected Properties getOrCreateProperties(Locale locale) {

		Properties props = this.propertiesByLocale.get(locale);

		if (props == null) {
			props = new Properties();
			this.propertiesByLocale.put(locale, props);
		}

		return props;
	}
	
	protected PropertyImpl makePropertyImpl() {
		return new PropertyImpl();
	}
	
	protected static class PropertyImpl implements Property {
		
		private String fieldName;
		private String fieldType;
		private String key;
		private String value;
		private Locale locale;
		
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getFieldType() {
			return fieldType;
		}
		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public Locale getLocale() {
			return locale;
		}
		public void setLocale(Locale locale) {
			this.locale = locale;
		}
	}
}
