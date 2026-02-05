package com.aikyam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aikyam.model.MatrixDirectRoom;
import com.aikyam.model.User;

@Repository
public interface MatrixDirectRoomRepository extends JpaRepository<MatrixDirectRoom, Long> {
//	@Query("""
//			SELECT r FROM MatrixDirectRoom r
//			WHERE (r.userAId = :u1 AND r.userBId = :u2)
//			 OR (r.userAId = :u2 AND r.userBId = :u1)
//			""")
	Optional<MatrixDirectRoom> findByUserAIdAndUserBId(String userA,String userB);
}
