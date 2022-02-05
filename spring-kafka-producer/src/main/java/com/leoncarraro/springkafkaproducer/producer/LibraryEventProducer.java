package com.leoncarraro.springkafkaproducer.producer;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoncarraro.springkafkaproducer.constant.KafkaTopicConstant;
import com.leoncarraro.springkafkaproducer.domain.event.LibraryEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
@AllArgsConstructor
public class LibraryEventProducer {

	private final KafkaTemplate<Long, String> kafkaTemplate;

	private final ObjectMapper objectMapper;

	public void send(final LibraryEvent libraryEvent) throws JsonProcessingException {
		Long key = libraryEvent.getId();
		String payload = objectMapper.writeValueAsString(libraryEvent.getBook());

		ProducerRecord<Long, String> producerRecord = buildProducerRecord(key, payload);

		ListenableFuture<SendResult<Long, String>> listenableFuture = kafkaTemplate.send(producerRecord);
		listenableFuture.addCallback(new ListenableFutureCallback<>() {

			@Override
			public void onSuccess(final SendResult<Long, String> result) {
				handleSuccess(key, payload, result);
			}

			@Override
			public void onFailure(@Nullable final Throwable ex) {
				handleFailure(key, payload, ex);
			}
		});
	}

	private ProducerRecord<Long, String> buildProducerRecord(final Long key, final String payload) {
		List<Header> headers = List.of( //
				new RecordHeader("metadata-1-key", "metadata-1-value".getBytes()), //
				new RecordHeader("metadata-2-key", "metadata-2-value".getBytes()));

		return new ProducerRecord<>(KafkaTopicConstant.TOPIC_NAME, null, null, key, payload, headers);
	}

	private void handleFailure(final Long key, final String payload, final Throwable ex) {
		log.error("Failure to send event");
		log.error("Key: {}", key);
		log.error("Payload: {}", payload);

		if (Objects.nonNull(ex)) {
			log.error("Error description: {}", ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void handleSuccess(final Long key, final String payload, final SendResult<Long, String> result) {
		log.info("Successfully sent event");
		log.info("Key: {}", key);
		log.info("Payload: {}", payload);
		log.info("Partition: {}", result.getRecordMetadata().partition());
	}

}
