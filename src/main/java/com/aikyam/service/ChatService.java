package com.aikyam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aikyam.dto.MatrixTokenCache;
import com.aikyam.model.MatrixDirectRoom;
import com.aikyam.model.User;
import com.aikyam.repository.MatrixDirectRoomRepository;
import com.aikyam.repository.MatrixMessageRepository;
import com.aikyam.repository.UserRepository;

@Service
public class ChatService {
	private final UserRepository userRepository;
	private final MatrixDirectRoomRepository matrixDirectRoomRepository;
	private final MatrixMessageRepository matrixMessageRepository;
	private final MatrixService matrixService;
	
	@Autowired
	private MatrixTokenCache tokenCache;
	@Autowired
	private AuthService authService;
	@Autowired
	private RoomService roomService;

	public ChatService(
			UserRepository userRepository,
			MatrixDirectRoomRepository matrixDirectRoomRepository,
			MatrixMessageRepository matrixMessageRepository,
			MatrixService matrixService) {
		this.userRepository = userRepository;
		this.matrixDirectRoomRepository = matrixDirectRoomRepository;
		this.matrixMessageRepository = matrixMessageRepository;
		this.matrixService = matrixService;
	}
	
//	public void sendOneToOneMessage(
//			String senderMatrixId,
//			String senderUsername,
//			String senderPassword,
//			Integer receiverUserId,
//			String message) {
//		authService.ensureSession(senderMatrixId, senderUsername, senderPassword);
//		
//		String accessToken = tokenCache.getToken(senderMatrixId);
//		
//		User receiver = userRepository.findById(receiverUserId).orElseThrow();
//		String receiverMatrixId = receiver.getMatrixUserId();
//		
//		String roomId = getOrCreateRoom(senderMatrixId, receiverMatrixId, accessToken);
//		
//		//roomService.joinRoom(accessToken, roomId);
//		roomService.assertTokenBelongsToUser(accessToken, senderMatrixId);
//		
//		roomService.sendMessage(accessToken, roomId, message);
//		
//		
//	}
	public void sendOneToOneMessage(
	        String senderMatrixId,
	        String senderUsername,
	        String senderPassword,
	        Integer receiverUserId,
	        String message) {

	    // 1️⃣ Ensure the sender has a valid session
	    authService.ensureSession(senderMatrixId, senderUsername, senderPassword);

	    // 2️⃣ Get access token for the sender
	    String accessToken = tokenCache.getToken(senderMatrixId);

	    // 3️⃣ Lookup receiver Matrix ID
	    User receiver = userRepository.findById(receiverUserId).orElseThrow();
	    String receiverMatrixId = receiver.getMatrixUserId();

	    // 4️⃣ Get existing room or create a new one
	    String roomId = getOrCreateRoom(senderMatrixId, receiverMatrixId, accessToken);

	    // 5️⃣ Ensure the token actually belongs to the sender (optional)
	    roomService.assertTokenBelongsToUser(accessToken, senderMatrixId);

	    // 6️⃣ Send message — do NOT call joinRoom or ensureJoined for the sender
	    roomService.sendMessage(accessToken, roomId, senderMatrixId, message);
	}
	private String getOrCreateRoom(String senderMatrixId,String receiverMatrixId,String senderAccessToken) {
		String userA = senderMatrixId.compareTo(receiverMatrixId) < 0
				? senderMatrixId : receiverMatrixId;
		String userB = senderMatrixId.compareTo(receiverMatrixId) < 0 
				? receiverMatrixId : senderMatrixId;
		return matrixDirectRoomRepository.
				findByUserAIdAndUserBId(userA,userB)
				.map(MatrixDirectRoom::getRoomId)
				.orElseGet(() ->{
					String roomId = roomService.createDirectRoom(senderAccessToken, receiverMatrixId);
					System.out.println("createDirectRoom called"+receiverMatrixId);
					matrixDirectRoomRepository.save(new MatrixDirectRoom(userA,userB,roomId));
					return roomId;
				});
	}

}
