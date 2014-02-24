package com.abouchama.activemq;


import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowControlQueue extends BrokerFilter {

	public FlowControlQueue(Broker next, Long MaxSizePerMessage) {
		super(next);
		this.MaxSizePerMessage = MaxSizePerMessage;
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(FlowControlQueue.class);

	long MaxSizePerMessage = 999999999;

	public void setMaxSizePerMessage(long maxSizePerMessage) {
		MaxSizePerMessage = maxSizePerMessage;
	}
	
		
	@Override
	public void send(ProducerBrokerExchange producerExchange, Message message)
			throws Exception {
		
		LOG.info("1 - Welcome into the flow control Queue plugin");
		LOG.info("2 - message.getSize : " + message.getSize()
				+ " MaxSizePerMessage : " + MaxSizePerMessage);

		if (message.getSize() <= MaxSizePerMessage) {
			LOG.info("3 - Message Authorized : The Message Size respect the MaxSizePerMessage - Your size message  "
					+ message.getSize() + " and MaxSizePerMessage = " + MaxSizePerMessage);

		} else {
			LOG.error("3 - Message Dropped : The Message Size should respect the MAXDEPTH (see https://github.com/abouchama/activemq_flow_control_plugins) : ["
					+ message.getSize() + "] and MaxSizePerMessage = " + MaxSizePerMessage);
			message.isDropped();
			message.getConnection().close();
		}
		super.send(producerExchange, message);
	}
}
