package lab.square.spltcoverage.test.antenna;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import lab.square.spltcoverage.core.antenna.FeatureExpressionParser;
import lab.square.spltcoverage.core.antenna.FeatureLocator;
import lab.square.spltcoverage.model.FeatureSet;
import lab.square.spltcoverage.model.antenna.FeatureLocation;
import lab.square.spltcoverage.test.TestConfig;

public class FeatureLocatorTest {
	
	private static final String NESTED_FEATURE_LOCATION_SOURCE_PATH = TestConfig.ANTENNA_SOURCE_CLASS_A_1;
	
	Map<Integer, String> expectedFeatureLocation;
	
	@Before
	public void setUp() {
		expectedFeatureLocation = new HashMap<>();
		
		expectedFeatureLocation.put(9, "A");
		expectedFeatureLocation.put(11, "!(A)");
		expectedFeatureLocation.put(17, "B");
		expectedFeatureLocation.put(19, "!(B)");
	}
	@Test
	public void testGetFeatureExpressionAtLineOfFeatureLocations() {
		Collection<FeatureLocation> featureLocations = FeatureLocator.analyze(NESTED_FEATURE_LOCATION_SOURCE_PATH);
		
		String featureExpressionAt13 = FeatureLocation.getFeatureExpressionAtLineOfFeatureLocations(13, featureLocations);
		
		FeatureSet hasA = new FeatureSet();
		hasA.addFeature("A");
		
		FeatureSet hasAAndC = new FeatureSet();
		hasAAndC.addFeature("A");
		hasAAndC.addFeature("C");
		
		FeatureSet hasC = new FeatureSet();
		hasC.addFeature("C");
		
		assertFalse(FeatureExpressionParser.evaluate(featureExpressionAt13, hasA));
		assertFalse(FeatureExpressionParser.evaluate(featureExpressionAt13, hasAAndC));
		assertTrue(FeatureExpressionParser.evaluate(featureExpressionAt13, hasC));
	}
	
	private boolean verify(int startLine, String featureExpression) {
		if(!expectedFeatureLocation.containsKey(startLine))
			return false;
		
		if(!expectedFeatureLocation.get(startLine).equals(featureExpression))
			return false;
		
		expectedFeatureLocation.put(startLine, null);
		
		return true;
	}
}
