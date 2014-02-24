package com.abouchama.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;

public class BrokerResourcesControlPlugin implements BrokerPlugin {
	long MaxQueues = 999999999;
	long MaxMessages = 999999999;
	long MaxProducers = 999999999;
	long MaxConsumers = 999999999;


	public long getMaxQueues() {
		return MaxQueues;
	}

	public void setMaxQueues(long maxQueues) {
		MaxQueues = maxQueues;
	}
	public long getMaxMessages() {
		return MaxMessages;
	}

	public void setMaxMessages(long maxMessages) {
		MaxMessages = maxMessages;
	}

	public long getMaxConsumers() {
		return MaxConsumers;
	}

	public void setMaxConsumers(long maxConsumers) {
		MaxConsumers = maxConsumers;
	}

	public long getMaxProducers() {
		return MaxProducers;
	}

	public void setMaxProducers(long maxProducers) {
		MaxProducers = maxProducers;
	}

	@Override
	public Broker installPlugin(Broker broker) throws Exception {
		return new BrokerResourcesControl(broker, MaxQueues, MaxMessages, MaxConsumers, MaxProducers);
	}
	
}