FROM openjdk:17-jdk
ADD out/artifacts/TemperatureSensor_jar/TemperatureSensor.jar .
EXPOSE 7777
CMD java -jar TemperatureSensor.jar --envname=prod