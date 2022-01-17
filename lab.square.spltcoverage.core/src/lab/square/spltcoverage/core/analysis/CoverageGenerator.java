package lab.square.spltcoverage.core.analysis;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import lab.square.spltcoverage.model.CoverageResult;
import lab.square.spltcoverage.model.IProxy;

/*
 * Dependes on jacoco 0.7.7!!!!!
 */
public class CoverageGenerator {

	private static final String DESTFILE = "mydata.exec";

	private static final String SERVICE_URL = "service:jmx:rmi:///jndi/rmi://localhost:7777/jmxrmi";

	private final PrintStream out;
	private Class[] targetClasses;
	private int count;
	private final IProxy proxy;

	final IRuntime runtime;
	final Instrumenter instr;
	final RuntimeData data;

	/**
	 * Creates a new example instance printing to the given stream.
	 * 
	 * @param out stream for outputs
	 * @throws IOException
	 * @throws MalformedObjectNameException
	 */
	public CoverageGenerator(final PrintStream out) throws IOException, MalformedObjectNameException {
		this.out = out;
		runtime = new LoggerRuntime();
		instr = new Instrumenter(runtime);
		data = new RuntimeData();

		// Open connection to the coverage agent:
		final JMXServiceURL url = new JMXServiceURL(SERVICE_URL);
		final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		final MBeanServerConnection connection = jmxc.getMBeanServerConnection();

		proxy = MBeanServerInvocationHandler.newProxyInstance(connection, new ObjectName("org.jacoco:type=Runtime"),
				IProxy.class, false);
	}

	public CoverageGenerator(final PrintStream out, Class... targetClasses)
			throws MalformedObjectNameException, IOException {
		this(out);
		this.targetClasses = targetClasses;
	}

	public CoverageResult analyze() throws Exception {

		System.out.println("Version: " + proxy.getVersion());
		System.out.println("Session: " + proxy.getSessionId());

		final ExecutionDataStore execStore = new ExecutionDataStore();
		final SessionInfoStore sessionStore = new SessionInfoStore();

		final ExecutionDataReader reader = new ExecutionDataReader(
				new ByteArrayInputStream(proxy.getExecutionData(false)));
		reader.setExecutionDataVisitor(execStore);
		reader.setSessionInfoVisitor(sessionStore);
		reader.read();

		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(execStore, coverageBuilder);

		for (int i = 0; i < targetClasses.length; i++) {
			final String targetName = targetClasses[i].getName();
			analyzer.analyzeClass(getTargetClass(targetName), targetName);
		}

		final CoverageResult result = new CoverageResult(analyzer, coverageBuilder, proxy);
		return result;
	}

	private void makeDirectory(String directory) {
		String[] splitted = directory.split("/");
		String checkDirectory = "";
		for (int i = 0; i < splitted.length - 1; i++) {
			checkDirectory = checkDirectory + splitted[i] + "/";
			File file = new File(checkDirectory);
			if (!file.exists())
				file.mkdir();
		}
	}

	private InputStream getTargetClass(final String name) {
		final String resource = '/' + name.replace('.', '/') + ".class";
		if (targetClasses.length == 0)
			return null;
		return getClass().getResourceAsStream(resource);
	}

	public void resetData() throws IOException, MalformedObjectNameException {
		proxy.reset();
	}

	public void generateCoverage(ICoverageRunner runner) {
		runTestInPath(runner.getClasspath(), runner.getTestClassesPath(), runner);
	}

	private void runTestInPath(String classpath, String[] testClassesPath, ICoverageRunner runner) {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TestListener(runner));

		for (String path : testClassesPath) {
			Class forTest = null;
			try {
				forTest = loadClassByPath(classpath, convertPathToClassName(path));
			} catch (MalformedURLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			junit.run(forTest);
		}
	}

	private String convertPathToClassName(String classPath) {
		String filtered = classPath.replace('\\', '/').replace('/', '.').replaceFirst(".*bin.", "");

		if (filtered.endsWith(".class"))
			filtered = filtered.substring(0, filtered.length() - 6);

		return filtered;
	}

	private Class loadClassByPath(String binPath, String name) throws MalformedURLException, ClassNotFoundException {
		URLClassLoader loader = URLClassLoader.newInstance(new URL[] { new File(binPath).toURI().toURL() });

		return loader.loadClass(name);
	}

	private void makeExecFile(String directory, final byte[] exeData) throws IOException, FileNotFoundException {
		makeDirectory(directory);
		File execFile = new File(directory + ".exec");
		execFile.createNewFile();
		final FileOutputStream localFile = new FileOutputStream(execFile, false);
		localFile.write(exeData);
		localFile.close();
	}

	private class TestListener extends RunListener {
		ICoverageRunner runner;

		public TestListener(ICoverageRunner runner) {
			this.runner = runner;
		}

		@Override
		public void testStarted(Description description) throws Exception {
		}

		@Override
		public void testFinished(Description description) throws Exception {
			System.out.println(description.getTestClass().getSimpleName());
			System.out.println(description.getMethodName());
			System.out.println("//==============finished===========//");

			String testCaseDirectory;
			String testMethodDirectory;
			testCaseDirectory = description.getTestClass().getSimpleName() + "/";
			testMethodDirectory = description.getMethodName();
			String directory = runner.getOutputPath() + testCaseDirectory + testMethodDirectory;

			final byte[] exeData = proxy.getExecutionData(false);

			makeExecFile(directory, exeData);

			resetData();
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			System.out.println(failure.getTestHeader());
			System.out.println(failure.getTrace());
			System.out.println(failure.getMessage());
		}
	}

}
