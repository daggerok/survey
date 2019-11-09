package com.github.daggerok.surveys.votingsystem.voting;

import com.github.daggerok.surveys.votingsystem.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Log4j2
@RestController
@RequiredArgsConstructor
class VotingResource {

    private static final String oops = UUID.randomUUID().toString();

    private final Environment env;
    private final Flux<String> longStream;

    @PostMapping("/api/message")
    void sendMessage(@RequestBody Message message) {
        String body = Objects.isNull(message) || Objects.isNull(message.getBody()) ? "oops" : message.getBody();
        log.info("received body {}", body);
    }

    @GetMapping("/api/hostname")
    Mono<String> getHostname() {
        return Mono.just(env.getProperty("HOSTNAME", oops));
    }

    @GetMapping(path = "/api/stream", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<String> getMessageStream() {
        return longStream;
    }
}
