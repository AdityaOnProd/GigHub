package com.app.gighub.services;

import com.app.gighub.models.User;
import com.app.gighub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User get(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public User getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User save(User user){
		user.setPassword( passwordEncoder.encode( user.getPassword() )  );
        return userRepository.save(user);
    }

}
