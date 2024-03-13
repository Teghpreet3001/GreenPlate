package com.example.greenplate;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.greenplate.models.User;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ShreyashiUnitTest {


    @Test
    public void negativeHeight() {
        User user = new User("Shreyashi", "Dutta", "sdutta302@gatech.edu", "-65", "130", "Female", "19");

        // Asserting that the age is negative
        assertTrue("Age should be negative", Integer.parseInt(user.getAge()) < 0);
    }
    @Test
    public void negativeWeight() {
        User user = new User("Shreyashi", "Dutta", "sdutta302@gatech.edu", "65", "-130", "Female", "19");

        // Asserting that the age is negative
        assertTrue("Age should be negative", Integer.parseInt(user.getAge()) < 0);
    }
}
