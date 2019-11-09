package com.github.daggerok.surveys.votingsystem.pubsub;

import com.github.daggerok.surveys.votingsystem.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.Rendering;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.util.Map;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/pub-sub")
class PubSubPage {

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
        return Rendering.view("redirect:/pub-sub/")
                        .build();
    }

    @GetMapping
    Rendering index(@ModelAttribute("message") Message message) {
        Publisher<String> hostname = client.get()
                                           .uri("/api/hostname")
                                           .exchange()
                                           .flatMap(r -> r.bodyToMono(String.class));
        var flow = Flux.from(hostname).map(host -> "starting...");
        var stream = new ReactiveDataDriverContextVariable(flow, 1);
        return Rendering.view("pub-sub")
                        .modelAttribute("hostname", hostname)
                        .modelAttribute("message", message)
                        .modelAttribute("stream", stream)
                        .build();
    }

    @GetMapping(path = "/stream", produces = TEXT_EVENT_STREAM_VALUE)
    Rendering stream() {
        var flow = client.get()
                         .uri("/api/pub-sub")
                         .retrieve()
                         .bodyToFlux(String.class);
        var stream = new ReactiveDataDriverContextVariable(flow, 1);
        return Rendering.view("pub-sub :: .pub-sub")
                        .modelAttribute("stream", stream)
                        .build();
    }
}
