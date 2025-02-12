package com.dishcovery.project.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private int authStatus;
}
