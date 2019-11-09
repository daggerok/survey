package com.github.daggerok.surveys.votingsystem.pubsub;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Log4j2
@Configuration
class PubSubConfig {

    @Bean
    Collection<FluxSink<ServerSentEvent<String>>> subscribers() {
        return new CopyOnWriteArrayList<>();
    }

    @Bean
    Flux<ServerSentEvent<String>> processor(Collection<FluxSink<ServerSentEvent<String>>> subscribers) {
        return Flux.create(fluxSink -> subscribers.add(
                fluxSink.onCancel(() -> {
                            log.info("cancelling processing!");
                            subscribers.remove(fluxSink);
                        })
                        .onDispose(() -> log.debug("disposing..."))
                        .onRequest(i -> log.debug("{} subscribers on request", subscribers.size()))));
    }

    @Bean
    Consumer<String> publisher(Environment env,
                               Collection<FluxSink<ServerSentEvent<String>>> subscribers) {

        return message -> subscribers.forEach(
                fluxSink -> fluxSink.next(
                        ServerSentEvent.<String>builder()
                                       .id(env.getProperty("HOSTNAME", UUID.randomUUID().toString()))
                                       .comment(":: id its either env.HOSTNAME or random UUID")
                                       .data(message)
                                       .event("message")
                                       .build()));
    }
}
