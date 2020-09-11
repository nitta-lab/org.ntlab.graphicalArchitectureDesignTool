package models.dataFlowModel;

import models.*;

public class ResourceDependency extends Edge {
	protected DataflowChannelGenerator channelGenerator = null;

	public ResourceDependency(ResourceNode src, ResourceNode dst, DataflowChannelGenerator channelGenerator) {
		super(src, dst);
		this.channelGenerator = channelGenerator;
	}
	
	public DataflowChannelGenerator getChannelGenerator() {
		return channelGenerator;
	}
	
	public String toString() {
		return channelGenerator.getChannelName();
	}
}
