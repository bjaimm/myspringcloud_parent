package com.herosoft.user.po;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private String id;
    private String name;
    private String message;
}
