package lab.square.spltcoverage.core.model;


import java.util.Collection;
import java.util.HashSet;

import org.jacoco.core.analysis.IClassCoverage;

/**
 * The TestCaseCoverage class is a class containing the coverage data of a test case that had run.
 * It has the TestMethodCoverages.
 * @author SQUARELAB
 *
 */
public class TestCaseCoverage {
	private Collection<IClassCoverage> classCoverages;
	private Collection<TestMethodCoverage> testMethodCoverages;
	private String testCaseName;
	private Class[] targetClasses;
	
	/**
	 * Create an empty TestCaseCoverage.
	 * @param testCaseName
	 */
	public TestCaseCoverage(String testCaseName) {
		testMethodCoverages = new HashSet<TestMethodCoverage>();
		classCoverages = new HashSet<IClassCoverage>();
		this.testCaseName = testCaseName;
	}
	
	/**
	 * Create an empty TestCaseCoverage and set the targetClasses.
	 * @param testCaseName		The name of the test case to identify.
	 * @param targetClasses		The target class is used when decide equality.
	 * 							If the target class is not null,
	 * 							equals() checks if the target classes' coverage is the same only. 
	 */
	public TestCaseCoverage(String testCaseName, Class... targetClasses) {
		this(testCaseName);
		this.targetClasses = targetClasses;
	}
	
	/**
	 * Get the TestMethodCoverage of the given test method name.
	 * @param testMethodName
	 * @return
	 */
	public TestMethodCoverage getTestMethodCoverage(String testMethodName) {
		for(TestMethodCoverage tmc : testMethodCoverages) {
			if(tmc.getMethodName().equals(testMethodName))
				return tmc;
		}
		return null;
	}
	
	/**
	 * Get all the TestMethodCoverages.
	 * @return
	 */
	public Collection<TestMethodCoverage> getTestMethodCoverages(){
		return testMethodCoverages;
	}
	
	/**
	 * Get the test case name.
	 * @return
	 */
	public String getTestCaseName() {
		return testCaseName;
	}
	
	/**
	 * Add the TestMethodCoverage.
	 * @param testMethodCoverage
	 */
	public void addTestMethodCoverage(TestMethodCoverage testMethodCoverage) {
		this.testMethodCoverages.add(testMethodCoverage);
	}

	/**
	 * Add the IClassCoveerages.
	 * It must be derived by the test methods' coverage containing.
	 * @param classCoverages
	 */
	public void addClassCoverages(Collection<IClassCoverage> classCoverages) {
		this.classCoverages.addAll(classCoverages);
	}
	
	/**
	 * Get the target classes.
	 * 
	 * @return
	 */
	public Class[] getTargetClasses() {
		return targetClasses;
	}

	/**
	 * Set target classes.
	 * The target class is used when decide equality.
	 * If the target class is not null,
	 * equals() checks if the target classes' coverage is the same only. 
	 * 
	 * @param targetClasses
	 */
	public void setTargetClasses(Class[] targetClasses) {
		this.targetClasses = targetClasses;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for(TestMethodCoverage tmc : testMethodCoverages) {
			hash += tmc.hashCode();
		}
		hash += testCaseName.hashCode();
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) throws IllegalStateException {
		if(targetClasses == null)
			throw new IllegalStateException("Cannot decide equailty. Set target classes before call equals().");
		
		return equals(obj, targetClasses);
	}
	
	/**
	 * Checks equality within only given classes.
	 * You can use also setTargetClasses() and equals().
	 * @param obj
	 * @param classes
	 * @return
	 */
	public boolean equals(Object obj, Class... classes) {
		TestCaseCoverage compareTo;
		if (!(obj instanceof TestCaseCoverage))
			return false;

		compareTo = (TestCaseCoverage) obj;
		
		if(!testCaseName.equals(compareTo.testCaseName))
			return false;
		
		for(TestMethodCoverage tmc : testMethodCoverages) {
			if(!tmc.equals(compareTo.getTestMethodCoverage(tmc.getMethodName()), classes))
				return false;
		}
		
		return true;
	}

	public int getScore() {
		int score = 0;
		
		for(TestMethodCoverage tmc : testMethodCoverages) {
			score += tmc.getScore();
		}
		
		return score;
	}
	
}
