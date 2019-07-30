package kamon.system.jvm

import com.sun.management.UnixOperatingSystemMXBean
import java.lang.management.{ManagementFactory, OperatingSystemMXBean}

import kamon.Kamon
import kamon.metric.Gauge
import kamon.system.{JmxMetricBuilder, Metric, MetricBuilder}
import kamon.system.serviceTag
import org.slf4j.Logger

/**
  *  File descriptor metrics, as reported by JMX:
  *
  *   <b>Note:</b> This is UNIX specific
  *
  *    - @see [[https://docs.oracle.com/javase/8/docs/jre/api/management/extension/com/sun/management/UnixOperatingSystemMXBean.html "UnixOperatingSystemMXBean"]]*
  */
object FileDescriptorMetrics
    extends MetricBuilder("os.file-descriptor")
    with JmxMetricBuilder {

  def build(metricName: String, logger: Logger) = new Metric {

    val osBean: OperatingSystemMXBean =
      ManagementFactory.getOperatingSystemMXBean

    val openFileDescriptorMetric: Gauge = Kamon
      .gauge(metricName)
      .refine(Map(serviceTag, "component" -> "system-metrics"))

    def update(): Unit = {
      if (osBean.isInstanceOf[UnixOperatingSystemMXBean]) {
        openFileDescriptorMetric.set(
          osBean
            .asInstanceOf[UnixOperatingSystemMXBean]
            .getOpenFileDescriptorCount)
      }
    }
  }
}
