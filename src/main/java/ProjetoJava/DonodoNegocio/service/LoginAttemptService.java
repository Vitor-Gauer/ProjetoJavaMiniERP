package ProjetoJava.DonodoNegocio.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_SECONDS = 10;

    private final Map<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        AttemptInfo attemptInfo = attemptsCache.computeIfAbsent(key, k -> new AttemptInfo());
        attemptInfo.incrementAttempts();
    }

    public boolean isBlocked(String key) {
        AttemptInfo attemptInfo = attemptsCache.get(key);
        if (attemptInfo == null) {
            return false;
        }

        if (attemptInfo.isBlockExpired()) {
            attemptsCache.remove(key);
            return false;
        }

        return attemptInfo.getAttempts() >= MAX_ATTEMPTS;
    }

    public void unblock(String key) {
        attemptsCache.remove(key);
    }

    private static class AttemptInfo {
        @Getter
        private int attempts;

        private LocalDateTime blockStartTime;
        private LocalDateTime lastAttemptTime;

        public AttemptInfo() {

            this.attempts = 0;
            this.lastAttemptTime = LocalDateTime.now();
        }

        public void incrementAttempts() {
            attempts++;
            lastAttemptTime = LocalDateTime.now();

            if (attempts >= MAX_ATTEMPTS) {
                blockStartTime = LocalDateTime.now();
            }
        }

        public boolean isBlockExpired() {
            if (blockStartTime == null) {
                return false;
            }

            LocalDateTime now = LocalDateTime.now();
            long secondsSinceBlock = java.time.Duration.between(blockStartTime, now).getSeconds();

            if (secondsSinceBlock >= BLOCK_DURATION_SECONDS) {
                attempts = 0;
                blockStartTime = null;
                return true;
            }

            return false;
        }
    }
}