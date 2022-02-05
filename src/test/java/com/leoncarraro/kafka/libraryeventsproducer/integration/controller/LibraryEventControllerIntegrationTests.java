package com.leoncarraro.kafka.libraryeventsproducer.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import com.leoncarraro.kafka.libraryeventsproducer.constant.KafkaTopicConstant;
import com.leoncarraro.kafka.libraryeventsproducer.domain.entity.Book;
import com.leoncarraro.kafka.libraryeventsproducer.domain.event.LibraryEvent;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("LibraryEventController Integration Tests")
@EmbeddedKafka(topics = { KafkaTopicConstant.TOPIC_NAME }, partitions = 3)
@Slf4j
class LibraryEventControllerIntegrationTests {

	private static final String ENDPOINT = "/events";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;

	private Consumer<Long, String> consumer;

	@BeforeEach
	void beforeEach() {
		Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("groupId", Boolean.TRUE.toString(), embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory<>(configs, new LongDeserializer(), new StringDeserializer()) //
				.createConsumer();

		embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, KafkaTopicConstant.TOPIC_NAME);
	}

	void afterEach() {
		consumer.close();
	}

	@Test
	@DisplayName("Should send one message to 'library-events' topic correctly")
	void shouldSendOneMessageToTopicCorrectly() {
		Book book = Book.builder() //
				.id(1L) //
				.name("Book name") //
				.authorName("Author name") //
				.build();

		LibraryEvent event = LibraryEvent.builder() //
				.id(null) //
				.book(book) //
				.build();

		ResponseEntity<LibraryEvent> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, getRequestEntity(event), LibraryEvent.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		ConsumerRecord<Long, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, KafkaTopicConstant.TOPIC_NAME);

		log.info(consumerRecord.toString());
	}

	private <T> HttpEntity<T> getRequestEntity(final T event) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		return new HttpEntity<T>(event, headers);
	}

}
