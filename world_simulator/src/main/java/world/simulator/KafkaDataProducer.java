package world.simulator;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaDataProducer
{
    Properties props;
    KafkaProducer<String, String> producer;

    public KafkaDataProducer()
    {
        props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }

    public void SendDataToKafkaTopic(String topicName, int x, int y)
    {
        producer = new KafkaProducer<>(props);

        producer.send(new ProducerRecord<>(topicName, Integer.toString(x), Integer.toString(y)));
    }
}
