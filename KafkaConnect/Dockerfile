FROM confluentinc/cp-kafka-connect:7.0.3

COPY target/components/packages/mongodb-kafka-connect-mongodb-1.7.0.zip /tmp/mongodb-kafka-connect-mongodb-1.7.0.zip
EXPOSE 8083
RUN confluent-hub install --no-prompt /tmp/mongodb-kafka-connect-mongodb-1.7.0.zip 