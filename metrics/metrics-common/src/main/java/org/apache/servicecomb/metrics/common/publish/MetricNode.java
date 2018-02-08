/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.metrics.common.publish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MetricNode {
  public final String name;

  private final List<Metric> metrics;

  private final Map<String, MetricNode> children;

  public String getName() {
    return name;
  }

  public List<Metric> getMetrics() {
    return metrics;
  }

  public Map<String, MetricNode> getChildren() {
    return children;
  }

  public Double getFirstMatchMetricValue(List<String> tagKeys, List<String> tagValues) {
    for (Metric metric : this.metrics) {
      if (metric.containTag(tagKeys, tagValues)) {
        return metric.getValue();
      }
    }
    return Double.NaN;
  }

  public MetricNode(List<Metric> metrics, String... groupTagKeys) {
    if (groupTagKeys == null || groupTagKeys.length == 0) {
      this.name = null;
      this.metrics = metrics;
      this.children = null;
    } else {
      this.name = groupTagKeys[0];
      this.metrics = null;
      this.children = new HashMap<>();
      Map<String, List<Metric>> groups = groupByTag(metrics, this.name);
      if (groupTagKeys.length == 1) {
        for (Entry<String, List<Metric>> group : groups.entrySet()) {
          this.children.put(group.getKey(), new MetricNode(null, group.getValue(), null));
        }
      } else {
        for (Entry<String, List<Metric>> group : groups.entrySet()) {
          this.children.put(group.getKey(),
              new MetricNode(group.getValue(), Arrays.copyOfRange(groupTagKeys, 1, groupTagKeys.length)));
        }
      }
    }
  }

  private MetricNode(String name, List<Metric> metrics, Map<String, MetricNode> children) {
    this.name = name;
    this.metrics = metrics;
    this.children = children;
  }

  private Map<String, List<Metric>> groupByTag(List<Metric> metrics, String tagKey) {
    Map<String, List<Metric>> groups = new HashMap<>();
    for (Metric metric : metrics) {
      if (metric.getTags().containsKey(tagKey)) {
        groups.computeIfAbsent(metric.getTags().get(tagKey), g -> new ArrayList<>()).add(metric);
      }
    }
    return groups;
  }
}
