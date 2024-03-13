package com.example.greenplate;

import org.junit.Test;

import static org.junit.Assert.*;



import com.example.greenplate.models.User;


public class userUnitTest {
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
}
