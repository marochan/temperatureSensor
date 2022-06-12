import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Properties;

public class StreamProcessing {


    public static void main(String[] args){
        String topic = "TEMPERATURE";
        String targetTopic = "MOVINGAVG";
        String rsTargetTopic = "RS";
        String kafkaURI = "localhost:9092";
        String clientID = "StreamProcessing";
        Properties properties = new Properties();
        Steps.kafkaPropertiesSet(kafkaURI, properties, clientID);
        Consumed<String, Double> consumed = Consumed.with(Serdes.String(), Serdes.Double());

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Double> kafkaStreams = streamsBuilder.stream(topic, consumed);
        Duration timeDifference = Duration.ofSeconds(30);
        Duration diff = Duration.ofSeconds(60);
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

        /*
        Aggregating data from kafka topic
         */
        KTable  movingTable =  kafkaStreams.groupByKey()
                .windowedBy(SlidingWindows.ofTimeDifferenceWithNoGrace(timeDifference))
                .aggregate(
                        () -> Steps.generateTuple(), // initializer
                        (key, value, aggregate) -> Steps.tempAggregator(dateFormat,key, value, aggregate))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .filter((key, value) -> value.getSamples().size()==9)
                .mapValues(agg -> Steps.createJSONString(agg));

        KTable samplingTable = kafkaStreams.groupByKey()
                        .windowedBy(SlidingWindows.ofTimeDifferenceWithNoGrace(diff))
                        .aggregate(
                                () -> Steps.generateTuple(),
                                (key,value, aggregate) -> Steps.tempAggregator(dateFormat, key, value,aggregate))
                        .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                        .mapValues(agg -> Steps.reservoirSampling(agg));


        movingTable.toStream()
                .to(targetTopic, Produced.valueSerde(Serdes.String()));
        samplingTable.toStream()
                .filter((key,value) -> !value.equals("Collecting samples"))
                .to(rsTargetTopic, Produced.valueSerde(Serdes.String()));


        StreamsConfig config = new StreamsConfig(properties);
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), config);
        streams.cleanUp();
        streams.start();
    }

}
