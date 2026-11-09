package edu.psu.se411.lab14.model;

import org.springframework.data.annotation.Id;

public record Course(
			@Id Long id,
			String code,
			String name,
			String description,
			Integer credits
			
		) {}
