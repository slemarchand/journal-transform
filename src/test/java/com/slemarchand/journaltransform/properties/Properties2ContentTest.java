package com.slemarchand.journaltransform.properties;

import com.slemarchand.journaltransform.BaseTest;
import com.slemarchand.journaltransform.test.util.TestUtil;
import com.slemarchand.journaltransform.util.xml.XmlHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class Properties2ContentTest extends BaseTest {
	
	@Test
	public void testExecute() throws Exception {
		
		InputStream is = getResourceAsStream("/p2c_input.xml");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		Map<Locale, Properties> propertiesByLocale = new HashMap<Locale, Properties>();
		
		Properties props = new Properties();
		props.load(getResourceAsStream("/p2c.properties"));
		
		propertiesByLocale.put(Locale.US, props);
		
		Properties2Content p2c = new Properties2Content(new XmlHelper());
		
		p2c.contentInput(is);
		p2c.contentOutput(os);
		p2c.propertiesByLocale(propertiesByLocale);
		
		p2c.execute();
		
		String actualOutput = os.toString("UTF8");
		
		actualOutput = TestUtil.formatXml(actualOutput);
		
		String expectedOutput = IOUtils.toString(
				getResourceAsStream("/p2c_expected_output.xml"), "UTF-8");
	
		expectedOutput = TestUtil.formatXml(expectedOutput);
		
		Assert.assertEquals(expectedOutput, actualOutput);
		
		Assert.assertTrue(p2c.getUpdatedFieldsCount() > 0);
	}
}
