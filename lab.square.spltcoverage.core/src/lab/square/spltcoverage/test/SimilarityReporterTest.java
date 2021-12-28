package lab.square.spltcoverage.test;

import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import lab.square.spltcoverage.io.FeatureSetGroupReader;
import lab.square.spltcoverage.report.SimilarityReporter;

public class SimilarityReporterTest {
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testReport() {
		FeatureSetGroupReader reader = new FeatureSetGroupReader("D:\\workspacechallenege\\challenge-master\\workspace_IncLing\\Tools\\All_valid_conf\\chess\\products");
		
		Collection<Map<String, Boolean>> products = reader.readAll();
		
		SimilarityReporter reporter = new SimilarityReporter(products);
		
		reporter.generateReport();
	}

}
