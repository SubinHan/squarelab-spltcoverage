package lab.square.spltcoverage.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import lab.square.spltcoverage.core.analysis.ProductLinker;
import lab.square.spltcoverage.io.AbstractSplCoverageReader;
import lab.square.spltcoverage.io.SplCoverageReader;
import lab.square.spltcoverage.io.SplCoverageReaderFactory;
import lab.square.spltcoverage.model.FeatureSet;
import lab.square.spltcoverage.model.ProductCoverage;
import lab.square.spltcoverage.model.ProductNode;
import lab.square.spltcoverage.model.SplCoverage;

public class ProductNodeTest {

	private static final String JAVA_SOURCE_PATH_1 = TestConfig.ANTENNA_PRODUCT1_PATH + "/src";
	private static final String CLASSPATH_1 = TestConfig.ANTENNA_PRODUCT1_PATH + "/bin";

	private static final String JAVA_SOURCE_PATH_2 = TestConfig.ANTENNA_PRODUCT2_PATH + "/src";
	private static final String CLASSPATH_2 = TestConfig.ANTENNA_PRODUCT2_PATH + "/bin";

	private static final String EXECPATH = TestConfig.ANTENNA_SPL_COVERAGE_PATH;

	private static Collection<ProductNode> roots;
	private static SplCoverage splCoverage;

	@Before
	public void setUp() {
		AbstractSplCoverageReader reader = SplCoverageReaderFactory.createAntennaSplCoverageReader(
				new String[] { CLASSPATH_1, CLASSPATH_2 }, new String[] { JAVA_SOURCE_PATH_1, JAVA_SOURCE_PATH_2 });
		splCoverage = reader.readSplCoverage(EXECPATH);

		Collection<FeatureSet> featureSets = new ArrayList<>();

		for (ProductCoverage each : splCoverage.getProductCoverages()) {
			featureSets.add(each.getFeatureSet());
		}

		roots = ProductLinker.link(featureSets);
	}

	@Test
	public void testFillCoverage() {
		for (ProductNode root : roots) {
			assertNull(root.getProductCoverage());
		}

		for (ProductNode root : roots) {
			root.fillCoverageRecursivelyIfEmpty(splCoverage);
		}

		for (ProductNode root : roots) {
			assertNotNull(root.getProductCoverage());
		}
	}
}
