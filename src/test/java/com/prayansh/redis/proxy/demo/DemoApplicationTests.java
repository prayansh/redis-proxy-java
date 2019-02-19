package com.prayansh.redis.proxy.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private MockMvc mvc;

    @Test
    public void testPing() throws Exception {
        this.mvc.perform(get("/ping")).andExpect(status().isOk())
                .andExpect(content().string("PONG"));
    }

    @Test
    public void getUnknownTest() throws Exception {
        this.mvc.perform(get("/").param("key", "unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("(nil)"));
    }

    @Test
    public void getFooTest() throws Exception {
        this.mvc.perform(put("/").param("key", "foo").param("value", "bar"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
        this.mvc.perform(put("/").param("key", "bar").param("value", "foo"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
        this.mvc.perform(get("/").param("key","foo"))
                .andExpect(status().isOk())
                .andExpect(content().string("bar"));
        this.mvc.perform(get("/").param("key","bar"))
                .andExpect(status().isOk())
                .andExpect(content().string("foo"));
    }
}
