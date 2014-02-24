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

public class BrokerResourcesControlTest extends BrokerCleaner {
	private BrokerService broker;
	private Connection connection;
	private Session producerSession, consumerSession;
	private Session producerSession1, consumerSession1;
	private Session producerSession2, consumerSession2;
	private MessageProducer producer;
	private MessageProducer producer1;
	private MessageProducer producer2;
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
	public void testMaxProducers() throws Exception {
		boolean transacted = true;
		producerSession1 = connection.createSession(transacted,
				Session.AUTO_ACKNOWLEDGE);
		producerSession2 = connection.createSession(transacted,
				Session.AUTO_ACKNOWLEDGE);
		javax.jms.Queue destination = producerSession1
				.createQueue(TEST_QUEUE_NAME);

		producer1 = producerSession1.createProducer(destination);
		producer2 = producerSession2.createProducer(destination);

		Message msg1 = producerSession1.createTextMessage("");
		Message msg2 = producerSession2.createTextMessage("");
		msg1.setJMSDeliveryMode(1);
		msg2.setJMSDeliveryMode(1);
		producer1.send(msg1);
		producer2.send(msg2);
		Thread.sleep(3000);
		producerSession1.commit();
		producer1.close();
		Thread.sleep(3000);
		producer2.close();
		producerSession2.close();
	}
}
