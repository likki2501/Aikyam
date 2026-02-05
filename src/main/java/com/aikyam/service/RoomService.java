package com.aikyam.service;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.aikyam.model.MatrixMessage;
import com.aikyam.repository.MatrixMessageRepository;

@Service
public class RoomService {
	
	@Value("${matrix.base.url}")
	private String matrixBaseUrl;
	@Autowired
	private  RestTemplate restTemplate;
	@Autowired
	private MatrixMessageRepository matrixMessageRepository;
	
	public String createDirectRoom(String accessToken, String inviteeMatrixId) {
		String url = matrixBaseUrl+"/_matrix/client/v3/createRoom";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		System.out.println("invitee ---> "+inviteeMatrixId);
		Map<String,Object> body = Map.of(
				"is_direct", true,
		        "invite", List.of(inviteeMatrixId),
		        "preset", "trusted_private_chat");
		ResponseEntity<Map> response = restTemplate.
				postForEntity(url, 
						new HttpEntity<>(body,headers),
						Map.class);
		return response.getBody().get("room_id").toString();
	}
	
	public void ensureJoined(String accessToken, String roomId) {
	    String url = matrixBaseUrl
	            + "/_matrix/client/v3/rooms/"
	            + URLEncoder.encode(roomId, StandardCharsets.UTF_8)
	            + "/join";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(accessToken);

	    try {
	        restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                new HttpEntity<>(headers),
	                Void.class
	        );
	    } catch (HttpClientErrorException.Forbidden e) {
	        // not invited OR already joined → safe to ignore
	    } catch (HttpClientErrorException.Conflict e) {
	        // already joined
	    }
	}

	
//	public void sendMessage(
//	        String accessToken,
//	        String roomId,
//	        String message
//	) {
//	    // 1. Best-effort join
//	    ensureJoined(accessToken, roomId);
//
//	    // 2. Send message
//	    String txnId = UUID.randomUUID().toString();
//	    String url = matrixBaseUrl
//	            + "/_matrix/client/v3/rooms/"
//	            + URLEncoder.encode(roomId, StandardCharsets.UTF_8)
//	            + "/send/m.room.message/"
//	            + txnId;
//
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setBearerAuth(accessToken);
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//
//	    Map<String, Object> payload = Map.of(
//	            "msgtype", "m.text",
//	            "body", message
//	    );
//
//	    ResponseEntity<Map> response = restTemplate.exchange(
//	            url,
//	            HttpMethod.PUT,
//	            new HttpEntity<>(payload, headers),
//	            Map.class
//	    );
//
//	    // 3. Persist AFTER success
//	    MatrixMessage m = new MatrixMessage();
//	    m.setEventId((String) response.getBody().get("event_id"));
//	    m.setMessageType("m.text");
//	    m.setContent(message);
//	    m.setSentAt(Instant.now());
//
//	    matrixMessageRepository.save(m);
//	}
	
	public void sendMessage(
	        String accessToken,
	        String roomId,
	        String senderMatrixId,
	        String message
	) {
		System.out.println("Access Token :"+accessToken);
		System.out.println("Room ID :"+roomId);
	    // 1️⃣ Validate access token belongs to the sender (optional but safe)
	    assertTokenBelongsToUser(accessToken, senderMatrixId);

	    // 2️⃣ Send message
	    String txnId = UUID.randomUUID().toString();
	    String url = matrixBaseUrl
	            + "/_matrix/client/v3/rooms/"
	            + roomId
	            + "/send/m.room.message/"
	            + txnId;

	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(accessToken);
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    Map<String, Object> payload = Map.of(
	            "msgtype", "m.text",
	            "body", message
	    );

	    ResponseEntity<Map> response = restTemplate.exchange(
	            url,
	            HttpMethod.PUT,
	            new HttpEntity<>(payload, headers),
	            Map.class
	    );

	    // 3️⃣ Persist AFTER success
	    MatrixMessage m = new MatrixMessage();
	    m.setEventId((String) response.getBody().get("event_id"));
	    m.setMessageType("m.text");
	    m.setContent(message);
	    m.setSentAt(Instant.now());
	    //m.setSenderMatrixId(senderMatrixId); // optional: track sender in DB
	    m.setRoom(roomId);

	    matrixMessageRepository.save(m);
	}
	
	public boolean isInvited(
            String roomId,
            String userMatrixId,
            String accessToken
    ) {
        String url = matrixBaseUrl
                + "/_matrix/client/v3/rooms/"
                + URLEncoder.encode(roomId, StandardCharsets.UTF_8)
                + "/state/m.room.member/"
                + URLEncoder.encode(userMatrixId, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            String membership = (String) response.getBody().get("membership");
            return "invite".equalsIgnoreCase(membership);

        } catch (HttpClientErrorException.NotFound e) {
            // No membership event exists
            return false;
        }
	}
	
	public String assertTokenBelongsToUser(String accessToken, String expectedUserId) {
	    String url = matrixBaseUrl + "/_matrix/client/v3/account/whoami";

	    HttpHeaders h = new HttpHeaders();
	    h.setBearerAuth(accessToken);

	    ResponseEntity<Map> whoami =
	            restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(h), Map.class);

	    String actualUser = (String) whoami.getBody().get("user_id");

	    if (!expectedUserId.equals(actualUser)) {
	        throw new IllegalStateException(
	            "Token mismatch! Expected " + expectedUserId + " but got " + actualUser
	        );
	    }

	    return actualUser;
	}
	public void joinRoom(String accessToken, String roomId) {
	    String url = matrixBaseUrl
	            + "/_matrix/client/v3/rooms/"
	            + URLEncoder.encode(roomId, StandardCharsets.UTF_8)
	            + "/join";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(accessToken);

	    restTemplate.exchange(
	            url,
	            HttpMethod.POST,
	            new HttpEntity<>(headers),
	            Void.class
	    );
	}
	
	
}
