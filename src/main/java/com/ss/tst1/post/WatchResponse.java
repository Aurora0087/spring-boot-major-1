package com.ss.tst1.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WatchResponse {
    private String message;
    private String videoUrl;

    public WatchResponse(String message) {
        this.message = message;
    }
}
