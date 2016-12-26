package com.stone.rx2.bean;

import java.util.List;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 16/12/2016 17 00
 */
public class Student {

    private String name;
    private int age;
    private List<Course> courseList;

    public Student() {
    }

    public Student(int age, String name, List<Course> courses) {
        this.age = age;
        this.name = name;
        this.courseList = courses;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public static class Course {
        private String name;

        public Course() {
        }

        public Course(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
