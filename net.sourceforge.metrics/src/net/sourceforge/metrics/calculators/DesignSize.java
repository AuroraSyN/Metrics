package net.sourceforge.metrics.calculators;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;

import net.sourceforge.metrics.core.Metric;
import net.sourceforge.metrics.core.sources.AbstractMetricSource;
import net.sourceforge.metrics.core.sources.TypeMetrics;
/**
 * Calculates the design complexity on the basis of number of classes in the application.
 * @author sumit bisht
 */
public class DesignSize extends Calculator {

	public DesignSize() {
		super(DESIGN_SIZE);
	}

	@Override
	public void calculate(AbstractMetricSource source)
			throws InvalidSourceException {
		if (source.getLevel() != PROJECT) throw new InvalidSourceException("DesignSize metrics is only applicable to projects");
		TypeMetrics tm = (TypeMetrics)source;
		IType iType = (IType)source.getJavaElement();
		ITypeHierarchy hierarchy = tm.getHierarchy();
		IType[] supers = hierarchy.getAllSuperclasses(iType);
		IType[] subs = hierarchy.getSubtypes(iType); // BUG #933209 
		source.setValue(new Metric(DESIGN_SIZE, supers.length+subs.length));
	}

}
