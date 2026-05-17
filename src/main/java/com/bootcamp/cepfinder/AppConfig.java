package com.bootcamp.cepfinder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * Bean do RestTemplate usado pelo ViaCepService.
     * Declarado aqui para facilitar substituição/mock nos testes.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
