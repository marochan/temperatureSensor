package com.ts;

import org.eclipse.paho.client.mqttv3.*;

import java.net.http.WebSocket;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;
public class Sensor {




    public Sensor() throws MqttException {
    }

    public static void main(String[] args) throws MqttException, InterruptedException {
        String publisherId = "Temperature sensor v1.0";
        String topic = "TEMPERATURE";
        IMqttClient mqttClient = new MqttClient("tcp://172.17.0.1:1883", publisherId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(10);
        mqttClient.connect(options);
        while (true){
           MqttMessage message = new MqttMessage();
           double temp = readTemperature();
           String bytes = String.valueOf(temp);
           byte[] payload = bytes
                    .getBytes();
           message.setQos(0);
           message.setRetained(true);
           message.setPayload(payload);
           mqttClient.publish(topic, message);

           System.out.println("Publishing: " + temp + " on topic: " + topic);
           Thread.sleep(3000);
        }
    }
    public static double readTemperature(){

        double temp = ThreadLocalRandom.current().nextDouble(18,19);
        int trimmed = (int)(temp*100.0);
        double shortDouble = ((double)trimmed)/100.0;
        return shortDouble;
    }
}
