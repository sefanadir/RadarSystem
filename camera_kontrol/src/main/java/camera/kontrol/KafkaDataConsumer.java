package camera.kontrol;

import java.util.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaDataConsumer
{
    KafkaConsumer<String, String> consumer;

    public KafkaDataConsumer()
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);
    }

    public ConsumerRecords<String, String> GetTargetBearingPosition(String topicName)
    {
        consumer.subscribe(Arrays.asList(topicName));

        ConsumerRecords<String, String> records = consumer.poll(100);

        return records;
    }
}
