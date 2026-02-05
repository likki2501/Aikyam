package com.aikyam.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "matrix_message")
public class MatrixMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	//@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "room_id", referencedColumnName = "room_id")
	private String room;
	@Column(name = "event_id", nullable = false, unique = true)
	private String eventId;
	//@ManyToOne
	//@JoinColumn(name = "sender_user_id")
//	private User senderUserId;
//	private String senderMatrixId;
	
	private String messageType;
	@Column(columnDefinition = "TEXT")
	private String content;
	private Instant sentAt;
	private Instant createdAt = Instant.now();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
//	public User getSenderUserId() {
//		return senderUserId;
//	}
//	public void setSenderUserId(User senderUserId) {
//		this.senderUserId = senderUserId;
//	}
//	public String getSenderMatrixId() {
//		return senderMatrixId;
//	}
//	public void setSenderMatrixId(String senderMatrixId) {
//		this.senderMatrixId = senderMatrixId;
//	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Instant getSentAt() {
		return sentAt;
	}
	public void setSentAt(Instant sentAt) {
		this.sentAt = sentAt;
	}
	public Instant getCreatedAt() {
		return createdAt;
	}
	
	
}
