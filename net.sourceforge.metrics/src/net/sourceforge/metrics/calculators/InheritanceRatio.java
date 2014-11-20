/*
 * Copyright (c) 2014 Sumit Bisht. All rights reserved.
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
 * $id$
 */
package net.sourceforge.metrics.calculators;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

import net.sourceforge.metrics.core.Constants;
import net.sourceforge.metrics.core.Metric;
import net.sourceforge.metrics.core.sources.AbstractMetricSource;
import net.sourceforge.metrics.core.sources.TypeMetrics;
/**
 * Provides the inheritance ratio for the class
 * which is the ratio of number of methods inherited by a class to the total 
 * number of methods accessible by the methods in the class
 * 
 * @author sumit bisht
 *
 */
public class InheritanceRatio extends Calculator implements Constants {

	public InheritanceRatio() {
		super(INHERITANCE);
	}

	@Override
	public void calculate(AbstractMetricSource source)
			throws InvalidSourceException {
		Set<IMethod> currentClassMethods = new HashSet<IMethod>();
		Set<IMethod> inheritedMethods = new HashSet<IMethod>();
		if (source.getLevel() != TYPE) throw new InvalidSourceException("InheritanceRatio is only applicable to types");
		try {
			IMethod[] members = ((IType)source.getJavaElement()).getMethods();
			for(IMethod method: members){
				currentClassMethods.add(method);
			}
		} catch (JavaModelException e) {
		}
		IType[] parents = calculateParents(source);
		for(IType parent: parents){
			addVisibleMethods(inheritedMethods, parent);
		}
//		System.out.println("Current class: "+currentClassMethods.size()+", inherited: "+inheritedMethods.size());
		Double ratio = 0.0d;
		if(currentClassMethods.size() != 0 && inheritedMethods.size() != 0)
			ratio = (double) (inheritedMethods.size()/currentClassMethods.size());
		
		source.setValue(new Metric(INHR, ratio));
	}
	/**
	 * Calculates all the parent types for the selected type
	 * @param source The selected type
	 * @return Array of all super classes in the hierarchy of the class
	 */
	private IType[] calculateParents(AbstractMetricSource source){
		TypeMetrics tm = (TypeMetrics)source;
		IType iType = (IType)source.getJavaElement();
		ITypeHierarchy hierarchy = tm.getHierarchy();
		return hierarchy.getAllSuperclasses(iType);
	}
	
	/**
	 * Adds non-static and non-private methods to the methodSet
	 * @param methodSet The passed set containing methods
	 * @param classType The class type over which the iteration is to be made
	 */
	private void addVisibleMethods(Set<IMethod> methodSet, IType classType){
		try {
			IMethod[] methods = classType.getMethods();
			for(IMethod method: methods){
				if((method.getFlags() & Flags.AccStatic) != 0)
					continue;
				if((method.getFlags() & Flags.AccPrivate) != 0)
					continue;
				methodSet.add(method);
			}
		} catch (JavaModelException e) {
			System.err.println("Not adding methods as improper class detected");
		}
	}

}
