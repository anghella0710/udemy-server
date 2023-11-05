package com.java.udemy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.java.udemy.dto.CategoryDTO;
import com.java.udemy.models.Course;
import com.java.udemy.repository.CourseRepository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/courses", produces = MediaType.APPLICATION_JSON_VALUE)
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping(path = "/id/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable @NotNull Integer id) {
        return ResponseEntity.of(courseRepository.findById(id));
    }

    @GetMapping(path = "/cat/{category}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Course> getCoursesByCategory(@PathVariable @NotBlank String category) {
        var courseList = courseRepository.getCoursesByCategoryEquals(category);
        if (courseList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results for given category");
        }
        return courseList;
    }

    @GetMapping(path = "/top")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Course>> getAllTopCourses() {
        var courseList = courseRepository.getTop6CoursesByIsFeatured(true);
        CacheControl cc = CacheControl.maxAge(60, TimeUnit.MINUTES).cachePublic();
        return ResponseEntity.ok().cacheControl(cc).body(courseList);
    }

    @GetMapping(path = "/categories")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<CategoryDTO>> getCategoryListDistinct() {
        var categoryDTO = courseRepository.getAllDistinctCategories();
        CacheControl cc = CacheControl.maxAge(60, TimeUnit.MINUTES).cachePublic();
        return ResponseEntity.ok().cacheControl(cc).body(categoryDTO);
    }


    @GetMapping(path = "/search")
    @ResponseStatus(value = HttpStatus.OK)
    public Slice<Course> searchForCourseByTitle(@RequestParam(defaultValue = "") @NotBlank String title,
                                                @RequestParam(defaultValue = "0") Integer page) {
        if (title.length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query too short");
        }
        return courseRepository.getCoursesByTitleContaining(title, PageRequest.of(page, 10));
    }
    @PostMapping(path="/insert")
    ResponseEntity<Course> insertCourse(@RequestBody Course newCourse){
       
        return ResponseEntity.status(HttpStatus.OK).body(courseRepository.save(newCourse));
    }

    @PutMapping(path="/{id}")
    ResponseEntity<Course> updateCourse(@RequestBody Course newCourse,@PathVariable Integer id){
        Course updatedCourse=courseRepository.findById(id)
            .map(course->{
                course.setTitle(newCourse.getTitle());
                course.setSubtitle(newCourse.getSubtitle());
                course.setAuthor(newCourse.getAuthor());
                course.setCategory(newCourse.getCategory());
                course.setRating(newCourse.getRating());
                course.setThumbUrl(newCourse.getThumbUrl());
                return courseRepository.save(course);
            }).orElseGet(()->{
                newCourse.setId(id);
                return courseRepository.save(newCourse);
            });
        return ResponseEntity.status(HttpStatus.OK).body(updatedCourse);
    }
    
    @DeleteMapping(path="/{id}")
    ResponseEntity<String> deleteCourse(@PathVariable Integer id) {
        boolean exists = courseRepository.existsById(id);
        if (exists) {
            courseRepository.deleteById(id);
            return ResponseEntity.ok().body("Course with ID " + id + " has been successfully deleted.");
            
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

