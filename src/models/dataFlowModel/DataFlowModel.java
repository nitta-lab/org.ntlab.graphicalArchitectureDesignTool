package models.dataFlowModel;

import java.util.Set;

import models.dataConstraintModel.ChannelGenerator;
import models.dataConstraintModel.DataConstraintModel;
import models.dataConstraintModel.IdentifierTemplate;

public class DataFlowModel extends DataConstraintModel {	
	public ResourceDependencyGraph getResourceDependencyGraph() {
		ResourceDependencyGraph resourceDependencyGraph = new ResourceDependencyGraph();
		for (ChannelGenerator channelGen: getChannelGenerators()) {
			DataflowChannelGenerator dfChannelGen = (DataflowChannelGenerator)channelGen;
			Set<IdentifierTemplate> inputResources = dfChannelGen.getInputIdentifierTemplates();
			Set<IdentifierTemplate> outputResources = dfChannelGen.getOutputIdentifierTemplates();
			for (IdentifierTemplate in: inputResources) {
				for (IdentifierTemplate out: outputResources) {
					resourceDependencyGraph.addEdge(in ,out, dfChannelGen);
				}
			}
		}
		for (ChannelGenerator channelGen: getIOChannelGenerators()) {
			DataflowChannelGenerator dfChannelGen = (DataflowChannelGenerator)channelGen;
			Set<IdentifierTemplate> outputResources = dfChannelGen.getOutputIdentifierTemplates();
			for (IdentifierTemplate out: outputResources) {
				resourceDependencyGraph.addNode(out);
			}			
		}
		return resourceDependencyGraph;
	}
}
