package com.abouchama.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class FlowControlQueuePlugin implements BrokerPlugin {
	long MaxSizePerMessage = 999999999;
	long MaxProducersPerQueue = 100;
	
	public long getMaxSizePerMessage() {
		return MaxSizePerMessage;
	}

	public void setMaxSizePerMessage(long maxSizePerMessage) {
		MaxSizePerMessage = maxSizePerMessage;
	}

	public long getMaxProducersPerQueue() {
		return MaxProducersPerQueue;
	}

	public void setMaxProducersPerQueue(long maxProducersPerQueue) {
		MaxProducersPerQueue = maxProducersPerQueue;
	}

	@Override
	public Broker installPlugin(Broker broker) throws Exception {
		return new FlowControlQueue(broker, MaxSizePerMessage, MaxProducersPerQueue);
	}
}