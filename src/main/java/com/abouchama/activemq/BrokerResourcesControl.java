package com.abouchama.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerResourcesControl extends BrokerFilter {

	public BrokerResourcesControl(Broker next, Long MaxQueues, Long MaxMessages, Long MaxProducers,
			Long MaxConsumers) {
		super(next);
		this.MaxQueues = MaxQueues;
		this.MaxMessages = MaxMessages;
		this.MaxProducers = MaxProducers;
		this.MaxConsumers = MaxConsumers;
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(BrokerResourcesControl.class);

	long MaxQueues = 999999999;
	long MaxProducers = 999999999;
	long MaxConsumers = 999999999;
	long MaxMessages = 999999999;

	public void setMaxQueues(long maxQueues) {
		MaxQueues = maxQueues;
	}
	public void setMaxMessages(long maxMessages) {
		MaxMessages = maxMessages;
	}
	public void setMaxConsumers(long maxConsumers) {
		MaxConsumers = maxConsumers;
	}

	public void setMaxProducers(long maxProducers) {
		MaxProducers = maxProducers;
	}

	public void addProducer(ConnectionContext context, ProducerInfo info)
			throws Exception {
		LOG.info("A - MaxProducers - Welcome to the Managing Broker Capacity plugin");
		long TotalProducerCount = context.getBroker().getBrokerService()
				.getAdminView().getTotalProducerCount();
		LOG.info("B - MaxProducers -  MaxProducers : " + MaxProducers
				+ "TotalProducerCount : " + TotalProducerCount);

		while (MaxProducers < TotalProducerCount) {
			LOG.info("D - Connection is blocked MaxProducers : ["
					+ MaxProducers + "] > TotalProducerCount :["
					+ TotalProducerCount + "]");
			context.getConnection().isBlocked();
			Thread.sleep(1000);
			TotalProducerCount = 0;
			TotalProducerCount = context.getBroker().getBrokerService()
					.getAdminView().getTotalProducerCount();
		}
		context.getConnection().isActive();
		LOG.info("D - Connection is active MaxProducers : [" + MaxProducers
				+ "] > TotalProducerCount :[" + TotalProducerCount + "]");
		super.addProducer(context, info);
	}

	public Subscription addConsumer(ConnectionContext context, ConsumerInfo info)
			throws Exception {
		LOG.info("A - MaxConsumers - Welcome to the Managing Broker Capacity plugin");
		long TotalConsumerCount = context.getBroker().getBrokerService()
				.getAdminView().getTotalConsumerCount();
		LOG.info("B - MaxConsumers -  MaxConsumers : " + MaxConsumers
				+ "TotalConsumerCount : " + TotalConsumerCount);

		while (MaxConsumers < TotalConsumerCount) {
			LOG.info("D - Connection is blocked MaxConsumers : ["
					+ MaxConsumers + "] > TotalConsumerCount :["
					+ TotalConsumerCount + "]");
			context.getConnection().isBlocked();
			Thread.sleep(1000);
			TotalConsumerCount = 0;
			TotalConsumerCount = context.getBroker().getBrokerService()
					.getAdminView().getTotalConsumerCount();
		}
		context.getConnection().isActive();
		LOG.info("D - Connection is active MaxConsumers : [" + MaxConsumers
				+ "] > TotalConsumerCount :[" + TotalConsumerCount + "]");
		return super.addConsumer(context, info);
	}

	public void addSession(ConnectionContext context, SessionInfo info)
			throws Exception {
		LOG.info("A - MaxQueues|MaxMessages - Welcome to the Managing Broker Capacity plugin");
		long TotalQueuesCount = context.getBroker().getBrokerService()
				.getAdminView().getBroker().getQueueViews().size();
		long TotalMessageCount = context.getBroker().getBrokerService()
				.getAdminView().getTotalMessageCount();
		LOG.info("B - MaxQueues|MaxMessages - TotalQueuesCount : [" + TotalQueuesCount +  "] MaxQueues : [" + MaxQueues+"] - TotalMessageCount : [" + TotalMessageCount +  "] MaxMessages : [" + MaxMessages+"]");

		while (MaxQueues <= TotalQueuesCount) {
			LOG.error("C - MaxQueues - Connection is blocked MaxQueues : [" + MaxQueues
					+ "] > TotalQueuesCount :[" + TotalQueuesCount + "]");
			context.getConnection().isBlocked();
			Thread.sleep(1000);
			TotalQueuesCount = 0;
			TotalQueuesCount = context.getBroker().getBrokerService()
					.getAdminView().getBroker().getQueueViews().size();
		}
		while (MaxMessages <= TotalMessageCount) {
			LOG.error("C - MaxMessages - Connection is blocked MaxMessages : [" + MaxMessages
					+ "] > TotalMessageCount :[" + TotalMessageCount + "]");
			context.getConnection().isBlocked();
			Thread.sleep(1000);
			TotalMessageCount = 0;
			TotalMessageCount = context.getBroker().getBrokerService()
					.getAdminView().getTotalMessageCount();
		}
		context.getConnection().isActive();
		LOG.info("C - MaxQueues|Messages - Connection is active - TotalQueuesCount : [" + TotalQueuesCount +  "] MaxQueues : [" + MaxQueues+"] - TotalMessageCount : [" + TotalMessageCount +  "] MaxMessages : [" + MaxMessages+"]");
		super.addSession(context, info);
	}
}