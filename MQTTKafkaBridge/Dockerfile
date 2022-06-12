FROM openjdk:17-jdk
ADD out/artifacts/MQTTKafkaBridge_jar/MQTTKafkaBridge.jar .
EXPOSE 4444
CMD java -jar MQTTKafkaBridge.jar --envname=prod