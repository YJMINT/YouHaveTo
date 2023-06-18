package com.yht.exerciseassist.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatDto {
    @NotNull
    private Long roomId;
    @NotBlank
    private String roomName;
    @NotBlank
    private String username;
    @NotBlank
    private String content;
}
