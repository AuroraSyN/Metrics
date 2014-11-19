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
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import net.sourceforge.metrics.core.Constants;
import net.sourceforge.metrics.core.Metric;
import net.sourceforge.metrics.core.sources.AbstractMetricSource;

/**
 * Calculates the number of public methods in a class
 * @author sumit bisht
 *
 */
public class Messaging extends Calculator implements Constants {

	public Messaging() {
		super(MESSAGING);
	}

	@Override
	public void calculate(AbstractMetricSource source)
			throws InvalidSourceException {
		if (source.getLevel() != TYPE) throw new InvalidSourceException("Messaging is only applicable to types");
		try {
			IMethod[] methods = ((IType)source.getJavaElement()).getMethods();
			int count = 0;
			for (int i = 0; i < methods.length; i++) {
				if ((methods[i].getFlags() & Flags.AccPublic) != 0) {
					count++;
				}
			}
			source.setValue(new Metric(MESSAGING, count));
		} catch (JavaModelException e) {
			source.setValue(new Metric(MESSAGING, 0));
		}
	}

}
