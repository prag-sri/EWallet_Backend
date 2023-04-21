package com.project.majorproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/add")
    String createUser(@RequestBody UserRequest userRequest){
        return userService.addUser(userRequest);
    }

    @GetMapping("/findByUsername/{userName}")
    User findByUsername(@PathVariable("userName") String userName){
        return userService.findByUsername(userName);
    }

    @GetMapping("/findEmailDTO/{userName}")
    public UserResponseDTO getEmailNameDTO(@PathVariable("userName") String userName)
    {
        return userService.finEmailAndNameDTO(userName);
    }
}
