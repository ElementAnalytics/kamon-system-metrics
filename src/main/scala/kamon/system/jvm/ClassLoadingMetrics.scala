/*
 * =========================================================================================
 * Copyright © 2013-2017 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package kamon.system.jvm

import java.lang.management.ManagementFactory

import kamon.Kamon
import kamon.system.{JmxMetricBuilder, Metric, MetricBuilder}
import kamon.system.serviceTag
import org.slf4j.Logger

/**
 *  Class Loading metrics, as reported by JMX:
 *    - @see [[http://docs.oracle.com/javase/8/docs/api/java/lang/management/ClassLoadingMXBean.html "ClassLoadingMXBean"]]
 */
object ClassLoadingMetrics extends MetricBuilder("jvm.class-loading") with JmxMetricBuilder{
  def build(metricName: String, logger: Logger) = new Metric {
    val classLoadingBean = ManagementFactory.getClassLoadingMXBean

    val classLoadingMetric = Kamon.gauge(metricName)

    val classesLoadedMetric           = classLoadingMetric.refine(Map(serviceTag, "component" -> "system-metrics", "mode" -> "loaded"))
    val classesUnloadedMetric         = classLoadingMetric.refine(Map(serviceTag, "component" -> "system-metrics", "mode" -> "unloaded"))
    val classesLoadedCurrentlyMetric  = classLoadingMetric.refine(Map(serviceTag, "component" -> "system-metrics", "mode" -> "currently-loaded"))

    def update(): Unit = {
      classesLoadedMetric.set(classLoadingBean.getTotalLoadedClassCount)
      classesUnloadedMetric.set(classLoadingBean.getUnloadedClassCount)
      classesLoadedCurrentlyMetric.set(classLoadingBean.getLoadedClassCount.toLong)
    }
  }
}