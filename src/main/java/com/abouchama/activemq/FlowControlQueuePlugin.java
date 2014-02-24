package com.abouchama.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class FlowControlQueuePlugin implements BrokerPlugin {
	long MaxSizePerMessage = 999999999;

	public long getMaxSizePerMessage() {
		return MaxSizePerMessage;
	}

	public void setMaxSizePerMessage(long maxSizePerMessage) {
		MaxSizePerMessage = maxSizePerMessage;
	}



	@Override
	public Broker installPlugin(Broker broker) throws Exception {
		return new FlowControlQueue(broker, MaxSizePerMessage);
	}
}