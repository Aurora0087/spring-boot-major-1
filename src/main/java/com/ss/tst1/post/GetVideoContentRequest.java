package com.ss.tst1.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVideoContentRequest {
    private String ignore;
    private String limit;
}
