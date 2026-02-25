package ProjetoJava.DonodoNegocio.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 10;

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        return attemptsCache.getOrDefault(key, 0) >= MAX_ATTEMPTS;
    }

    public void unblock(String key) {
        attemptsCache.remove(key);
    }
}