package com.dishcovery.project.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {

    private static final long serialVersionUID = 1L;

    private MemberVO memberVO;
    private String name;

    public CustomUser(MemberVO memberVO, Collection<? extends GrantedAuthority> authorities) {
        // Collection<? extends GrantedAuthority> authorities :
        //  권한 정보를 저장하는 Collection

        // User 클래스 생성자에 username, password, authorities를 적용
        // 인증 및 권한 확인에 필요한 정보
        super(memberVO.getEmail(), memberVO.getPassword(), authorities);

        // 전송된 member 객체 적용
        this.memberVO = memberVO;
        this.name = memberVO.getName();
    }
}
