import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ResponseObject
import internal.GlobalVariable as GlobalVariable

import kafka.KafkaHelper

/*
 * Skenario 2: Pengujian menggunakan Kafka,
 * Katalon berperan sebagai CONSUMER (menerima/membaca pesan dari topic).
 */

KafkaHelper kafkaHelper = new KafkaHelper()

String bootstrapServers = 'localhost:9092'
String topic = 'test-topic'
String groupId = 'katalon-consumer-group'

List<String> messages = kafkaHelper.consumeMessages(bootstrapServers, topic, groupId, 15000)

assert messages != null
assert !messages.isEmpty() : 'Tidak ada pesan yang diterima dari topic ' + topic

println("Total pesan diterima: " + messages.size())
messages.each { msg ->
    println("Isi pesan: " + msg)
}

// validasi tambahan terhadap isi pesan
def firstMessage = new groovy.json.JsonSlurper().parseText(messages[0])
assert firstMessage != null
