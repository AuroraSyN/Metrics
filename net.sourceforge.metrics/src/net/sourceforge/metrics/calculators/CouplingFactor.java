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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

import net.sourceforge.metrics.core.Constants;
import net.sourceforge.metrics.core.Metric;
import net.sourceforge.metrics.core.sources.AbstractMetricSource;
import net.sourceforge.metrics.core.sources.TypeMetrics;

/**
 * Provides the coupling factor between classes
 * which are not in same inheritance hierarchy
 * The number of fields present inside the class help determine this metric
 * @author sumit bisht
 *
 */
public class CouplingFactor extends Calculator implements Constants {

	public CouplingFactor() {
		super(COF);
	}

	@Override
	public void calculate(AbstractMetricSource source)
			throws InvalidSourceException {
		if (source.getLevel() != TYPE) throw new InvalidSourceException("Coupling is only applicable to types");
		try {
			int coupling = 0;
			TypeMetrics tm = (TypeMetrics)source;
			IType iType = (IType)source.getJavaElement();
			ITypeHierarchy hierarchy = tm.getHierarchy();
			IType[] supers = hierarchy.getAllSuperclasses(iType);
			List<String> parentClassNames = new ArrayList<String>();
			for(IType clazz: supers){
				parentClassNames.add(clazz.getTypeQualifiedName());
			}
			IField[] members = iType.getFields();
			for(IField member : members){
				String typeSig = member.getTypeSignature();
				if(member.getTypeSignature().startsWith("Q")){
					if(parentClassNames.contains(typeSig)){
						// Nothing as this is a case of encapsulation
					}else if(typeSig.contains("String")){
						// Nothing for built in classes
					}
					else{
						coupling ++;
					}
				}
			}
			source.setValue(new Metric(COF, coupling));
		} catch (JavaModelException e) {
			System.err.println("Wrong/invalid source code tree.");
		}
	}

}
