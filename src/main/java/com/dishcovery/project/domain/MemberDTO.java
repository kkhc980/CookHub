package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class MemberDTO {
    private int memberId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private Date createdAt;
    private Date updatedAt;
    private String authKey;
    private int authStatus;
}
