package com.example.greenplate.sprint2;

import static org.junit.Assert.assertFalse;
import com.example.greenplate.viewmodels.SignUpViewModel;

import org.junit.Test;

public class SignUpUnitTest {
    // Unnathi's Test
    @Test
    public void preventsSignUpWithNullField() {
        SignUpViewModel signUpViewModel = new SignUpViewModel();
        boolean isInputValid = signUpViewModel.handleInputData(null, null,
                null, null);
        assertFalse(isInputValid);
    }
}
