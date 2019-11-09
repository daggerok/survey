package com.github.daggerok.surveys.votingsystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
class MessagesStreamConfig {

    @Bean
    Flux<String> longStream() {
        return Flux.from(Flux.interval(Duration.ofMillis(456))
                             .take(Duration.ofSeconds(9)))
                   .map(n -> String.format("%d: %s | %s", n, LocalDateTime.now(), UUID.randomUUID()))
                   .share();
    }
}
