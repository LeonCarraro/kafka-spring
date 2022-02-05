package com.leoncarraro.kafka.libraryeventsproducer.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leoncarraro.kafka.libraryeventsproducer.domain.event.LibraryEvent;

@RestController
@RequestMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class LibraryEventController {

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LibraryEvent> create(@RequestBody final LibraryEvent libraryEvent) {
		// TODO: Invoke kafka producer
		return ResponseEntity.created(null).body(libraryEvent);
	}

}
