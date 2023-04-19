import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisTemplate<String,User> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    String addUser(UserRequest userRequest){
        User user= User.builder().
                userName(userRequest.getUserName()).
                age(userRequest.getAge()).
                mobNo(userRequest.getMobNo()).build();

        //Save in the DB
        userRepository.save(user);

        //Save in the Cache
        saveInCache(user);

        return "User added successfully!";
    }

    public void saveInCache(User user){
        Map map= objectMapper.convertValue(user, Map.class);

        redisTemplate.opsForHash().putAll(user.getUserName(),map);
        redisTemplate.expire(user.getUserName(), Duration.ofHours(12));
    }

    public User findByUsername(String username){

        User user= null;

        //1. Find is redis cache
        Map map= redisTemplate.opsForHash().entries(username);

        //2.If not found in the redis/map
        if(map==null)
        {
            //3. Find the userobject from the DB
            user= userRepository.findByUsername(username);

            //4. Save in the Redis cache
            saveInCache(user);
        }
        else
        {
            user= objectMapper.convertValue(map,User.class);

        }
        return user;
    }
}
