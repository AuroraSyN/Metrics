package net.sourceforge.metrics.calculators;

import net.sourceforge.metrics.core.Metric;
import net.sourceforge.metrics.core.sources.AbstractMetricSource;
import net.sourceforge.metrics.core.sources.ProjectMetrics;
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
		if (source.getLevel() != PROJECT){
			System.out.println("ERROR: Unable to proceed as the selected element is not a project. Design size only applies to projects");
			throw new InvalidSourceException("DesignSize metrics is only applicable to projects");
		}
		ProjectMetrics projMt = (ProjectMetrics) source;
		int size = projMt.getChildren().size();
		System.out.println("Project size: "+size);
		source.setValue(new Metric(DESIGN_SIZE, size));
	}

}
