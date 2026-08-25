package com.app.gighub.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.app.gighub.models.User;
import com.app.gighub.repositories.UserRepository;

public class UserValidator implements Validator {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"name", "user.name.empty", "Name field required");


        if( o instanceof User != true) {
            errors.reject("user.invalid-class");
            return ;
        }
        User user = (User) o;
        //
        // Check if password is empty or too short

        if(user.getPassword().length() < 6){
            errors.rejectValue("password", "user.password.short", "Password must be longer than 5 characters");
        }


        //
        // Check if email already exists:
        if( userRepository.findByEmail(user.getEmail()) != null){
            errors.rejectValue("email", "user.email.exists", "Duplicate e-mail");
        }

    }
}
