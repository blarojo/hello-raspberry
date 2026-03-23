package com.blanpin.hello_raspberry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/")
    public String hello() {
        log.info("GET / called");
        return "Hello from Raspberry Pi!";
    }

    @GetMapping("/status")
    public StatusResponse status() {
        StatusResponse response = new StatusResponse("UP", Instant.now().toString());
        log.info("GET /status called — returning status={}, time={}", response.status(), response.time());
        return response;
    }
}

record StatusResponse(String status, String time) {}
