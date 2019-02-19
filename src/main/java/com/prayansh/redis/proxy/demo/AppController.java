package com.prayansh.redis.proxy.demo;

import com.prayansh.redis.proxy.demo.app.RedisProxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import redis.clients.jedis.exceptions.JedisConnectionException;

@RestController
public class AppController {
    private RedisProxy proxy;


    @Autowired
    public AppController(@Value("${proxy.config.redis.hostname}") String hostName,
                         @Value("${proxy.config.redis.port}") int portNumber,
                         @Value("${proxy.config.cachesize}") long timeToExpireMillis,
                         @Value("${proxy.config.cachettl}") int cacheCapacity) {
        proxy = new RedisProxy(hostName, portNumber, timeToExpireMillis, cacheCapacity);
    }

    @GetMapping(path = "/")
    public ResponseEntity<String> get(@RequestParam("key") String key) {
        try {
            String val = proxy.get(key);
            if (val == null) {
                return new ResponseEntity<>("(nil)", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(val, HttpStatus.OK);
            }
        } catch (JedisConnectionException jce) {
            return new ResponseEntity<>("Backing redis instance not running", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PutMapping(path = "/")
    public ResponseEntity<String> set(@RequestParam("key") String key, @RequestParam("value") String value) {
        try {
            proxy.set(key, value);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (JedisConnectionException jce) {
            return new ResponseEntity<>("Backing redis instance not running", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping(path = "/ping")
    public ResponseEntity<String> ping() {
        try {
            String ping = proxy.ping();
            return new ResponseEntity<>(ping, HttpStatus.OK);
        } catch (JedisConnectionException jce) {
            return new ResponseEntity<>("Backing redis instance not running", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @DeleteMapping(path = "/clear")
    public ResponseEntity<String> clear() {
        // Ideally should be under some kind of authentication
        // since no auth is present this implementation is strictly for demo purposes
        String s = proxy.flushDB();
        return new ResponseEntity<>(s, HttpStatus.OK);
    }
}
