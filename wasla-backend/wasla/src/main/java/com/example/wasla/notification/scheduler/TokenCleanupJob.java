package com.example.wasla.notification.scheduler;


import com.example.wasla.notification.entity.DeviceToken;
import com.example.wasla.notification.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupJob {

    private final DeviceTokenRepository tokenRepository;
    private final static int staleDays = 90;


    /**
     * Runs every day at 2:00 AM.
     * Deactivates tokens that haven't been used in 90 days.
     *
     * Why 90 days? Users who haven't opened the app in 3 months are
     * very unlikely to have a valid token. Sending to them wastes quota
     * and inflates your failure metrics.
     */
    @Scheduled(cron = "0 0 2 * * *")   // 2:00 AM every day
    @Transactional
    public void cleanupStaleTokens() {

        LocalDateTime cutoff = LocalDateTime.now().minusDays(staleDays);


        List<String> oldTokens = tokenRepository.findOldTokens(cutoff)
                .stream()
                .map(DeviceToken::getToken)
                .toList();

        log.info("Token cleanup job started — Found {} stale tokens (unused since {})",
                oldTokens.size(), cutoff.toLocalDate());

        tokenRepository.deleteOldTokens(oldTokens);

        log.info("Token cleanup job complete — Deactivated {} tokens", oldTokens.size());




    }
}
