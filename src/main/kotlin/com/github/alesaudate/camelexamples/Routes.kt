package com.github.alesaudate.camelexamples

import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.BindyType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

const val INPUT_RAW_DATA_CHANNEL = "direct://input_raw_data"
const val INPUT_SPLITTER_DATA_CHANNEL = "direct://split_data"
const val INPUT_STRUCTURED_DATA_CHANNEL = "direct://structured_data"


@Component
class ReadFromFile(@Value("\${sample.directory.input}") val fileLocation: String) : RouteBuilder() {

    override fun configure() {
        from("file://$fileLocation")
            .log("Reading file \${file:name}")
            .to(INPUT_SPLITTER_DATA_CHANNEL)
    }
}


@Component
class SplitData() : RouteBuilder() {

    override fun configure() {
        from(INPUT_SPLITTER_DATA_CHANNEL)
            .split(body().tokenize("\n")).streaming()
            .log(LoggingLevel.DEBUG, "Splitted line: \${body}")
            .to(INPUT_RAW_DATA_CHANNEL)
    }

}


@Component
class DataUnmarshaller() : RouteBuilder() {

    override fun configure() {
        from(INPUT_RAW_DATA_CHANNEL)
            .log(LoggingLevel.DEBUG, "About to unmarshall data from CSV to Record")
            .unmarshal().bindy(BindyType.Csv, Record::class.java)
            .log(LoggingLevel.DEBUG, "Unmarshalling succeeded")
            .log(LoggingLevel.DEBUG, "Message body is of type \${body.getClass()} : \${body}")
            .to(INPUT_STRUCTURED_DATA_CHANNEL)
    }
}

@Component
class KafkaSender(@Value("\${sample.kafka.topic}") val topic: String) : RouteBuilder() {
    override fun configure() {
        from(INPUT_STRUCTURED_DATA_CHANNEL)
            .log(LoggingLevel.DEBUG, "About to send \${body} to Kafka")
            .to("kafka:$topic")
    }
}


@Component
class KafkaListener(@Value("\${sample.kafka.topic}") val topic: String) : RouteBuilder() {
    override fun configure() {
        from("kafka:$topic")
            .log(LoggingLevel.INFO, "Just received a record from Kafka: \${body}")
    }
}