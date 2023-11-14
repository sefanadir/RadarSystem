package camera.kontrol;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaDataProducer
{
    KafkaProducer<String, String> producer;

    public KafkaDataProducer()
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    public void SendDataToKafkaTopic(String topicName, double distance, double theta)
    {
        producer.send(new ProducerRecord<>(topicName, Double.toString(distance), Double.toString(theta)));
    }
}