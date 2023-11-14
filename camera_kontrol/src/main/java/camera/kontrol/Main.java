package camera.kontrol;

public class Main
{
    public static final String TargetBearingPositionTopic = "TargetBearingPosition";
    public static final String CameraLosStatusTopic = "CameraLosStatus";

    public static void main(String[] args)
    {

        KafkaDataProducer kafkaDataProducer = new KafkaDataProducer();
        KafkaDataConsumer kafkaDataConsumer = new KafkaDataConsumer();

        kafkaDataConsumer.GetTargetBearingPosition(TargetBearingPositionTopic);

        while(true)
        {
            var records = kafkaDataConsumer.GetTargetBearingPosition(TargetBearingPositionTopic);

            for(var record : records)
            {
                double distance = Double.parseDouble(record.key());

                double theta = Double.parseDouble(record.value());

                kafkaDataProducer.SendDataToKafkaTopic(CameraLosStatusTopic, distance, theta);
            }
        }
    }
}