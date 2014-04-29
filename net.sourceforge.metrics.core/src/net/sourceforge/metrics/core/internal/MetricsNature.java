/*
 * Copyright (c) 2003 Frank Sauer. All rights reserved.
 *
 * Licenced under CPL 1.0 (Common Public License Version 1.0).
 * The licence is available at http://www.eclipse.org/legal/cpl-v10.html.
 *
 *
 * DISCLAIMER OF WARRANTIES AND LIABILITY:
 *
 * THE SOFTWARE IS PROVIDED "AS IS".  THE AUTHOR MAKES  NO REPRESENTATIONS OR WARRANTIES,
 * EITHER EXPRESS OR IMPLIED.  TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL THE
 * AUTHOR  BE LIABLE FOR ANY DAMAGES, INCLUDING WITHOUT LIMITATION, LOST REVENUE,  PROFITS
 * OR DATA, OR FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL  OR PUNITIVE DAMAGES,
 * HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF  LIABILITY, ARISING OUT OF OR RELATED TO
 * ANY FURNISHING, PRACTICING, MODIFYING OR ANY USE OF THE SOFTWARE, EVEN IF THE AUTHOR
 * HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 *
 * $Id: MetricsNature.java,v 1.1 2005/04/16 21:37:09 sauerf Exp $
 */
 
package net.sourceforge.metrics.core.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Metrics Nature adds/removes Metrics builder to a project
 * 
 * @author Frank Sauer
 * @see IProjectNature
 */
public class MetricsNature implements IProjectNature {
	
	private IProject project = null;

	public final static String NATURE_ID = "net.sourceforge.metrics.core.nature";
	public final static String BUILDER_ID = "net.sourceforge.metrics.core.builder";

	/**
	 * enable the metrics nature/builder
     * @param project
     * @param monitor
	 * @throws CoreException
	 */
	public static void addNatureToProject(IProject project, IProgressMonitor monitor) throws CoreException {
		if (project != null && !project.hasNature(NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		}
	}
    
	/**
	 * disable (remove) the metrics builder
	 * @param project
     * @param monitor
	 * @throws CoreException
	 */
	public static void removeNatureFromProject(IProject project, IProgressMonitor monitor) throws CoreException {
		if (project != null && project.hasNature(NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			List lNatures = new ArrayList(Arrays.asList(natures));
			lNatures.remove(NATURE_ID);
			String[] newNatures = (String[])lNatures.toArray(new String[]{});
			if (newNatures.length < natures.length) {
				description.setNatureIds(newNatures);
				project.setDescription(description, monitor);
			}
		}
	}
    
	/**
	 * add metrics builder to project description
	 * @see IProjectNature#configure
	 */
	public void configure() throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		boolean found = false;

		for (int i = 0; i < commands.length; ++i) {
		  if (commands[i].getBuilderName().equals(BUILDER_ID)) {
			 found = true;
			 break;
		  }
		}
		if (!found) {
		  //add builder to project
		  ICommand command = description.newCommand();
		  command.setBuilderName(BUILDER_ID);
		  ICommand[] newCommands = new ICommand[commands.length + 1];

		  // Add it before other builders.
		  System.arraycopy(commands, 0, newCommands, 0, commands.length);
		  newCommands[commands.length] = command;
		  description.setBuildSpec(newCommands);
		  project.setDescription(description, null);
		}

	}

	/**
	 * remove metrics builder from project description
	 * @see IProjectNature#deconfigure
	 */
	public void deconfigure() throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		String builderID = BUILDER_ID;
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
				description.setBuildSpec(newCommands);
				getProject().setDescription(description, null);
				return;
			}
		}
	}

	public IProject getProject()  {
		return project;
	}

	public void setProject(IProject project)  {
		this.project = project;
	}
}
