package com.dishcovery.project.domain;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class MemberVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
