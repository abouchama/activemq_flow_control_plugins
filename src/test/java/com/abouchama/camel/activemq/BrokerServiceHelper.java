package com.abouchama.camel.activemq;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.junit.Assert;
import org.apache.commons.io.FileUtils;

/**
 * Collection of static methods to setup a running
 * org.apache.activemq.broker.BrokerService Useful for tests that need to
 * instanciate a CXP or PXP broker alike
 * 
 */
public class BrokerServiceHelper {
  private final static String DEFAULT_ACTIVEMQXML_FILENAME = "src/main/resources/META-INF/spring/activemq.xml";

  /**
   * Creates and starts an activemq broker service called "ERMS-0" from the
   * default src/main/resources/META-INF/spring/activemq.xml and
   * src/test/resources/ERMS-0/activemq.properties configuration files. The data
   * folder is src/test/resources/ERMS-0/data. IMPORTANT: make sure you call
   * BrokerService.stop() once you are done
   */
  public static BrokerService createAndStartUniqueBroker() throws Exception {
    return createAndStartBroker("ERMS-0");
  }

  /**
   * Creates and starts an activemq broker service from a given spring
   * activemq.xml configuration file and a given activemq.properties property
   * file. The data folder will be stored under provided activemqBaseDir
   * directory. IMPORTANT: make sure you call BrokerService.stop() once you are
   * done
   * 
   * @param activemqXmlFileName
   *          the path to the activemq.xml file
   * @param activemqBaseDir
   *          the amq working dir (contains the conf folder and the
   *          activemq.properties within the conf folder)
   * @return broker service started from the spring configuration file
   * @throws Exception
   */
  private static BrokerService createAndStartBroker(String activemqBaseDirName)
      throws Exception {
    File activemqBaseDir = new File("src/test/resources/" + activemqBaseDirName);
    File activemqDataDir = new File("src/test/resources/" + activemqBaseDirName + "/data");

    // because of a bug in ActiveMQ 5.3.0, setPersistent(false) does not work
    // so we have to delete by hand the data directory
    FileUtils.deleteDirectory(activemqDataDir);

    // Sets system properties needed by activemq
    System.setProperty("activemq.base", activemqBaseDir.getAbsolutePath());
    System.setProperty("org.apache.activemq.UseDedicatedTaskRunner", "true");

    // create and start the broker
    boolean start = false;
    File activemqXmlFile = new File(DEFAULT_ACTIVEMQXML_FILENAME);
    BrokerService broker = BrokerFactory.createBroker("xbean:" + activemqXmlFile.toURI(), start);
    broker.setPersistent(false);
    broker.getBrokerDataDirectory().delete();
    broker.setDeleteAllMessagesOnStartup(true);
    broker.start();

    return broker;
  }

  public static List<BrokerService> createTwoConnectedStartedBrokers() throws Exception {
    List<BrokerService> brokers = new ArrayList<BrokerService>(2);
    BrokerService erms0 = createAndStartBroker("ERMS-0");
    int erms0TcpPort = BrokerServiceHelper.getOpenWirePort(erms0);
    brokers.add(erms0);

    BrokerService erms1 = createAndStartBroker("ERMS-1");
    int erms1TcpPort = BrokerServiceHelper.getOpenWirePort(erms1);
    brokers.add(erms1);

    // add the erms0 to erms1 network connector
    DiscoveryNetworkConnector erms0Toerms1NC = BrokerServiceHelper.startNetworkConnector(erms0,
        "static://(tcp://localhost:" + erms1TcpPort + ")");
    // add the erms1 to erms0 network connector
    DiscoveryNetworkConnector erms1Toerms0NC = BrokerServiceHelper.startNetworkConnector(erms1,
        "static://(tcp://localhost:" + erms0TcpPort + ")");

    // let the brokers connect to each other
    Thread.sleep(1000);

    // checks network bridges
    String erms0Toerms1BridgeMsg = erms0.getBrokerName() + " should have an active network bridge to "
        + erms1.getBrokerName();
    Assert.assertEquals(erms0Toerms1BridgeMsg, 1, erms0Toerms1NC.activeBridges().size());
    String erms1Toerms0BridgeMsg = erms1.getBrokerName() + " should have an active network bridge to "
        + erms0.getBrokerName();
    Assert.assertEquals(erms1Toerms0BridgeMsg, 1, erms1Toerms0NC.activeBridges().size());

    return brokers;
  }

  /**
   * Return the openwire port if openwire transport connector is defined in
   * activemq.xml, throw an exception otherwise
   * 
   * @param service
   * @return
   * @throws Exception
   */
  private static int getOpenWirePort(BrokerService service) throws Exception {
    List<TransportConnector> transConnectors = service.getTransportConnectors();
    for (TransportConnector transConnector : transConnectors) {
      if ("openwire".equals(transConnector.getName())) {
        URI uri = transConnector.getConnectUri();
        return uri.getPort();
      }
    }
    throw new Exception("No openwire transport connector for this broker");
  }

  /**
   * Configure a new network connector, add it to the BrokerService and start it
   * 
   * @param service
   *          the broker service onto which the network connector will be added
   * @param uri
   *          network connector uri (remote broker uri)
   * @param staticQueueName
   *          queue name to be statically included
   * @return network connector setup
   * @throws Exception
   */
  public static DiscoveryNetworkConnector startNetworkConnector(BrokerService service, String uri,
      String staticQueueName) throws Exception {
    DiscoveryNetworkConnector networkConnector = new DiscoveryNetworkConnector();
    networkConnector.setUri(new URI(uri));
    networkConnector.setName(UUID.randomUUID().toString());
    networkConnector.setDynamicOnly(false);
    networkConnector.setConduitSubscriptions(false);
    networkConnector.setDecreaseNetworkConsumerPriority(false);
    networkConnector.addExcludedDestination(new ActiveMQTopic(">"));
    networkConnector.addExcludedDestination(new ActiveMQQueue(">"));
    networkConnector.addDynamicallyIncludedDestination(new ActiveMQQueue(">"));
    networkConnector.addDynamicallyIncludedDestination(new ActiveMQTopic(">"));
    if (staticQueueName != null) {
      networkConnector.addStaticallyIncludedDestination(new ActiveMQQueue(staticQueueName));
    }
    service.addNetworkConnector(networkConnector);
    networkConnector.start();

    return networkConnector;
  }

  /**
   * Calling {@link #startNetworkConnector(BrokerService, String, String)
   * startNetworkConnector} with staticQueueName=null
   * 
   * @param service
   * @param uri
   * @return
   * @throws Exception
   */
  public static DiscoveryNetworkConnector startNetworkConnector(BrokerService service, String uri) throws Exception {
    return startNetworkConnector(service, uri, null);
  }
}
