package com.project.majorproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisTemplate<String, User> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate kafkaTemplate;

    String addUser(UserRequest userRequest){
        User user= User.builder().
                userName(userRequest.getUserName()).
                age(userRequest.getAge()).
                mobNo(userRequest.getMobNo()).
                email(userRequest.getEmail()).
                name(userRequest.getName()).build();

        //Save in the DB
        userRepository.save(user);

        //Save in the Cache
        saveInCache(user);

        //Send an update to wallet module/ wallet service -> create a new wallet
        kafkaTemplate.send("create_wallet",user.getUserName());

        return "User added successfully!";
    }

    public void saveInCache(User user){
        Map map= objectMapper.convertValue(user, Map.class);
        String key= "USER_KEY"+ user.getUserName();
        System.out.println("The user key is "+key);
        redisTemplate.opsForHash().putAll(key,map);
        redisTemplate.expire(key, Duration.ofHours(12));
    }

    public User findByUsername(String userName){

        User user= null;

        //1. Find is redis cache
        Map map= redisTemplate.opsForHash().entries(userName);

        //2.If not found in the redis/map
        if(map==null)
        {
            //3. Find the userobject from the DB
            user= userRepository.findByUserName(userName);

            //4. Save in the Redis cache
            saveInCache(user);
        }
        else
        {
            user= objectMapper.convertValue(map, User.class);

        }
        return user;
    }

    public UserResponseDTO finEmailAndNameDTO(String userName)
    {
        User user= findByUsername(userName);

        UserResponseDTO userResponseDTO= new UserResponseDTO(user.getUserName(),user.getEmail());
        return userResponseDTO;
    }
}
