package lab.square.spltcoverage.core.antenna;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;

import lab.square.spltcoverage.model.antenna.AntennaProductCoverage;
import lab.square.spltcoverage.utils.Tools;

public class AntennaCoverageAccumulator {

	private List<AntennaProductCoverage> accumulatedProductCoverages;
	private Map<String, CoverageOfSingleClass> accumulated;
	private Set<String> visitedFeatures;

	private boolean isCoverageChanged;
	private Set<String> newlyVisitedFeatures;
	private Map<String, Integer> newlyCoveredLineCount;
	private Map<String, Integer> newlyActivatedLineCount;
	private Set<String> newlyCoveredClasses;
	private Set<String> newlyActivatedClasses;
	private Map<String, CoverageOfSingleClass> diffWithPrevious;

	/**
	 * Create a new AntennaCoverageAccumulator.
	 */
	public AntennaCoverageAccumulator() {
		accumulatedProductCoverages = new ArrayList<>();
		accumulated = new HashMap<>();
		visitedFeatures = new HashSet<>();
		newlyVisitedFeatures = new HashSet<>();
		newlyCoveredLineCount = new HashMap<>();
		newlyActivatedLineCount = new HashMap<>();
		newlyCoveredClasses = new HashSet<>();
		newlyActivatedClasses = new HashSet<>();
		diffWithPrevious = new HashMap<>();
	}

	/**
	 * Returns true if the coverage changed compared with the last accumulation and
	 * the previous.
	 * 
	 * @return
	 */
	public boolean isCoverageChanged() {
		return isCoverageChanged;
	}

	/**
	 * Accumulate a new Antenna product coverage.
	 * 
	 * @param productCoverage
	 */
	public void accumulate(AntennaProductCoverage productCoverage) {
		clearPreviousStates();
		accumulatedProductCoverages.add(productCoverage);
		updateNewlyVisitedFeatures(productCoverage);

		for (IClassCoverage classCoverage : productCoverage.getClassCoverages()) {
			accumulateCoverageOfClass(classCoverage);
		}
	}

	private void clearPreviousStates() {
		newlyVisitedFeatures.clear();
		isCoverageChanged = false;
		newlyCoveredLineCount.clear();
		newlyActivatedLineCount.clear();
		newlyCoveredClasses.clear();
		newlyActivatedClasses.clear();
		diffWithPrevious.clear();
	}

	private void updateNewlyVisitedFeatures(AntennaProductCoverage newProductCoverage) {
		for (String feature : newProductCoverage.getFeatureSet().getFeatures()) {
			if (visitedFeatures.contains(feature))
				continue;

			visitedFeatures.add(feature);
			newlyVisitedFeatures.add(feature);
		}

	}

	private void accumulateCoverageOfClass(IClassCoverage classCoverage) {
		if (!classCoverage.containsCode())
			return;

		String className = Tools.convertClassNameByConvention(classCoverage.getName());
		CoverageOfSingleClass coverage = new CoverageOfSingleClass(classCoverage);

		accumulated.compute(className, (k, v) -> {
			if (v == null) {
				updateNews(className, coverage);
				return coverage;
			}

			CoverageOfSingleClass diff = coverage.subtract(v);
			updateNews(className, diff);

			return v.add(coverage);
		});
	}

	private void updateNews(String className, CoverageOfSingleClass coverage) {
		diffWithPrevious.put(className, coverage);

		if (coverage.isNotActivatedAnything())
			return;

		newlyActivatedClasses.add(className);
		newlyActivatedLineCount.put(className, coverage.getActivatedLineCount());

		if (coverage.isNotCoveredAnything())
			return;
		isCoverageChanged = true;
		newlyCoveredLineCount.put(className, coverage.getCoveredCount());
		newlyCoveredClasses.add(className);
	}

	/**
	 * Returns newly accumulated features as a collection of strings.
	 * 
	 * @return
	 */
	public Collection<String> getNewlyVisitedFeatures() {
		return newlyVisitedFeatures;
	}

	/**
	 * Returns newly covered line count of a specific class compared with the last
	 * accumulation and the previous. The class name should have the following
	 * format: "example.package.Classname".
	 * 
	 * @param classNameWithDots
	 * @return
	 */
	public int getNewlyCoveredLineCountOfClass(String classNameWithDots) {
		if (!newlyCoveredLineCount.containsKey(classNameWithDots))
			return 0;

		return newlyCoveredLineCount.get(classNameWithDots);
	}

	/**
	 * Returns newly covered lines of a specific class as a set of integers compared
	 * with the last accumulation and the previous. The class name should have the
	 * following format: "example.package.Classname".
	 * 
	 * @param classNameWithDots
	 * @return
	 */
	public Set<Integer> getNewlyCoveredLineNumbersOfClass(String classNameWithDots) {
		if (!newlyCoveredClasses.contains(classNameWithDots))
			return Collections.emptySet();

		return diffWithPrevious.get(classNameWithDots).getCoveredLineNumbers();
	}

	/**
	 * Returns newly covered classes as a collection of strings compared with the
	 * last accumulation and the previous.
	 * 
	 * @return
	 */
	public Collection<String> getNewlyCoveredClasses() {
		return newlyCoveredClasses;
	}

	/**
	 * Returns newly activated classes as a collection of strings compared with the
	 * last accumulation and the previous.
	 * 
	 * @return
	 */
	public Collection<String> getNewlyActivatedClasses() {
		return newlyActivatedClasses;
	}

	/**
	 * Returns newly activated line count of a specific class compared with the last
	 * accumulation and the previous. The class name should have the following
	 * format: "example.package.Classname".
	 * 
	 * @param classNameWithDots
	 * @return
	 */
	public int getNewlyActivatedLineCountOfClass(String classNameWithDots) {
		if (!diffWithPrevious.containsKey(classNameWithDots))
			return 0;

		return diffWithPrevious.get(classNameWithDots).getActivatedLineCount();
	}

	/**
	 * Returns total activated line count of a specific class.
	 * 
	 * @param classNameWithDots
	 * @return
	 */
	public int getTotalActivatedLineCountOfClass(String classNameWithDots) {
		CoverageOfSingleClass coverage = accumulated.get(classNameWithDots);

		return coverage.getActivatedLineCount();
	}

	/**
	 * Returns total covered line count of a specific class.
	 * 
	 * @param classNameWithDots
	 * @return
	 */
	public int getTotalCoveredLineCountOfClass(String classNameWithDots) {
		CoverageOfSingleClass coverage = accumulated.get(classNameWithDots);
		return coverage.getCoveredCount();
	}

	/**
	 * Returns total activated line count with all accumulation before.
	 * 
	 * @return
	 */
	public int getTotalActivatedLine() {
		int totalActivatedLine = 0;
		for (CoverageOfSingleClass coverage : accumulated.values()) {
			totalActivatedLine += coverage.getActivatedLineCount();
		}
		return totalActivatedLine;
	}

	/**
	 * Returns total covered line count with all accumulation before.
	 * 
	 * @return
	 */
	public int getTotalCoveredLine() {
		int totalCoveredLine = 0;
		for (CoverageOfSingleClass coverage : accumulated.values()) {
			totalCoveredLine += coverage.getCoveredCount();
		}
		return totalCoveredLine;
	}

	private class CoverageOfSingleClass {

		private int[] lines;
		int lastLine;

		public CoverageOfSingleClass(IClassCoverage classCoverage) {
			if (!isClassCoverageAvailable()) {
				lines = new int[0];
				return;
			}

			lastLine = classCoverage.getLastLine();

			lines = new int[lastLine + 1];
			initStatus(classCoverage);
		}

		public CoverageOfSingleClass(CoverageOfSingleClass classCoverage) {
			lastLine = classCoverage.lastLine;
			lines = new int[lastLine + 1];
			System.arraycopy(classCoverage.lines, 0, this.lines, 0, lastLine + 1);
		}

		public boolean isClassCoverageAvailable() {
			return lastLine != -1;
		}

		private void initStatus(IClassCoverage classCoverage) {
			initLineStatusAsEmpty();
			initEachLineStatus(classCoverage);
		}

		private void initEachLineStatus(IClassCoverage classCoverage) {
			for (int i = 0; i <= lastLine; i++) {
				lines[i] = classCoverage.getLine(i).getStatus();
			}
		}

		private void initLineStatusAsEmpty() {
			for (int i = 0; i < lines.length; i++) {
				lines[i] = ICounter.EMPTY;
			}
		}

		public CoverageOfSingleClass add(CoverageOfSingleClass other) {
			CoverageOfSingleClass bigger;
			CoverageOfSingleClass smaller;
			bigger = this;
			smaller = other;

			if (!hasMoreLineThan(other)) {
				bigger = other;
				smaller = this;
			}

			CoverageOfSingleClass result = new CoverageOfSingleClass(bigger);
			for (int i = 0; i <= smaller.lastLine; i++) {
				if (isGreaterEqualThan(bigger.lines[i], smaller.lines[i])) {
					result.lines[i] = smaller.lines[i];
				}
			}

			return result;
		}

		public CoverageOfSingleClass subtract(CoverageOfSingleClass other) {
			CoverageOfSingleClass smaller;
			smaller = other;

			if (!hasMoreLineThan(other)) {
				smaller = this;
			}

			CoverageOfSingleClass result = new CoverageOfSingleClass(this);
			for (int i = 0; i <= smaller.lastLine; i++) {
				if (isGreaterEqualThan(result.lines[i], other.lines[i])) {
					result.lines[i] = ICounter.EMPTY;
				}
			}

			return result;
		}

		private boolean hasMoreLineThan(CoverageOfSingleClass other) {
			return this.lastLine > other.lastLine;
		}

		private boolean isGreaterEqualThan(int status1, int status2) {
			if (status1 == ICounter.EMPTY)
				return true;

			if (status1 == ICounter.NOT_COVERED)
				return status2 != ICounter.EMPTY;

			if (status1 == ICounter.PARTLY_COVERED)
				return status2 == ICounter.PARTLY_COVERED || status2 == ICounter.FULLY_COVERED;

			return status2 == ICounter.FULLY_COVERED;
		}

		public boolean isNotCoveredAnything() {
			for (int i = 0; i < lines.length; i++) {
				if (lines[i] != ICounter.EMPTY && lines[i] != ICounter.NOT_COVERED)
					return false;
			}
			return true;
		}

		public boolean isNotActivatedAnything() {
			for (int i = 0; i < lines.length; i++) {
				if (lines[i] != ICounter.EMPTY)
					return false;
			}
			return true;
		}

		public Set<Integer> getCoveredLineNumbers() {
			Set<Integer> coveredLineNumbers = new HashSet<>();

			for (int i = 0; i < lines.length; i++) {
				if (lines[i] == ICounter.FULLY_COVERED)
					coveredLineNumbers.add(i);
			}

			return coveredLineNumbers;
		}

		public int getCoveredCount() {
			return getCoveredLineNumbers().size();
		}

		public int getActivatedLineCount() {
			int count = 0;

			for (int i = 0; i < lines.length; i++) {
				if (lines[i] != ICounter.EMPTY)
					count++;
			}

			return count;
		}

	}

}
