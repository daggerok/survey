package com.github.daggerok.surveys.votingsystem.voting;

import com.github.daggerok.surveys.votingsystem.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.Rendering;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
class VotingPage {

    private final WebClient client;

    @PostMapping
    Rendering post(@ModelAttribute("message") Message message) {
        log.info("req: {}", message);
        client.post()
              .uri("/api/message")
              .body(BodyInserters.fromValue(message))
              .exchange()
              .flatMap(r -> r.bodyToMono(Map.class))
              .subscribe(log::info);
        return Rendering.view("redirect:/")
                        .build();
    }

    @GetMapping
    Rendering index(@ModelAttribute("message") Message message) {
        var hostname = client.get()
                             .uri("/api/hostname")
                             .exchange()
                             .flatMap(r -> r.bodyToMono(String.class));
        var stringFlux = client.get()
                               .uri("/api/stream")
                               .retrieve()
                               .bodyToFlux(String.class);
        var stream = new ReactiveDataDriverContextVariable(stringFlux, 1);
        return Rendering.view("index")
                        .modelAttribute("hostname", hostname)
                        .modelAttribute("message", message)
                        .modelAttribute("stream", stream)
                        .build();
    }
}
