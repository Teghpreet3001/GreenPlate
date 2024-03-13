package com.example.greenplate;

import org.junit.Test;

import static org.junit.Assert.*;



import com.example.greenplate.models.User;


public class userUnitTest {
    // Aditya's Tests
    @Test
        public void testAgeUpdate() {
        User user = new User("Aditya", "Kabu", "kabuaditya@example.com", "80", "180", "Male", "20");
        user.setAge("21");
        assertEquals("Age should be 21", "21", user.getAge());
    }

    @Test
    public void testGenderUpdate() {
        User user = new User("Aditya", "Kabu", "kabuaditya@example.com", "80", "180", "Female", "20");
        user.setGender("Male");
        assertEquals("Gender should now be Male", "Male", user.getGender());
    }

    // Teghpreet's Tests
    @Test
    public void negativeAge() {
        User user = new User("Teghpreet", "Singh", "teg3001@gamil.com", "180", "70", "Male", "21");
        user.setAge("-21");
        String age = user.getAge();
        assertEquals("Age will not change","21",age);
    }

    @Test
    public void invalidGender() {
        User user = new User("Teghpreet", "Singh", "teg3001@gamil.com", "180", "70", "Male", "21");
        user.setGender("not a valid gender");
        String gender = user.getGender();
        assertEquals("Gender will not change", "Male", gender);
    }
}
