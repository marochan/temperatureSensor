import com.ts.serde.AggregationClass;
import com.ts.serde.AggregationSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.*;

public class Steps {
    static String reservoirSampling(@NotNull AggregationClass agg) {
        if (agg.getSamples().size() < 8) {
            return "Collecting samples";
        }
        int sampleSize = 4;
        double[] samples = new double[sampleSize];
        for(int j = 0; j < sampleSize; j++){
            samples[j] = agg.getSamples().get(j);
        }
        Random random = new Random();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < agg.getSamples().size(); i++) {
            int index = random.nextInt(i + 1);
            if (index < sampleSize) {
                samples[index] = agg.getSamples().get(i);
            }
        }
        jsonObject.put("TimeStamp", agg.getTimeStamp());
        jsonObject.put("ReservoirSample", Arrays.toString(samples));
        return jsonObject.toString();
    }

    static String createJSONString(AggregationClass agg) {
        JSONObject object = new JSONObject();
        object.put("TimeStamp", agg.getTimeStamp());
        object.put("Average", String.valueOf(agg.getAvg()));
        return object.toString();
    }


    static AggregationClass generateTuple() {
        return new AggregationClass(null, 0.0, 0.0, 0.0, new ArrayList<>());
    }


    static void kafkaPropertiesSet(String kafkaURI, Properties properties, String clientID) {
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURI);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, clientID);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, AggregationSerde.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    public static AggregationClass tempAggregator(DateFormat dateFormat, String key, Double value, AggregationClass aggregate) {
        aggregate.setTimeStamp(dateFormat.format(new Date()));
        aggregate.setCount(aggregate.getCount()+1);
        aggregate.setSum(aggregate.getSum() + value);
        aggregate.setAvg(aggregate.getSum()/aggregate.getCount());
        List<Double> s = aggregate.getSamples();
        s.add(value);
        aggregate.setSamples(s);
        return aggregate;
    }
}
