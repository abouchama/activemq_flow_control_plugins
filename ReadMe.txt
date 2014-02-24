The Destination Flow Control Plugin:
===================================

This plugin allow to to enforce flow control limits, by defining the following :

- MaxSizePerMessage : Specifies the maximum length of a message that can be transmitted.
- MaxMessagesPerQueue: Number of maximal de messages authorized in the queue. The value should be comprise from 0 to 99999999. 
- MaxProducersPerQueue: Number of maximal producers per queue.

Below is an example of a basic configuration:
<plugins>
	<bean xmlns="http://www.springframework.org/schema/beans" id="FlowControlQueuePlugin"
				class="com.abouchama.activemq.FlowControlQueuePlugin">
		<property name="MaxSizePerMessage" value="1100" />
		<property name="MaxMessagesPerQueue" value="1100" />
		<property name="MaxProducersPerQueue" value="10" />
	</bean>
</plugins>

The Broker Resources Control Plugin:
===================================
This plugin allow to control resources of the broker, by specifying some limits like:

- MaxQueues: maximum of queues per broker.
- MaxMessages: maximum of messages per broker.
- MaxProducers: Number of maximal producers per Broker. 
- MaxConsumers: Number of maximal consumers per broker.

Below is an example of a basic configuration:
<plugins>
	<bean xmlns="http://www.springframework.org/schema/beans" id="BrokerResourcesControlPlugin"
				class="com.abouchama.activemq.BrokerCapacityPlugin">
				<property name="MaxQueues" value="99" />
				<property name="MaxMessages" value="999" />
				<property name="MaxProducers" value="50" />
				<property name="MaxConsumers" value="100" />
	</bean>
</plugins>

The association of these 2 plugins allow to determine the capacity of the broker, that can be provisionned based on simple monitoring solution.

For more help see the blog :
    http://abouchama.blogspot.fr/

Enjoy ;)