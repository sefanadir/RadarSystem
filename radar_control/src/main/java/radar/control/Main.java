package radar.control;

public class Main
{
    public static final String TowerPositionTopic = "TowerPosition";
    public static final String TargetPointPositionTopic = "TargetPointPosition";
    public static final String TargetBearingPositionTopic = "TargetBearingPosition";

    public static void main(String[] args)
    {
        KafkaDataProducer kafkaDataProducer = new KafkaDataProducer();

        KafkaDataConsumer kafkaDataConsumer = new KafkaDataConsumer();

        Location radarTower;

        Location cameraTower;

        while(true)
        {
            var towerLocations = kafkaDataConsumer.GetLocation(TowerPositionTopic);

            if(!towerLocations.isEmpty())
            {
                radarTower = towerLocations.get(0);

                cameraTower = towerLocations.get(1);

                break;
            }
        }

        while(true)
        {
            var targetLocations = kafkaDataConsumer.GetLocation(TargetPointPositionTopic);

            for(var target : targetLocations)
            {
                double distance = CalculateDistance(cameraTower, target);

                double theta = CalculateAngel(cameraTower, target);

                kafkaDataProducer.SendDataToKafkaTopic(TargetBearingPositionTopic, distance, theta);
            }
        }
    }

    private static double CalculateDistance(Location cameraTower, Location target)
    {
        return  Math.sqrt((Math.pow(cameraTower.GetX()-cameraTower.GetY(), 2) + Math.pow(target.GetX()-target.GetY(), 2)));
    }

    private static double CalculateAngel(Location cameraTower, Location target)
    {
        return  180.0 / Math.PI * Math.atan2(cameraTower.GetX()-target.GetX(), target.GetY()-cameraTower.GetY());
    }
}