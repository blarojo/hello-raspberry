package com.blanpin.hello_raspberry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class HelloRaspberryApplication {

    private static final Logger log = LoggerFactory.getLogger(HelloRaspberryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HelloRaspberryApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("Application started successfully — listening on port 8080");
        log.info("Available endpoints: GET /  |  GET /status");
    }
}
