package net.sourceforge.metrics.calculators;

import java.util.List;
import java.util.ArrayList;

import net.sourceforge.metrics.core.Metric;
import net.sourceforge.metrics.core.sources.AbstractMetricSource;
import net.sourceforge.metrics.core.sources.ProjectMetrics;
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
		if (source.getLevel() != PROJECT){
			throw new InvalidSourceException("DesignSize metrics is only applicable to projects");
		}
		ProjectMetrics projMt = (ProjectMetrics) source;
		
		int size = getAllTypes(projMt).size();
		System.out.println("Project size: "+size);
		source.setValue(new Metric(DESIGN_SIZE, size));
	}
	/**
	 * @param source The root element for traversal
	 * @return the type
	 */
	private List<TypeMetrics> getAllTypes(AbstractMetricSource source){
		List <TypeMetrics> typeList = new ArrayList<TypeMetrics>();
		if(source instanceof TypeMetrics)
			typeList.add((TypeMetrics) source);
		else if(source.getChildren().size()>0){
			for(Object child : source.getChildren()){
				List<TypeMetrics> childData = getAllTypes((AbstractMetricSource) child);
				if(childData.size()>0)
					typeList.addAll(childData);
			}
		}
		return typeList;
	}

}
