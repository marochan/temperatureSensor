package com.ts;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static com.ts.Steps.kafkaPropertiesSet;
import static com.ts.Steps.optionsSetting;

// uri do mqqt brokera do uruchomienia w dockerze: tcp://172.17.0.1:1883
public class Bridge {

    public static void main(String[] args) throws MqttException {
        String subscriberID = "MQTT-Kafka bridge";
        String topic = "TEMPERATURE";
        String kafkaURI = "kafka:9093";
        IMqttClient mqttClient = new MqttClient("tcp://172.17.0.1:1883", subscriberID);
        /*
        for connection outside of docker network
        String kafkaURI = "localhost:9092";
         IMqttClient mqttClient = new MqttClient("tcp://localhost:1883", subscriberID);
         */

        MqttConnectOptions options = new MqttConnectOptions();
        Properties properties = new Properties();
        kafkaPropertiesSet(kafkaURI, properties);
        optionsSetting(options);
        mqttClient.connect(options);
        MongoClient mongoClient = new MongoClient("mongo", 27017);
        MongoDatabase database = mongoClient.getDatabase("Temperature");
        MongoCollection brokerCollection = database.getCollection("MQTTReadings");
        Producer<String, Double> producer = new KafkaProducer<>(properties);
        DateFormat dateFormatKafka = new SimpleDateFormat("yyyy MM dd HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");


            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection to broker lost!" + throwable.getMessage());

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    try{

                        Date date = new Date();
                        String time = dateFormatKafka.format(date);
                        String mqttKey = dateFormat.format(date);
                        String content = new String(mqttMessage.getPayload());

                        System.out.println("\nReceived a Message!" +
                                "\n\tTime:    " + time +
                                "\n\tTopic:   " + topic +
                                "\n\tMessage: " + content +
                                "\n\tQoS:     " + mqttMessage.getQos() + "\n");
                        double temp = Double.valueOf(content);

                        Document document = new Document();
                        document.put("Timestamp", mqttKey);
                        document.put("MQTTTemperature", temp);
                        brokerCollection.insertOne(document);
                        System.out.println("Data has been inserted into database: " + database.getName());
                        producer.send(new ProducerRecord<>(topic, time, temp), (recordMetadata, e) -> {
                            if (e != null) {
                                e.printStackTrace();
                            } else {
                                System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n",
                                        recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                            }
                        });
                        producer.flush();
                    } catch (Exception e){
                        System.err.println(e);
                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });

            mqttClient.subscribe(topic, 0);
        }
}
