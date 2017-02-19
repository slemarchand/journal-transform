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
		
		test(new String[]{
				"alter-locales",
				getOutputDirectory("alter-locales"),
				"--copy-locale=fr_FR<-en_US,ru_RU<-en_US",
				"--remove-locale=en_US",
				"--default-locale=fr_FR"
				},"alter-locales");
	}
	
	String getOutputDirectory(String dataSetName) {
		return new File(baseDirectory, dataSetName + "/output").getAbsolutePath();
	}
	
	void test(String[] args, String dataSetName) throws IOException {
		
		System.out.println(baseDirectory);
		
		// Prepare files
		
		File outputDirectory = new File(getOutputDirectory(dataSetName));
		
		if(outputDirectory.exists()) {
			FileUtils.deleteDirectory(outputDirectory);
		}
		
		File inputDirectory = new File(baseDirectory, dataSetName + "/input");
		
		FileUtils.copyDirectory(inputDirectory, outputDirectory);
		
		// Execute
		
		Main.main(args);
		
		// Check !

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
