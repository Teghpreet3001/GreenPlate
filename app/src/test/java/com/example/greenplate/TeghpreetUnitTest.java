package com.example.greenplate;

import org.junit.Test;
import static org.junit.Assert.*;
import com.example.greenplate.models.User;

public class TeghpreetUnitTest {

    User user = new User("Teghpreet", "Singh", "teg3001@gamil.com", "180", "70", "Male", "21");

    @Test
    public void negativeAge() {
        user.setAge("-21");
        String age = user.getAge();
        assertEquals("Age will not change","21",age);
    }

    @Test
    public void inValidGender() {
        user.setGender("not a valid gender");
        String gender = user.getGender();
        assertEquals("Gender will not change", "Male", gender);
    }

}


