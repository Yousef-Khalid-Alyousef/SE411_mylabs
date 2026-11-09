package edu.psu.se411.lab14.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.psu.se411.lab14.model.Course;
import edu.psu.se411.lab14.repository.CourseRepository;

@Service
public class CourseService {

	private CourseRepository courseRepository;
	
	public CourseService(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}
	
	public Iterable<Course> getAllCourses() {
		return courseRepository.findAll();
	}
	
	public Optional<Course> getCourseById(Long id) {
		return courseRepository.findById(id);
	}
	
}
