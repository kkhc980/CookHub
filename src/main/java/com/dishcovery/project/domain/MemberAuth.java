package com.dishcovery.project.domain;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class MemberAuth {
    private int authId;
    private int memberId;
    private String authKey;
    private Date createdAt;
    private Date expiresAt;
    private int expiresFlag;
}
