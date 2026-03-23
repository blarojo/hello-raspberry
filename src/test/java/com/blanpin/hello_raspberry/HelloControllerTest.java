package com.blanpin.hello_raspberry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void helloEndpointReturnsExpectedMessage() {
        assertThat(mvc.get().uri("/"))
                .hasStatusOk()
                .hasBodyTextEqualTo("Hello from Raspberry Pi!");
    }

    @Test
    void statusEndpointReturnsJsonWithStatusUpAndTime() {
        assertThat(mvc.get().uri("/status"))
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .extractingPath("$.status").isEqualTo("UP");

        assertThat(mvc.get().uri("/status"))
                .bodyJson()
                .hasPath("$.time");
    }
}
