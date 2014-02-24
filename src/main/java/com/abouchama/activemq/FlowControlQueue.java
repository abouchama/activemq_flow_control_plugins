package com.abouchama.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ProducerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowControlQueue extends BrokerFilter {

	public FlowControlQueue(Broker next, Long MaxSizePerMessage,
			Long MaxProducersPerQueue) {
		super(next);
		this.MaxSizePerMessage = MaxSizePerMessage;
		this.MaxProducersPerQueue = MaxProducersPerQueue;
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(FlowControlQueue.class);
	

	/* Per Queue */
	// MAXDEPTH = Longueur maximale de la file d'attente, Entrez le nombre
	// maximal de messages autorisés dans la file d'attente
	// MAXMSGL = Longueur maximale des messages, Entrez la longueur maximale
	// d'un message, en octets, autorisée dans la file d'attente.
	// MaxSizePerMessage
	// MaxActiveConsumersPerQueue
	// MaxProducersPerQueue

	long MaxSizePerMessage = 999999999;
	long MaxProducersPerQueue = 100;

	public void setMaxSizePerMessage(long maxSizePerMessage) {
		MaxSizePerMessage = maxSizePerMessage;
	}
	
	public void setMaxProducersPerQueue(long maxProducersPerQueue) {
		MaxProducersPerQueue = maxProducersPerQueue;
	}
		
	@Override
	public void send(ProducerBrokerExchange producerExchange, Message message)
			throws Exception {

		LOG.info("1 - Welcome into the flow control plugin - made by ABOUCHAMA");
		// producerExchange.getRegionDestination().getConsumers();
		// producerExchange.getRegionDestination().getMaxProducersToAudit();
		// LOG.info("producerExchange.getRegionDestination().getConsumers() : "
		// + producerExchange.getRegionDestination().getConsumers() +
		// "producerExchange.getRegionDestination().getMaxProducersToAudit() : "
		// + producerExchange.getRegionDestination().getMaxProducersToAudit());

		LOG.info("2 - message.getSize : " + message.getSize()
				+ " MaxSizePerMessage : " + MaxSizePerMessage
				+ " MaxProducersPerQueue :" + MaxProducersPerQueue);

		if (message.getSize() <= MaxSizePerMessage) {
			LOG.info("3 - Message Authorized : The Message Size respect the MaxSizePerMessage - Your size message  "
					+ message.getSize() + " and MaxSizePerMessage = " + MaxSizePerMessage);

			/*
			 * if
			 * (producerExchange.getRegionDestination().getMaxProducersToAudit()
			 * >= MaxProducersPerQueue){
			 * 
			 * producerExchange.setMutable(isStopped());
			 * LOG.info("Producer blocked ................. : MaxProducersPerqueue"
			 * + MaxProducersPerQueue); }
			 */
			//
			// super.send(producerExchange, message);
			//DestinationFlowControl;BrokerRessourcesControl
		} else {
			LOG.error("3 - Message Dropped : The Message Size should respect the MAXDEPTH (see http://github/abouchama/flowcontrol) : ["
					+ message.getSize() + "] and MAXDEPTH = " + MaxSizePerMessage);
			message.isDropped();
			message.getConnection().close();
			// throw new SecurityException("Your Message Size [" +
			// message.getSize() + "] not respect the MAXDEPTH of [" + MAXDEPTH
			// + "] or password is invalid.");
		}
		super.send(producerExchange, message);
	}
}
