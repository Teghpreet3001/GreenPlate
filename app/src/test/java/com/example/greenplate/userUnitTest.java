package com.example.greenplate;
import org.junit.Test;
import static org.junit.Assert.*;
import com.example.greenplate.models.User;


public class userUnitTest {
    // Rachit's Tests
    @Test
    public void testUpdateHeight(){
        User user = new User("John", "Doe", "johndoe@gmail.com",
                "80", "140", "Male", "18");
        user.setHeight("70");
        assertEquals("70", user.getHeight());
    }
    @Test
    public void testUpdateWeight() {
        User user = new User("John", "Doe", "johndoe@gmail.com",
                "80", "140", "Male", "18");
        user.setWeight("150");
        assertEquals("150",user.getWeight());
    }
    // Shreyashi's Tests
    @Test
    public void negativeHeight() {
        User user = new User("Teghpreet", "Singh", "teg3001@gamil.com", "180", "70", "Male", "21");
        user.setHeight("-80");
        String height = user.getHeight();
        assertEquals("Height will not change","180",height);
    }
    @Test
    public void negativeWeight() {
        User user = new User("Teghpreet", "Singh", "teg3001@gamil.com", "180", "70", "Male", "21");
        user.setWeight("-80");
        String weight = user.getWeight();
        assertEquals("Height will not change","70",weight);
    }

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
