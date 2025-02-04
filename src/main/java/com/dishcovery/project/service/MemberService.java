package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberDTO;

import java.util.Map;

public interface MemberService {

    int registerMember(MemberDTO memberDTO, String authKey); // 회원 정보 등록

    MemberDTO getMemberByEmail(String email); // 회원 정보 조회

    int updateMember(MemberDTO memberDTO);

    int deleteMember(String email);

    boolean selectDupCheckEmail(String email);

    int updateAuthStatus(String email);

    int updateExpiresFlag(Map<String, String> map);

    int updateTempPw(Map<String, String> map);
    int deleteAuthKey(String email);
    int updateAuthKey(String email, String authKey);
}
