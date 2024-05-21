package com.ss.tst1.videoContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateVideoContentResponse {
    private String message;
    private Integer contentId;

    public CreateVideoContentResponse(String message) {
        this.message = message;
    }
}
