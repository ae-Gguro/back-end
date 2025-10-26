package com.example.gguro.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatroomCheckResponseDTO {
    private boolean created_today;

    public boolean isCreatedToday() {
        return created_today;
    }
}
