package com.leoncarraro.springkafkaproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leoncarraro.springkafkaproducer.domain.event.LibraryEvent;
import com.leoncarraro.springkafkaproducer.producer.LibraryEventProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class LibraryEventController {

	private final LibraryEventProducer libraryEventProducer;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LibraryEvent> create(@RequestBody final LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Request received");
		libraryEventProducer.send(libraryEvent);
		log.info("Returning response before Kafka receives the message");

		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}

}
