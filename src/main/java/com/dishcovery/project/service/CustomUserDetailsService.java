package com.dishcovery.project.service;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.MemberRole;
import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.persistence.MemberMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberMapper memberMapper;

    // 전송된 email 으로 사용자 정보를 조회하고, UserDetails 에 저장하여 리턴하는 메서드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername()");
        // 이메일을 이용하여 회원 정보와 권한 정보를 조회
        MemberVO member = memberMapper.selectEmail(email);

        // 조회된 회원 정보가 없을 경우 예외 처리
        if (member == null) {
            throw new UsernameNotFoundException("UsernameNotFound");
        }

        MemberRole role = memberMapper.selectRoleByMemberId(member.getMemberId());

        // 회원의 역할을 Spring Security의 GrantedAuthority 타입으로 변환하여 리스트에 추가
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role.getRoleName()));

        // UserDetails 객체를 생성하여 회원 정보와 역할 정보를 담아 반환
        UserDetails userDetails = new CustomUser(member, authorities);
        return userDetails;
    }
}
