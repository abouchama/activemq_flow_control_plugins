package com.abouchama.camel.activemq;

import static org.junit.Assert.*;
import java.io.File;
import java.net.URI;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.abouchama.activemq.*;
/**
 * Tests the settings of the Dead Letter Queue: where go expired messages,
 * persistent messages, rolled back messages.
 * 
 */
public class DestinationFlowControlTest extends BrokerCleaner {
	private BrokerService broker;
	private Connection connection;
	private Session producerSession, consumerSession;
	private Session producerSession1, consumerSession1;
	private MessageProducer producer;
	private MessageProducer producer1;
	private MessageConsumer consumer;
	private MessageConsumer consumer1;
	private ActiveMQConnectionFactory connectionFactory;
	private final static String TEST_QUEUE_NAME = "TEST.QUEUE";

	@Before
	public void setUp() throws Exception {

		broker = BrokerServiceHelper.createAndStartUniqueBroker();
		super.brokers.add(broker);

		URI brokerUri = broker.getVmConnectorURI();
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, brokerUri);
		connection = connectionFactory.createConnection();
		connection.start();

	}

	@Override
	@After
	public void tearDown() throws Exception {
		// consumer.close();
		producer.close();
		// consumerSession.close();
		producerSession.close();
		connection.close();
		broker.deleteAllMessages();
		super.tearDown();
	}

	
	@Test
	public void testMaxMessagePerQueue() throws Exception {
		boolean transacted = true;
		producerSession = connection.createSession(transacted,
				Session.AUTO_ACKNOWLEDGE);
		javax.jms.Queue destination = producerSession
				.createQueue(TEST_QUEUE_NAME);
		producer = producerSession.createProducer(destination);
		// consumerSession = connection.createSession(transacted,
		// Session.AUTO_ACKNOWLEDGE);
		// consumer = consumerSession.createConsumer(destination);

		Message msg = producerSession.createTextMessage("");
		msg.setJMSDeliveryMode(1);
		producer.send(msg);
		producerSession.commit();

		Map<ActiveMQDestination, Destination> queues = broker.getBroker()
				.getDestinationMap();
		for (Destination queue : queues.values()) {
			if (queue.getName().equals(TEST_QUEUE_NAME)) {
				Assert.assertEquals(1, queue.browse().length);
			}
		}
	}
}
