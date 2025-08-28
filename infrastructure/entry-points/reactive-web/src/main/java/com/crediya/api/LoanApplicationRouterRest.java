package com.crediya.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class LoanApplicationRouterRest {
    
    @Bean
    public RouterFunction<ServerResponse> loanApplicationRoutes(LoanApplicationHandler loanApplicationHandler) {
        return route(POST("/api/v1/application").and(accept(MediaType.APPLICATION_JSON)),
                    loanApplicationHandler::registerApplication)
                .andRoute(GET("/api/v1/application/{id}"),
                    loanApplicationHandler::getApplicationById);
    }
}
