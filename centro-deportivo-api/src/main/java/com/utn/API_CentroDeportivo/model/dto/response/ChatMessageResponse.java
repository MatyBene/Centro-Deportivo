package com.utn.API_CentroDeportivo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String response;
    private LocalDateTime timestamp;
    private boolean success;

    public static ChatMessageResponse success(String response) {
        return new ChatMessageResponse(response, LocalDateTime.now(), true);
    }

    public static ChatMessageResponse error(String message) {
        return new ChatMessageResponse(message, LocalDateTime.now(), false);
    }
}
