package com.aikyam.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "matrix_direct_room",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"user_a_id","user_b_id"}),
				@UniqueConstraint(columnNames = {"user_b_id","user_a_id"})
})
public class MatrixDirectRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//@ManyToOne
	//@JoinColumn(name = "user_a_id", nullable = false)
	private String userAId;
	//@ManyToOne
	//@JoinColumn(name = "user_b_id",nullable = false)
	private String userBId	;
	
	@Column(name = "room_id",unique = true,nullable = false)
	private String roomId;
	
	//@ManyToOne
	//@JoinColumn(name = "created_by",nullable = false)
	private String createdBy;
	
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
	private List<MatrixMessage> messages = new ArrayList<>();
	
	public MatrixDirectRoom(String userA,String userB,String roomId) {
		this.userAId = userA;
		this.userBId = userB;
		this.roomId = roomId;
		this.createdBy = userA;
		
	}
	
	public MatrixDirectRoom() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserAId() {
		return userAId;
	}

	public void setUserAId(String userAId) {
		this.userAId = userAId;
	}

	public String getUserBId() {
		return userBId;
	}

	public void setUserBId(String userBId) {
		this.userBId = userBId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public List<MatrixMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<MatrixMessage> messages) {
		this.messages = messages;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	
}
