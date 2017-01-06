package com.slemarchand.journaltransform.locales;

import com.slemarchand.journaltransform.BaseTest;
import com.slemarchand.journaltransform.locales.AlterLocales;
import com.slemarchand.journaltransform.test.util.TestUtil;
import com.slemarchand.journaltransform.util.xml.XmlHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class AlterLocaleTest extends BaseTest {
	
	@Test
	public void testExecute() throws Exception {
		
		InputStream is = getResourceAsStream("/al_input.xml");
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		AlterLocales acl = new AlterLocales(new XmlHelper());

		acl
		.contentInput(is)
		.contentOutput(os)
		.copyLocale(Locale.US, Locale.FRANCE)
		.copyLocale(Locale.US, new Locale("ru","RU"))
		.removeLocale(Locale.US)
		.execute();
		
		String actualOutput = os.toString("UTF8");
		
		actualOutput = TestUtil.formatXml(actualOutput);
		
		String expectedOutput = IOUtils.toString(
				getResourceAsStream("/al_expected_output.xml"), "UTF-8");

		expectedOutput = TestUtil.formatXml(expectedOutput);
		
		System.out.println(expectedOutput);
		
		System.out.println(actualOutput);
		
		Assert.assertEquals(expectedOutput, actualOutput);
	}
	

}
