package lab.square.spltcoverage.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import lab.square.spltcoverage.core.analysis.CoverageGenerator;
import lab.square.spltcoverage.core.launch.SplCoverageGeneratorLauncher;
import lab.square.spltcoverage.io.CoverageReader;
import lab.square.spltcoverage.model.FeatureSet;
import lab.square.spltcoverage.model.ProductSourceInfo;
import lab.square.spltcoverage.utils.Tools;

public class SplCoverageGeneratorLauncherTest {
	private static final int P1_A_TESTMETHOD_COUNT = 2;
	private static final int P1_B_TESTMETHOD_COUNT = 3;
	private static final int P1_TESTCLASS_COUNT = 2;

	private static final String P1_TARGET_CLASSPATH = TestConfig.CLASSPATH_SELF;
	private static final String P1_TARGET_TESTPATH1 = TestConfig.ANTENNA_PRODUCT1_TEST_A_CLASSPATH_NESTED;
	private static final String P1_TARGET_TESTPATH2 = TestConfig.ANTENNA_PRODUCT1_TEST_B_CLASSPATH_NESTED;
	
	private static final String OUTPUT_PATH = "testResources/testOutput/SplCoverageGeneratorLauncherTest/";
	
	private static final int P2_A_TESTMETHOD_COUNT = 1;
	private static final int P2_B_TESTMETHOD_COUNT = 1;
	private static final int P2_TESTCLASS_COUNT = 2;

	private static final String P2_TARGET_CLASSPATH = TestConfig.CLASSPATH_SELF;
	private static final String P2_TARGET_TESTPATH1 = TestConfig.ANTENNA_PRODUCT2_TEST_A_CLASSPATH_NESTED;
	private static final String P2_TARGET_TESTPATH2 = TestConfig.ANTENNA_PRODUCT2_TEST_B_CLASSPATH_NESTED;
	
	@Test
	public void testLaunch() {
		try {
			Tools.deleteDirectoryRecursively(new File(OUTPUT_PATH));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		List<ProductSourceInfo> productSourceInfos;
		productSourceInfos = new ArrayList<>();
		
		FeatureSet featureSet1 = new FeatureSet();
		featureSet1.setFeature("A", true);
		featureSet1.setFeature("B", true);
		ProductSourceInfo info1 = new ProductSourceInfo(P1_TARGET_CLASSPATH, Arrays.asList(new String[] {P1_TARGET_TESTPATH1, P1_TARGET_TESTPATH2}), featureSet1);
		
		FeatureSet featureSet2 = new FeatureSet();
		featureSet2.setFeature("A", false);
		featureSet2.setFeature("B", false);
		ProductSourceInfo info2 = new ProductSourceInfo(P2_TARGET_CLASSPATH, Arrays.asList(new String[] {P2_TARGET_TESTPATH1, P2_TARGET_TESTPATH2}), featureSet2);
		
		productSourceInfos.add(info1);
		productSourceInfos.add(info2);
		
		SplCoverageGeneratorLauncher.launch(OUTPUT_PATH, productSourceInfos);

		assertEquals(getNumTestClasses(new File(OUTPUT_PATH + "/product1")), P1_TESTCLASS_COUNT);
		assertEquals(getNumTestClasses(new File(OUTPUT_PATH + "/product2")), P2_TESTCLASS_COUNT);
		assertEquals(getNumTestMethods(new File(OUTPUT_PATH + "/product1/ClassATest")), P1_A_TESTMETHOD_COUNT);
		assertEquals(getNumTestMethods(new File(OUTPUT_PATH + "/product1/ClassBTest")), P1_B_TESTMETHOD_COUNT);
		assertEquals(getNumTestMethods(new File(OUTPUT_PATH + "/product2/ClassATest")), P2_A_TESTMETHOD_COUNT);
		assertEquals(getNumTestMethods(new File(OUTPUT_PATH + "/product2/ClassBTest")), P2_B_TESTMETHOD_COUNT);
		
		File readFeatureSet1 = new File(OUTPUT_PATH + "/product1/featureset.txt");
		FileReader fr = null;
		try {
			fr = new FileReader(readFeatureSet1);
			BufferedReader br = new BufferedReader(fr);
			assertTrue(featureSet1.equals(CoverageReader.makeFeatureSet(br.readLine())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File readFeatureSet2 = new File(OUTPUT_PATH + "/product2/featureset.txt");
		try {
			fr = new FileReader(readFeatureSet2);
			BufferedReader br = new BufferedReader(fr);
			assertTrue(featureSet2.equals(CoverageReader.makeFeatureSet(br.readLine())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private int getNumTestClasses(File product) {
		int classCount = 0;
		for(File klass : product.listFiles()) {
			if(klass.isDirectory()) {
				classCount++;
			}
			else {
				;
			}
		}
		
		return classCount;
	}
	
	private int getNumTestMethods(File klass) {
		int methodCount = 0;
		
		for(File method : klass.listFiles()) {
			if(method.getName().endsWith(CoverageGenerator.SUFFIX_MERGED)) {
				;
			}
			else {
				methodCount++;
			}
		}
		
		return methodCount;
	}

}
