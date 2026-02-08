package com.aikyam.service;

import java.util.List;
import java.util.Map;

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

	    //  Ensure the sender has a valid session
	    authService.ensureSession(senderMatrixId, senderUsername, senderPassword);

	    //  Get access token for the sender
	    String accessToken = tokenCache.getToken(senderMatrixId);

	    // Lookup receiver Matrix ID
	    User receiver = userRepository.findById(receiverUserId).orElseThrow();
	    String receiverMatrixId = receiver.getMatrixUserId();

	    // Get existing room or create a new one
	    String roomId = getOrCreateRoom(senderMatrixId, receiverMatrixId, accessToken);

	    //  Ensure the token actually belongs to the sender (optional)
	    roomService.assertTokenBelongsToUser(accessToken, senderMatrixId);

	    //️⃣ Send message — do NOT call joinRoom or ensureJoined for the sender
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

	public List<Map<String, Object>> syncMessages(String matrixUserId,String password) {
		return matrixService.syncMessages(matrixUserId,password);
	}
	

}
