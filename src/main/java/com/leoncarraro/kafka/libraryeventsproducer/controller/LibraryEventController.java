package com.leoncarraro.kafka.libraryeventsproducer.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leoncarraro.kafka.libraryeventsproducer.domain.event.LibraryEvent;
import com.leoncarraro.kafka.libraryeventsproducer.producer.LibraryEventProducer;

@RestController
@RequestMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class LibraryEventController {

	private final LibraryEventProducer libraryEventProducer;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LibraryEvent> create(@RequestBody final LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Request received");
		libraryEventProducer.sendUsingProducerRecord(libraryEvent);
		log.info("Returning response before Kafka receives the message");

		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}

}
