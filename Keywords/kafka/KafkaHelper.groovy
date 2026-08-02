package kafka

import com.kms.katalon.core.annotation.Keyword
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util.Properties

class KafkaHelper {

    /**
     * @param bootstrapServers alamat broker, contoh "localhost:9092"
     * @param topic            nama topic yang akan dikonsumsi
     * @param groupId          consumer group id
     * @param timeoutMs        lama polling menunggu pesan (ms)
     * @return List<String> isi pesan (value) yang diterima
     */
    @Keyword
    List<String> consumeMessages(String bootstrapServers, String topic, String groupId, long timeoutMs = 10000) {
        Properties props = new Properties()
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, 'earliest')
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, 'true')

        List<String> messages = []
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)
        try {
            consumer.subscribe(Collections.singletonList(topic))
            long start = System.currentTimeMillis()

            while (System.currentTimeMillis() - start < timeoutMs) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000))
                for (ConsumerRecord<String, String> record : records) {
                    println("Kafka message diterima -> partition: ${record.partition()}, offset: ${record.offset()}, value: ${record.value()}")
                    messages.add(record.value())
                }
                if (!messages.isEmpty()) {
                    break
                }
            }
        } finally {
            consumer.close()
        }
        return messages
    }
}
