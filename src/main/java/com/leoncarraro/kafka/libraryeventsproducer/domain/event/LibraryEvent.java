package com.leoncarraro.kafka.libraryeventsproducer.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.leoncarraro.kafka.libraryeventsproducer.domain.entity.Book;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEvent {

	private Long id;

	private Book book;

}
