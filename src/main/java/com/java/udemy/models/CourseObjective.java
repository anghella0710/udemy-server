package com.java.udemy.models;

import jakarta.persistence.*;

import java.util.Objects;

import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_objectives")
@Getter
@Setter
@RequiredArgsConstructor
public class CourseObjective {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "course_id")
  @JsonBackReference
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Course course;

  @Size(max = 200)
  private String objective;

  public CourseObjective(Course course, @Size(max = 200) String objective) {
    super();
    this.course = course;
    this.objective = objective;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    CourseObjective that = (CourseObjective) o;

    if (!Objects.equals(id, that.id))
      return false;
    if (!Objects.equals(course, that.course))
      return false;
    return Objects.equals(objective, that.objective);
  }

  @Override
  public int hashCode() {
    return Objects.hash(course, objective);
  }

}
