package com.chatserver.chat.controller;


import com.chatserver.chat.dto.ChatMessageDto;
import com.chatserver.chat.service.ChatService;
import com.chatserver.chat.service.RedisPubSubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class StompController {

    private final ChatService chatService;

    private final SimpMessageSendingOperations messageTemplate;

    private final RedisPubSubService redisPubSubService;

    public StompController(ChatService chatService, SimpMessageSendingOperations messageTemplate, RedisPubSubService redisPubSubService) {
        this.chatService = chatService;
        this.messageTemplate = messageTemplate;
        this.redisPubSubService = redisPubSubService;
    }

    /**
     *
     방법1. MessageMapping(수신)과 SendTo(topic에 메시지 전달)한꺼번에 처리
     */
//    @MessageMapping("/{roomId}") // 클라이언트에서 특정 /publish/roomId 형태로 메시지를 발행시 MessageMapping 수신
//    @SendTo("/topic/{roomId}") // 해당 roomId에 메시지를 발행하여 구독중인 클라이언트에게 메시지 전송
//    // DesinationVariable : @MessageMapping 어노테이션으로 정의된 Websocket Controller 내에서만 사용 @PathVariable같은거임.
//    public String sendMessage(@DestinationVariable Long roomId, String message){
//        log.info("{}", message);
//        return message;
//    }

    /**
     * 방법2. MessageMapping 어노테이션만 활용.
     */
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) throws JsonProcessingException {
        log.info(chatMessageDto.getMessage());
        log.info(chatMessageDto.getSenderEmail());
        chatService.saveMessage(roomId, chatMessageDto);
        chatMessageDto.setRoomId(roomId);
//        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageDto);
        ObjectMapper  objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(chatMessageDto);
        redisPubSubService.publish("chat", message);
    }
}
