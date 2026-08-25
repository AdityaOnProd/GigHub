package com.app.gighub.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.gighub.models.User;
import com.app.gighub.services.UserService;

public abstract class AbstractController {

    @Autowired
    UserService userService;
    /**
     * Get logged user
     *
     * @return net.vatri.freelanceplatform.models.User
     **/
    protected User getCurrentUser(){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails == false) {
            return null;
        }
        String username = ((UserDetails) principal).getUsername();

        return  userService.getByEmail(username);
    }
}
