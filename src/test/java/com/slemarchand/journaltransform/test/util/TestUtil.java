package com.slemarchand.journaltransform.test.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class TestUtil {
	
	public static String formatXml(String xml) throws JDOMException, IOException {
		
		System.out.println(xml);
		
        Document doc = new SAXBuilder().build(new StringReader(xml));
        
        sortAttributes(doc.getRootElement());
        
        Format format = Format.getPrettyFormat(); 
        
        XMLOutputter outputter = new XMLOutputter(format);
   
        StringWriter sw = new StringWriter();
        
		outputter.output(doc, sw);
        
		String formattedXml = sw.toString();
		
		return formattedXml;
	}

	private static void sortAttributes(Element element) {
		
		List<Attribute> attributes = new ArrayList<Attribute>(element.getAttributes());
		
		Collections.sort(attributes, new Comparator<Attribute>() {
			@Override
			public int compare(Attribute a1, Attribute a2) {
				return a1.getName().compareTo(a2.getName());
			}	
		});
		
		for (Attribute attr : attributes) {
			attr.detach();
		}
		
		element.setAttributes(attributes);
		
		List<Element> children = element.getChildren();
		
		for (Element child : children) {
			sortAttributes(child);
		}
	}
}
