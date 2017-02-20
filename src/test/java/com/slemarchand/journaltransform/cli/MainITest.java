package com.slemarchand.journaltransform.cli;

import static org.junit.Assert.*;

import com.slemarchand.journaltransform.test.util.TestUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;
import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainITest {
	
	private static File baseDirectory; 
	
	@BeforeClass
	public static void before() {
		
		String baseDirResourcePath = MainITest.class.getPackage().getName()
				.replaceAll("\\.", "/")
				+ "/main-integration-test";
		
		baseDirectory = new File( MainITest.class.getClassLoader()
				.getResource(baseDirResourcePath).getFile());
	}
	
	@Test
	public void testAlterLocales() throws IOException {
		
		String dataSetName = "alter-locales";
		
		copyInputDirectoryToOutputDirectory(dataSetName);
		
		test(new String[]{
				"alter-locales",
				getOutputPath(dataSetName),
				"--copy-locale=fr_FR<-en_US,ru_RU<-en_US",
				"--remove-locale=en_US",
				"--default-locale=fr_FR"
				}, dataSetName);
	}
	
	@Test
	public void testContent2Properties() throws IOException {
		
		String dataSetName = "content2properties";
		
		cleanOutputDirectory(dataSetName);
		
		test(new String[]{
				"content2properties",
				getInputPath(dataSetName),
				getOutputPath(dataSetName)
				}, dataSetName);
	}
	
	@Test
	public void testProperties2Content() throws IOException {
		
		String dataSetName = "properties2content";
		
		copyInputDirectoryToOutputDirectory(dataSetName);
		
		test(new String[]{
				"properties2content",
				getInputPath(dataSetName),
				getOutputPath(dataSetName),
				},"properties2content");
	}
	
	private void test(String[] args, String dataSetName) throws IOException {
		
		System.out.println(baseDirectory);
		
		// Prepare files


		// Execute
		
		Main.main(args);
		
		// Check !

		File outputDirectory = getOutputDirectory(dataSetName);
		
		File expectedDirectory = new File(baseDirectory, dataSetName + "/expected");
		
		final Path outputDirectoryPath = Paths.get(outputDirectory.toURI());
		
		final Path expectedDirectoryPath = Paths.get(expectedDirectory.toURI());
		
		Files.walkFileTree(expectedDirectoryPath, new SimpleFileVisitor<Path>() {
	        
			@Override
	        public FileVisitResult visitFile(Path expectedFilePath,
	                BasicFileAttributes attrs)
	                throws IOException {
	        	
	            FileVisitResult result = super.visitFile(expectedFilePath, attrs);

	            checkOutputFile(expectedDirectoryPath, expectedFilePath, outputDirectoryPath);
	            
	            return result;
	        }


	    });
	}

	private void checkOutputFile(Path expectedDirectoryPath, Path expectedFilePath, Path outputDirectoryPath) throws IOException {
        
		// get the relative file name from path "expectedDirectoryPath"
        Path relativize = expectedDirectoryPath.relativize(expectedFilePath);
        
        // construct the path for the counterpart file in "outputDirectoryPath"
        Path actualFilePath = outputDirectoryPath.resolve(relativize);
     
        File expectedFile = expectedFilePath.toFile();
        File actualFile = actualFilePath.toFile();
        
        if(expectedFile.isFile()) {
        	
        	System.out.println("Testing " + expectedFile);
        	
        	assertTrue("The following file must exist: " +  actualFile, actualFile.exists());
        	
			if (actualFile.getName().endsWith(".xml")) {

				assertEquals(
						"The content for following file must be as expected: "
								+ actualFile,
						toFormattedXml(expectedFile),
						toFormattedXml(actualFile));

			} else if(actualFile.getName().endsWith(".properties")) {
			
				assertEquals(
						"The content for following file must be as expected: "
								+ actualFile,
						TestUtil.formatProperties(expectedFile),
						TestUtil.formatProperties(actualFile));
			} else {

				assertEquals(
						"The content for following file must be as expected: "
								+ actualFile, FileUtils
								.readFileToString(expectedFile,
										"UTF8"), FileUtils
								.readFileToString(actualFile,
										"UTF8"));
			}
        }
	}
	
	private File getInputDirectory(String dataSetName) {
		return new File(baseDirectory, dataSetName + "/input");
	}
	
	private String getInputPath(String dataSetName) {
		return getInputDirectory(dataSetName).getAbsolutePath();
	}

	private File getOutputDirectory(String dataSetName) {
		return new File(baseDirectory, dataSetName + "/output");
	}
	
	private String getOutputPath(String dataSetName) {
		return getOutputDirectory(dataSetName).getAbsolutePath();
	}
	
	private void copyInputDirectoryToOutputDirectory(String dataSetName)
			throws IOException {
		
		File outputDirectory = getOutputDirectory(dataSetName);
				
		File inputDirectory = getInputDirectory(dataSetName);
		
		cleanOutputDirectory(dataSetName);
		
		FileUtils.copyDirectory(inputDirectory, outputDirectory);
	}

	private void cleanOutputDirectory(String dataSetName) throws IOException {
		File outputDirectory = getOutputDirectory(dataSetName);
		
		if(outputDirectory.exists()) {
			FileUtils.deleteDirectory(outputDirectory);
		}
	}
	
	private String toFormattedXml(File file) throws IOException {
		String xml = FileUtils.readFileToString(file, "UTF8");
		try {
			xml = TestUtil.formatXml(xml);
		} catch (JDOMException e) {
			new IOException(e);
		}
		return xml;
	}
}
