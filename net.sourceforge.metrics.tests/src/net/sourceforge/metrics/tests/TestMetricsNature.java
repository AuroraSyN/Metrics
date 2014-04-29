package net.sourceforge.metrics.tests;

import net.sourceforge.metrics.core.internal.MetricsNature;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Ensure that the metrics nature gets properly added/removed
 * 
 * @author Frank Sauer
 */
public class TestMetricsNature extends MetricsTestCase {

	public void testAddRemoveNature() throws Exception {
		IJavaProject jProject = (IJavaProject) getMetricsElement(PATH_PROJECT).getJavaElement();
		IProject project = jProject.getProject();
		assertFalse("Project should not yet have metrics nature", project.hasNature(MetricsNature.NATURE_ID));
		MetricsNature.addNatureToProject(project, null);
		assertTrue("Project should have metrics nature", project.hasNature(MetricsNature.NATURE_ID));
		MetricsNature.removeNatureFromProject(project, null);
		assertFalse("Project should no longer have metrics nature", project.hasNature(MetricsNature.NATURE_ID));		
	}
}
