package com.hubspot.singularity;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.codahale.dropwizard.jackson.Jackson;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.singularity.config.MesosConfiguration;
import com.hubspot.singularity.config.SingularityConfiguration;
import com.hubspot.singularity.config.ZooKeeperConfiguration;
import com.hubspot.singularity.mesos.SingularityDriver;
import com.hubspot.singularity.mesos.SingularityMesosScheduler;

public class SingularityModule extends AbstractModule {
  
  public static final String MASTER_PROPERTY = "singularity.master";
  public static final String ZK_NAMESPACE_PROPERTY = "singularity.namespace";
  public static final String HOSTNAME_PROPERTY = "singularity.hostname";
  
  @Override
  protected void configure() {
    bind(SingularityMesosScheduler.class).in(Scopes.SINGLETON);
    bind(SingularityDriver.class).in(Scopes.SINGLETON);
  }

  @Provides
  @Singleton
  public ObjectMapper getObjectMapper() {
    return Jackson.newObjectMapper().setSerializationInclusion(Include.NON_NULL);
  }
  
  @Provides
  @Named(MASTER_PROPERTY)
  public String providesMaster(SingularityConfiguration config) {
    return config.getMesosConfiguration().getMaster();
  }
  
  @Provides
  @Singleton
  public ZooKeeperConfiguration zooKeeperConfiguration(SingularityConfiguration config) {
    return config.getZooKeeperConfiguration();
  }

  @Provides
  @Named(ZK_NAMESPACE_PROPERTY)
  public String providesZkNamespace(ZooKeeperConfiguration config) {
    return config.getZkNamespace();
  }

  @Provides
  @Named(HOSTNAME_PROPERTY)
  public String providesHostnameProperty(SingularityConfiguration config) {
    return config.getHostname();
  }

  @Provides
  @Singleton
  public LeaderLatch provideLeaderLatch(CuratorFramework curator,
                                        @Named(SingularityModule.ZK_NAMESPACE_PROPERTY) String zkNamespace,
                                        @Named(SingularityModule.HOSTNAME_PROPERTY) String hostname) {
    return new LeaderLatch(curator, String.format("%s/leader", zkNamespace), hostname);
  }
  
  @Provides
  @Singleton
  public MesosConfiguration mesosConfiguration(SingularityConfiguration config) {
    return config.getMesosConfiguration();
  }
  
  @Singleton
  @Provides
  public CuratorFramework provideCurator(ZooKeeperConfiguration config) {
    CuratorFramework client = CuratorFrameworkFactory.newClient(
        config.getQuorum(),
        config.getSessionTimeoutMillis(),
        config.getConnectTimeoutMillis(),
        new ExponentialBackoffRetry(config.getRetryBaseSleepTimeMilliseconds(), config.getRetryMaxTries()));
    
    client.start();
    
    return client.usingNamespace(config.getZkNamespace());
  }
  
}
