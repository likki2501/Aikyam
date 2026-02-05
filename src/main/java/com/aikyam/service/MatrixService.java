package com.aikyam.service;

import java.util.Map;

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

}
