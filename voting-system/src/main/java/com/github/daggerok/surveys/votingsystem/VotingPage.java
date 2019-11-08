package com.github.daggerok.surveys.votingsystem;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VotingPage {

    private static final String oops = UUID.randomUUID().toString();

    private final Environment env;

    @GetMapping
    Rendering index() {
        return Rendering.view("index")
                        .modelAttribute("message", env.getProperty("HOSTNAME", oops))
                        .build();
    }
}
