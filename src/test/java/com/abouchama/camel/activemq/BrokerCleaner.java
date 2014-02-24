package com.abouchama.camel.activemq;

import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.broker.BrokerService;
import org.junit.After;

// TODO this class could be only a helper ?
public abstract class BrokerCleaner {
  protected final List<BrokerService> brokers = new ArrayList<BrokerService>();

  @After
  public void tearDown() throws Exception {
    for (BrokerService broker : brokers) {
      broker.stop();
    }
  }
}
