package org.apache.kafka.connect.runtime;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.common.metrics.stats.Avg;
import org.apache.kafka.common.metrics.stats.Max;
import org.apache.kafka.common.utils.MockTime;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RecreateConnectMetricsTest {

    private static final Map<String, String> DEFAULT_WORKER_CONFIG = new HashMap<>();

    static {
        DEFAULT_WORKER_CONFIG.put(WorkerConfig.KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        DEFAULT_WORKER_CONFIG.put(WorkerConfig.VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        DEFAULT_WORKER_CONFIG.put(WorkerConfig.INTERNAL_KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        DEFAULT_WORKER_CONFIG.put(WorkerConfig.INTERNAL_VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
    }

    @Test
    public void recreate() {
        WorkerConfig workerConfig = new WorkerConfig(WorkerConfig.baseConfigDef(), DEFAULT_WORKER_CONFIG);
        ConnectMetrics connectMetrics = new ConnectMetrics("workerId",
                workerConfig,
                new MockTime());

        ConnectMetricsRegistry registry = connectMetrics.registry();
        ConnectMetrics.MetricGroup metricGroup = connectMetrics.group(registry.taskGroupName(),
                registry.connectorTagName(), "id.connector()",
                registry.taskTagName(), "10");
        metricGroup.close();

        Sensor sensor = metricGroup.sensor("arjun");
        sensor.add(metricName("x1"), new Max());
        sensor.add(metricName("v2"), new Avg());

        for (Map.Entry<MetricName, KafkaMetric> metrics: metricGroup.metrics().metrics().entrySet()) {
            System.out.println("Contains metric: " + metrics.getKey());
        }

        System.out.println(" -- New Metrics: ");

        metricGroup = connectMetrics.group(registry.taskGroupName(),
                registry.connectorTagName(), "id.connector()",
                registry.taskTagName(), "10");
        metricGroup.close();

        for (Map.Entry<MetricName, KafkaMetric> metrics: metricGroup.metrics().metrics().entrySet()) {
            System.out.println("Found metric: " + metrics.getKey());
        }

        sensor = metricGroup.sensor("arjun");
        sensor.add(metricName("x1"), new Max());
        sensor.add(metricName("v2"), new Avg());

    }

    static MetricName metricName(String name) {
        return new MetricName(name, "group", "", Collections.<String, String>emptyMap());
    }

}
