package com.utn.API_CentroDeportivo.controller;

import com.utn.API_CentroDeportivo.model.dto.request.ChatMessageRequest;
import com.utn.API_CentroDeportivo.model.dto.response.ChatMessageResponse;
import com.utn.API_CentroDeportivo.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request, Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                if (!username.equals("anonymousUser")) {
                    request.setUserId(username);
                }
            }

            ChatMessageResponse response = chatService.processMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ChatMessageResponse.error("Error al procesar el mensaje: " + e.getMessage())
            );
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat service is running");
    }
}
