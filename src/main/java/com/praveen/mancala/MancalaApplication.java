package com.praveen.mancala;

import com.praveen.mancala.model.Pit;
import com.praveen.mancala.payload.PitDto;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.LinkedList;

@SpringBootApplication
public class MancalaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MancalaApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		modelMapper.typeMap(Pit.class, PitDto.class).addMappings(mapper -> {
			mapper.map(src -> src.getNext().getId(), PitDto::setNext);
			mapper.map(src -> src.getOpposite().getId(), PitDto::setOpposite);
		});

		return modelMapper;
	}
}
