package com.example.wasla.user.client.service;

import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Client profile management service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Client findById(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public Client updateProfile(UUID clientId, String fullName, String phone) {
        Client client = findById(clientId);
        
        if (fullName != null) {
            client.setFullName(fullName);
        }
        if (phone != null) {
            client.setPhone(phone);
        }
        
        Client saved = clientRepository.save(client);
        log.info("Client {} profile updated", clientId);
        return saved;
    }

    @Transactional
    public void updateFcmToken(UUID clientId, String fcmToken) {
        Client client = findById(clientId);
        client.setFcmToken(fcmToken);
        clientRepository.save(client);
        log.info("Client {} FCM token updated", clientId);
    }
}
