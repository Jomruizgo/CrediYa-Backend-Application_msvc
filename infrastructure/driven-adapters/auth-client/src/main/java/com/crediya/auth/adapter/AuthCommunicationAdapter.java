package com.crediya.auth.adapter;

import com.crediya.auth.client.AuthServiceClient;
import com.crediya.auth.dto.UpdateUserDocumentRequestDto;
import com.crediya.auth.util.AuthErrorMessages;
import com.crediya.gatewayport.IAuthCommunicationPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
                        UpdateUserDocumentRequestDto updateRequest = new UpdateUserDocumentRequestDto(
                            user.name(),
                            user.lastName(),
                            documentId,
                            user.birthDate(),
                            user.address(),
                            user.phoneNumber(),
                            user.email(),
                            user.baseSalary(),
                            user.role()
                        );
                        return authServiceClient.updateUser(userId, updateRequest);
                    } else if (!user.documentId().equals(documentId)) {
                        return Mono.error(new IllegalArgumentException(AuthErrorMessages.DOCUMENT_ID_MISMATCH));
                    } else {
                        return Mono.just(user);
                    }
                })
                .then();
    }
}