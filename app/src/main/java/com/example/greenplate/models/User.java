package com.example.greenplate.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, Map<String, Integer>> meals;
    private String height;
    private String weight;
    private String gender;

    private String age;

    public User(String firstName, String lastName, String email, String height, String weight, String gender, String age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.meals = new HashMap<>();
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.age = age;
    }

    // Getter for firstName
    public String getFirstName() {
        return firstName;
    }

    // Setter for firstName
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Getter for lastName
    public String getLastName() {
        return lastName;
    }

    // Setter for lastName
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Map<String, Integer>> getMeals() {
        return meals;
    }

    public void setMeals(Map<String, Map<String, Integer>> meals) {
        this.meals = meals;
    }

    public void addMeal(String date, String mealName, Integer calories) {
        if (!this.meals.containsKey(date)) {
            this.meals.put(date, new HashMap<>());
        }
        this.meals.get(date).put(mealName, calories);
    }

    public String getHeight() {return height;}

    public void setHeight(String height) {
        if (Integer.parseInt(height) > 0) {
            this.height = height;
        }
    }

    public String getWeight() {return weight;}

    public void setWeight(String weight) {
        if (Integer.parseInt(weight) > 0) {
            this.weight = weight;
        }
    }

    public String getGender() {return gender;}

    public void setGender(String gender) {
        if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("Other")) {
            this.gender = gender;
        }
    }
  
    public String getAge() {return age;}

    public void setAge(String age) {
        if (Integer.parseInt(age) > 0) {
            this.age = age;
        }
    }

}
