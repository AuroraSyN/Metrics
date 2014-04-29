package net.sourceforge.metrics.tests;

import java.io.ByteArrayInputStream;

import net.sourceforge.metrics.core.IMetricsElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import junit.framework.TestCase;

public abstract class MetricsTestCase extends TestCase {

	public static final String TEST_JAVA = "package test;\n"
				+ "public class TestClass {\n" + "   private int foo = 10;\n"
				+ "   public TestClass() { }\n"
				+ "   public int getFoo() { return foo; }\n" + "}";
	public static final String PROJECT = "Test";
	public static final String SRC = "src";
	public static final String PACKAGE = "test";
	public static final String CLASS = "TestClass.java";
	public static final String PATH_PROJECT = "/" + PROJECT;
	public static final String PATH_SRC = PATH_PROJECT + "/" + SRC;
	public static final String PATH_PACKAGE = PATH_SRC + "/" + PACKAGE;
	public static final String PATH_CLASS = PATH_PACKAGE + "/" + CLASS;

	public static final String[] ALL = new String[] {
		PATH_PROJECT, PATH_SRC, PATH_PACKAGE, PATH_CLASS
	};
	
	/**
	 * Our tests need a project in the test workspace, so the setup creates one
	 * if it doesn't exist yet
	 */
	protected void setUp() throws Exception {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// create a project if it doesn't exist
		IProject project = workspace.getRoot().getProject(PATH_PROJECT);
		if (!project.exists()) {
			project.create(null);
			project.open(null);
			// create a source folder
			IFolder src = project.getFolder(SRC);
			src.create(true, true, null);
			// create a output directory
			IFolder binDir = project.getFolder("bin");
			binDir.create(true, true, null);
			
			// make this a Java project, thank you FAQ 351!
			IProjectDescription desc = project.getDescription();
			desc.setNatureIds(new String[] { JavaCore.NATURE_ID });
			project.setDescription(desc, null);
			IJavaProject javaProj = JavaCore.create(project);
			
			IPath binPath = binDir.getFullPath();
			javaProj.setOutputLocation(binPath, null);
			// add default JRE entry
			
			// add it to the build path
			IClasspathEntry cpe = JavaCore.newSourceEntry(src.getFullPath());
			javaProj.setRawClasspath(new IClasspathEntry[]{cpe}, null);
			// create a package
			IFolder testP = src.getFolder(PACKAGE);
			testP.create(true, true, null);
			// create a java file
			IFile testClass = testP.getFile(CLASS);
			testClass.create(new ByteArrayInputStream(TEST_JAVA.getBytes()), true, null);
		}
	}

	protected IResource find(String path) {
		IPath p = new Path(path);
		return ResourcesPlugin.getWorkspace().getRoot().findMember(p);
	}

	/**
	 * FInd the IMetricsElement for the resource with the given full path
	 * @param resourceName full path of a workspace resource
	 * @return metrics element or null if resource does not exist
	 */
	protected IMetricsElement getMetricsElement(String resourceName) {
		IResource resource = find(resourceName);
		if (resource == null) return null;
		IJavaElement element = JavaCore.create(resource);
		if (element == null) return null;
		return (IMetricsElement) Platform.getAdapterManager()
			.getAdapter(element, IMetricsElement.class);
	}

}
