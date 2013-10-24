package com.hubspot.singularity;

import com.google.inject.name.Named;
import org.apache.curator.framework.CuratorFramework;

import com.codahale.dropwizard.lifecycle.Managed;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.hubspot.singularity.mesos.SingularityDriver;
import org.apache.curator.framework.recipes.leader.LeaderLatch;

public class SingularityManaged implements Managed {

  private final SingularityDriver driver;
  private final CuratorFramework curator;
  private final LeaderLatch leaderLatch;
  
  @Inject
  public SingularityManaged(SingularityDriver driver, CuratorFramework curator, LeaderLatch leaderLatch) {
    this.driver = driver;
    this.curator = curator;
    this.leaderLatch = leaderLatch;
  }
  
  @Override
  public void start() throws Exception {
    driver.start();
    leaderLatch.start();
  }
  
  @Override
  public void stop() throws Exception {
    driver.stop();
    leaderLatch.close();
    Closeables.close(curator, true);
  }

}
