package com.crediya.auth.adapter;

import com.crediya.auth.client.AuthServiceClient;
import com.crediya.auth.util.AuthErrorMessages;
import com.crediya.gatewayport.IAuthCommunicationPort;
import com.crediya.model.UserInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class AuthCommunicationAdapter implements IAuthCommunicationPort {
    
    private final AuthServiceClient authServiceClient;
    
    public AuthCommunicationAdapter(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }
    
    @Override
    public Mono<Void> validateAndUpdateUserDocument(Long userId, String documentId) {
        return authServiceClient.getUserById(userId)
                .flatMap(user -> {
                    if (user.documentId() == null || user.documentId().isEmpty()) {
                        return Mono.error(new IllegalArgumentException(AuthErrorMessages.DOCUMENT_ID_NOT_FOUND));
                    } else if (!user.documentId().equals(documentId)) {
                        return Mono.error(new IllegalArgumentException(AuthErrorMessages.DOCUMENT_ID_MISMATCH));
                    } else {
                        return Mono.just(user);
                    }
                })
                .then();
    }

    @Override
    public Mono<UserInfo> getUserInfo(Long userId) {
        return authServiceClient.getUserById(userId)
                .map(userDto -> new UserInfo(
                        userDto.email(),
                        userDto.name() + " " + userDto.lastName(),
                        userDto.baseSalary()
                ));
    }
}