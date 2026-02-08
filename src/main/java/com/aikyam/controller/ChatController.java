package com.aikyam.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aikyam.dto.SendDirectMessageRequest;
import com.aikyam.service.AuthService;
import com.aikyam.service.ChatService;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ChatController {
	private final ChatService chatService;
	private final AuthService authService;
	public ChatController(ChatService chatService,AuthService authService) {
		this.chatService = chatService;
		this.authService = authService;
	}
	
	@PostMapping("/send")
	public ResponseEntity<Void> sendMessage(@RequestBody SendDirectMessageRequest request){
		String senderMatrixId = request.getSenderMatrixUserId();
		String senderUsername = request.getSenderUsername();
		String senderPassword = request.getSenderPassword();
		Integer receiverUserId = request.getReceiverUserId();
		String message = request.getMessage();
		chatService.sendOneToOneMessage(senderMatrixId, senderUsername, senderPassword, receiverUserId, message);
		return ResponseEntity.ok().build();
	}
	@GetMapping("/sync")
	public List<Map<String, Object>> sync(@RequestParam String matrixUserId,@RequestParam String password) {
	    return chatService.syncMessages(matrixUserId,password);
	}
	
}
