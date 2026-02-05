package com.aikyam.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class MatrixTokenCache {
	// Key = Matrix user_id, Value = access_token object
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    
    public String getToken(String matrixUserId) {
        return cache.get(matrixUserId);
    }

    public void storeToken(String matrixUserId, String accessToken) {
        cache.put(matrixUserId, accessToken);
    }

    public void removeToken(String matrixUserId) {
        cache.remove(matrixUserId);
    }
    
    public boolean hasSession(String matrixUserId) {
    	return cache.containsKey(matrixUserId);
    }
}
