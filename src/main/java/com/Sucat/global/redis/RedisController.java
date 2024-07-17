package com.Sucat.global.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/set")
    public ResponseEntity<?> setKeyValue() {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set("Korea", "Seoul");
        vop.set("America", "NewYork");
        vop.set("Italy", "Rome");
        vop.set("Japan", "Tokyo");
        return new ResponseEntity<>( HttpStatus.CREATED);
    }

    @GetMapping("/get/{key}")
    public ResponseEntity<?> getValueFromKey(@PathVariable String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String value = vop.get(key);
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

}

