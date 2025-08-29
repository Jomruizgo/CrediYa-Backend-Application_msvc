package com.crediya.api.router;

import com.crediya.api.handler.LoanTypeHandler;
import com.crediya.api.util.ApiPaths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class LoanTypeRouterRest {
    
    @Bean
    public RouterFunction<ServerResponse> loanTypeRoutes(LoanTypeHandler loanTypeHandler) {
        return route(GET(ApiPaths.LOAN_TYPE_BASE).and(accept(MediaType.APPLICATION_JSON)),
                    loanTypeHandler::getAllActiveLoanTypes)
                .andRoute(GET(ApiPaths.LOAN_TYPE_BY_ID),
                    loanTypeHandler::getLoanTypeById);
    }
}