package lab.square.spltcoverage.model;

import java.util.Collection;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;

/**
 * The TestMethodCoverage class is a class containing the coverage data of an atomic test that had run.
 * @author SQUARELAB
 *
 */

public class TestMethodCoverage implements ICoverageModelComponent {
	private Collection<IClassCoverage> classCoverages;
	private String testMethodName;
	private Class[] targetClasses;

	
	
	/**
	 * Create a new TestMethodCoverage.
	 * @param testMethodName	The name of the test method.
	 * @param classCoverages	The coverage data about a test method.
	 */
	public TestMethodCoverage(String testMethodName, Collection<IClassCoverage> classCoverages) {
		this.classCoverages = classCoverages;
		this.testMethodName = testMethodName;
	}
	
	/**
	 * Create a new TestMethodCoverage.
	 * @param testMethodName	The name of the test method to identify.
	 * @param classCoverages	The coverage data about a test method.
	 * @param targetClasses		The target class is used when decide equality.
	 * 							If the target class is not null,
	 * 							equals() checks if the target classes' coverage is the same only. 
	 */
	public TestMethodCoverage(String testMethodName, Collection<IClassCoverage> classCoverages, Class... targetClasses) {
		this(testMethodName, classCoverages);
		this.targetClasses = targetClasses;
	}

	/**
	 * Get coverage data of the specific class.
	 * @param className
	 * @return
	 */
	public IClassCoverage getCoverage(String className) {
		for (IClassCoverage cc : classCoverages) {
			if (className.equals(cc.getName())) {
				return cc;
			}
		}
		return null;
	}

	/**
	 * Get atomic test's name.
	 * @return
	 */
	public String getName() {
		return testMethodName;
	}
	
	/**
	 * Get the class coverages of the test method.
	 * @return
	 */
	public Collection<IClassCoverage> getClassCoverages() {
		return this.classCoverages;
	}
	
	public void addClassCoverages(Collection<IClassCoverage> classCoverages) {
		this.classCoverages.addAll(classCoverages);
	}
	
	/**
	 * Set target classes.
	 * The target class is used when decide equality.
	 * If the target class is not null,
	 * equals() checks if the target classes' coverage is the same only. 
	 * 
	 * @param classes
	 */
	public void setTargetClasses(Class... classes) {
		targetClasses = classes;
	}

	@Override
	public int hashCode() {
		if(targetClasses == null)
			return super.hashCode();
		
		int hash = 0;
		final int prime = 7;
		
		for (Class klass : targetClasses) {
			for (IClassCoverage cc : classCoverages) {
				if(klass.getCanonicalName().replace(".", "/").equals(cc.getName())) {
					for(int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
						if(cc.getLine(i).getStatus() == ICounter.FULLY_COVERED)
							hash += i;
					}
					break;
				}
			}
		}
		hash *= prime;
		hash += testMethodName.hashCode();
		
		return hash;
	}

	@Override
	public boolean equals(Object obj){
		if(targetClasses == null) {	
			String[] classNames = new String[classCoverages.size()];
			int i = 0;
			for(IClassCoverage cc : classCoverages) {
				classNames[i++] = cc.getName();
			}
			
			return equals(obj, classNames);
		}
		
		String[] classNames = new String[targetClasses.length];
		int i = 0;
		for(Class klass : targetClasses) {
			classNames[i++] = klass.getCanonicalName().replace(".", "/");
		}
		
		return equals(obj, classNames);
	}

	/**
	 * Checks equality within only given classes.
	 * You can use also setTargetClasses() and equals().
	 * @param obj
	 * @param classNames
	 * @return
	 */
	public boolean equals(Object obj, String... classNames) {
		TestMethodCoverage compareTo;
		if (!(obj instanceof TestMethodCoverage))
			return false;

		compareTo = (TestMethodCoverage) obj;

		if (!testMethodName.equals(compareTo.getName()))
			return false;

		for (String klass : classNames) {
			for (IClassCoverage cc : compareTo.getClassCoverages()) {
				if(klass.equals(cc.getName())) {
					for(int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
						if(cc.getLine(i).getStatus() != this.getCoverage(cc.getName()).getLine(i).getStatus())
							return false;
					}
					break;
				}
			}
		}

		return true;
	}

	/**
	 * Get the target classes.
	 * @return
	 */
	public Class[] getTargetClasses() {
		return targetClasses;
	}

	public int getScore() {
		int score = 0;
		
		for(IClassCoverage cc : classCoverages) {
			for(int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
				if(cc.getLine(i).getStatus() == ICounter.FULLY_COVERED)
					score++;
			}
		}
		
		return score;
	}
}
