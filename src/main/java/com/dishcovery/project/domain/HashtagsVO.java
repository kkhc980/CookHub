package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class HashtagsVO {
    private Integer hashtagId; // 해시태그 ID (PK)
    private String hashtagName; // 해시태그 이름 (Unique)
    
    public HashtagsVO(int hashtagId, String hashtagName) {
        this.hashtagId = hashtagId;
        this.hashtagName = hashtagName;
    }
}