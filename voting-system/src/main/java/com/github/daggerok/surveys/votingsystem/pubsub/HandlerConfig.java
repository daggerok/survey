package com.github.daggerok.surveys.votingsystem.pubsub;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Collection;
import java.util.function.Consumer;

@Log4j2
@Service
@RequiredArgsConstructor
class HandlerConfig {

    private final Collection<FluxSink<ServerSentEvent<String>>> subscribers;

    @Bean
    Consumer<ServerSentEvent<String>> onNext() {
        return payload -> log.info("sending payload: {} to {} subscribers...",
                                   payload.data(), subscribers.size());
    }

    @Bean
    Consumer<Throwable> onError() {
        return err -> log.error("({}) f*ck... {}", subscribers.size(), err.getLocalizedMessage());
    }

    @Bean
    Runnable onComplete() {
        return () -> log.debug("({}) done!", subscribers.size());
    }

    @Bean
    ApplicationRunner handler(Flux<ServerSentEvent<String>> processor) {
        return args -> processor.subscribe(onNext(), onError(), onComplete());
    }
}
