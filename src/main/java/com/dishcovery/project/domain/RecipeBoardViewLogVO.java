package com.dishcovery.project.domain;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecipeBoardViewLogVO {
    private int logId;          // 로그 ID
    private int recipeBoardId;  // 게시글 ID
    private String ipAddress;    // IP 주소
    private Date viewDate;       // 조회 날짜
}
