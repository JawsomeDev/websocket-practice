package com.chatserver.chat.repository;

import com.chatserver.chat.domain.ChatRoom;
import com.chatserver.chat.domain.ReadStatus;
import com.chatserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {

    List<ReadStatus> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    Long countByChatRoomAndMemberAndIsReadFalse(ChatRoom chatRoom, Member member);
}