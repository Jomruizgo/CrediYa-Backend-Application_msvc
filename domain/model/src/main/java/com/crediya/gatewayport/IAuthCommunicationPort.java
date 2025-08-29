package com.crediya.gatewayport;

import reactor.core.publisher.Mono;

public interface IAuthCommunicationPort {
    
    Mono<Void> validateAndUpdateUserDocument(Long userId, String documentId);
}