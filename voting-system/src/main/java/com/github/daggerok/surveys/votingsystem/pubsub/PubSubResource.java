package com.github.daggerok.surveys.votingsystem.pubsub;

import com.github.daggerok.surveys.votingsystem.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pub-sub")
class PubSubResource {

    private final Consumer<String> publisher;
    private final Flux<ServerSentEvent<String>> processor;
    private final Collection<FluxSink<ServerSentEvent<String>>> subscribers;

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<String>> subscribe(ServerWebExchange exchange) {
        log.info("new subscriber joined... total: {}", subscribers.size());
        return processor.share();
    }

    /*
    @PostMapping
    Mono<String> publish(@RequestBody Message message) {
        log.info("handling request {}", message);
        return Optional.ofNullable(message)
                       .map(Message::getBody)
                       .map(body -> Mono.just(body)
                                        .handle((data, sink) -> Optional.ofNullable(data)
                                                                        .ifPresent(sink::next))
                                        .filter(Objects::nonNull)
                                        .doOnNext(publisher)
                                        .map(b -> String.format("message '%s' sent.", b)))
                       .orElse(Mono.just("message body hasn't been found..."));
    }
    */

    @PostMapping
    Mono<String> publish(@RequestBody Message message) {
        log.info("handling request {}", message);
        return Mono.justOrEmpty(message)
                   .handle((msg, sink) -> Optional.ofNullable(msg.getBody())
                                                  .ifPresent(sink::next))
                   .map(String::valueOf)
                   .doOnNext(publisher)
                   .map(b -> String.format("message '%s' has been sent.", b))
                   .switchIfEmpty(Mono.just("message body hasn't been found..."));
    }
}
