package edu.psu.se411.lab14.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.psu.se411.lab14.model.Course;
import edu.psu.se411.lab14.service.CourseService;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
	
	private CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}
	
	@GetMapping()
	public ResponseEntity<Iterable<Course>> getAllCourses() {
		return ResponseEntity.ok(courseService.getAllCourses());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
		Optional<Course> op = courseService.getCourseById(id);
		if(op.isPresent()) {
			return ResponseEntity.ok(op.get());
		}
		return ResponseEntity.notFound().build();
	}
	
}
