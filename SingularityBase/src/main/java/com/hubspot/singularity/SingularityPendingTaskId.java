package com.hubspot.singularity;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.hubspot.mesos.JavaUtils;

public class SingularityPendingTaskId implements Comparable<SingularityPendingTaskId> {

  private final String requestId;
  private final long nextRunAt;
  private final int instanceNo;

  @JsonCreator
  public SingularityPendingTaskId(@JsonProperty("requestId") String requestId, @JsonProperty("nextRunAt") long nextRunAt, @JsonProperty("instanceNo") int instanceNo) {
    this.requestId = requestId;
    this.nextRunAt = nextRunAt;
    this.instanceNo = instanceNo;
  }

  public static List<SingularityPendingTaskId> filter(List<SingularityPendingTaskId> taskIds, String requestId) {
    List<SingularityPendingTaskId> matching = Lists.newArrayList();
    for (SingularityPendingTaskId taskId : taskIds) {
      if (taskId.getRequestId().equals(requestId)) {
        matching.add(taskId);
      }
    }
    return matching;
  }
  
  public String getRequestId() {
    return requestId;
  }

  public long getNextRunAt() {
    return nextRunAt;
  }

  public int getInstanceNo() {
    return instanceNo;
  }
  
  public static SingularityPendingTaskId fromString(String string) {
    final String[] splits = JavaUtils.reverseSplit(string, 3, "-");
 
    final String requestId = splits[0];
    final long nextRunAt = Long.parseLong(splits[1]);
    final int instanceNo = Integer.parseInt(splits[2]);
    
    return new SingularityPendingTaskId(requestId, nextRunAt, instanceNo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(instanceNo, requestId, nextRunAt);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SingularityPendingTaskId other = (SingularityPendingTaskId) obj;
    if (instanceNo != other.instanceNo)
      return false;
    if (requestId == null) {
      if (other.requestId != null)
        return false;
    } else if (!requestId.equals(other.requestId))
      return false;
    if (nextRunAt != other.nextRunAt)
      return false;
    return true;
  }

  public String toString() {
    return String.format("%s-%s-%s", getRequestId(), getNextRunAt(), getInstanceNo());
  }
  
  @Override
  public int compareTo(SingularityPendingTaskId o) {
    return ComparisonChain.start()
        .compare(this.getNextRunAt(), o.getNextRunAt())
        .compare(this.getRequestId(), o.getRequestId())
        .compare(this.getInstanceNo(), o.getInstanceNo())
        .result();
  }
  

}
