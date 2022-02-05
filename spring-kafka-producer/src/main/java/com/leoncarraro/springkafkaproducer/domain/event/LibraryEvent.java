package com.leoncarraro.springkafkaproducer.domain.event;

import com.leoncarraro.springkafkaproducer.domain.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEvent {

	private Long id;

	private Book book;

}
