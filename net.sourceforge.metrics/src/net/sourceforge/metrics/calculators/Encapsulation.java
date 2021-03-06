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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import net.sourceforge.metrics.core.Constants;
import net.sourceforge.metrics.core.Metric;
import net.sourceforge.metrics.core.sources.AbstractMetricSource;

/**
 * Calculates both the private and total attributes in a class
 * and finds out the ratio between total and private attributes.
 * In current avtar, this combines fields and methods to form attributes.
 * @author sumit bisht
 *
 */
public class Encapsulation extends Calculator implements Constants {

	public Encapsulation() {
		super(ENCAPSULATION);
	}

	@Override
	public void calculate(AbstractMetricSource source)
			throws InvalidSourceException {
		if (source.getLevel() != TYPE) throw new InvalidSourceException("Encapsulation is only applicable to types");
		try {
			IField[] fields = ((IType)source.getJavaElement()).getFields();
			float totFields = fields.length;
			float privFields = 0;
			for(IField field: fields){
				if((field.getFlags() & Flags.AccPrivate) != 0)
					privFields++;
			}
			IMethod[] methods = ((IType)source.getJavaElement()).getMethods();
			float totMethods = methods.length;
			float privMethods = 0.0f;
			for(IMethod meth: methods){
				if((meth.getFlags() & Flags.AccPrivate) != 0)
					privMethods++;
			}
			System.out.println("Found "+totFields+" fields, "+totMethods+" methods.");
			double result = (privFields+privMethods)/(totFields+totMethods);
			if(totFields+totMethods == 0)
				result = 0;	
			source.setValue(new Metric(ENCAPSULATION, result));
		} catch (JavaModelException modelEx) {
			source.setValue(new Metric(ENCAPSULATION, 0));
		}
	}

}
