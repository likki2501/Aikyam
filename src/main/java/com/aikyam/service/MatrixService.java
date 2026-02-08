package com.aikyam.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.aikyam.dto.LoginRequest;
import com.aikyam.dto.MatrixLoginRequest;
import com.aikyam.dto.MatrixLoginResponse;
import com.aikyam.dto.MatrixTokenCache;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MatrixService {

    @Value("${matrix.base.url}")
    private String matrixBaseUrl;

    @Value("${matrix.admin.token}")
    private String adminToken;

    @Value("${matrix.domain}")
    private String matrixDomain;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate;
    private final MatrixTokenCache tokenCache;
 // stores sync tokens per user
    private final Map<String, String> syncTokens = new ConcurrentHashMap<>();


    public MatrixService(RestTemplate restTemplate,
            MatrixTokenCache tokenCache) {
        this.restTemplate = restTemplate;
        this.tokenCache = tokenCache;
    }

    public String createMatrixUser(String username, String password) {
        String matrixUserId = "@" + username + ":" + matrixDomain;
        String url = matrixBaseUrl + "/_synapse/admin/v2/users/" + matrixUserId; // Admin endpoint

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken); // Admin token

        Map<String, Object> body = Map.of(
                "password", password,
                "admin", false,
                "deactivated", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            System.out.println("Matrix user created successfully!");
        } catch (HttpClientErrorException e) {
            System.err.println("Matrix error: " + e.getResponseBodyAsString());
        }

        return matrixUserId;
    }

    public MatrixLoginResponse login(@RequestBody LoginRequest request) {

        final String username = request.getUsername();
        final String password = request.getPassword();
        System.out.println(username);
        System.out.println(password);
        final String url = matrixBaseUrl + "/_matrix/client/v3/login";
        MatrixLoginRequest loginRequest = new MatrixLoginRequest();
        MatrixLoginRequest.Identifier identifier = new MatrixLoginRequest.Identifier();
        identifier.setUser(username);
        loginRequest.setIdentifier(identifier);
        loginRequest.setPassword(password);

        // System.out.println(objectMapper.writeValueAsString(loginRequest));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MatrixLoginRequest> entity = new HttpEntity<MatrixLoginRequest>(loginRequest, headers);
        try {
            ResponseEntity<MatrixLoginResponse> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity,
                    MatrixLoginResponse.class);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException(
                    "Matrix Login Failed" + e.getResponseBodyAsString());
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> syncMessages(String matrixUserId,String password) {

        String accessToken = tokenCache.getToken(matrixUserId);
        if (accessToken == null) {
        	LoginRequest request = new LoginRequest();
        	request.setUsername(matrixUserId);
        	request.setPassword(password);
        	MatrixLoginResponse response = login(request);
        	accessToken = response.getAccess_token();
        	tokenCache.storeToken(matrixUserId, accessToken);
        }

        String since = syncTokens.get(matrixUserId);

        String url = matrixBaseUrl + "/_matrix/client/v3/sync?timeout=30000";
        if (since != null) {
            url += "&since=" + since;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

        Map<String, Object> body = response.getBody();

        // store next sync token
        String nextBatch = (String) body.get("next_batch");
        syncTokens.put(matrixUserId, nextBatch);

        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> rooms =
                (Map<String, Object>) body.get("rooms");
        if (rooms == null) return messages;

        Map<String, Object> join =
                (Map<String, Object>) rooms.get("join");
        if (join == null) return messages;

//        for (Object roomObj : join.values()) {
//
//            Map<String, Object> room =
//                    (Map<String, Object>) roomObj;
//
//            Map<String, Object> timeline =
//                    (Map<String, Object>) room.get("timeline");
//
//            if (timeline == null) continue;
//
//            List<Map<String, Object>> events =
//                    (List<Map<String, Object>>) timeline.get("events");
//
//            if (events == null) continue;
//
//            for (Map<String, Object> event : events) {
//                if ("m.room.message".equals(event.get("type"))) {
//                    messages.add(event);
//                }
//            }
//        }
        for (Object roomObj : join.values()) {

            Map<String, Object> room =
                    (Map<String, Object>) roomObj;

            Map<String, Object> timeline =
                    (Map<String, Object>) room.get("timeline");

            if (timeline == null) continue;

            List<Map<String, Object>> events =
                    (List<Map<String, Object>>) timeline.get("events");

            if (events == null) continue;

            for (Map<String, Object> event : events) {
                if ("m.room.message".equals(event.get("type"))) {

                    Map<String, Object> content =
                            (Map<String, Object>) event.get("content");

                    if (content == null) continue;

                    Map<String, Object> msg = new HashMap<>();
                    msg.put("event_id", event.get("event_id"));
                    msg.put("sender", event.get("sender"));
                    msg.put("body", content.get("body"));

                    messages.add(msg);
                }
            }
        }

        return messages;
    }
    

}
