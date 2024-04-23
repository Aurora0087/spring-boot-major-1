package com.ss.tst1.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentsRequest {
    private String parentId;
    private String parentType;
    private String ignore;
    private String limit;
}
