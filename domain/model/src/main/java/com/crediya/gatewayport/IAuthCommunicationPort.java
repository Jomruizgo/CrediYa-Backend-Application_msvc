package com.crediya.gatewayport;

import com.crediya.model.UserInfo;
import reactor.core.publisher.Mono;

public interface IAuthCommunicationPort {
    
    Mono<Void> validateAndUpdateUserDocument(Long userId, String documentId);
    
    Mono<UserInfo> getUserInfo(Long userId);
}