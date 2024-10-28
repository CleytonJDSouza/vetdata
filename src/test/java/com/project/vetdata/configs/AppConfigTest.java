package com.project.vetdata.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = AppConfig.class)
public class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    public void given_appConfig_when_restTemplateBean_is_called_then_it_should_be_not_null() {
        RestTemplate result = appConfig.restTemplate();
        assertNotNull(result, "O bean RestTemplate não deve ser nulo");
    }
}


