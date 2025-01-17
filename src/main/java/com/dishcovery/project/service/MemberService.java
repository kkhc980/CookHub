package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberDTO;
import com.dishcovery.project.domain.MemberVO;

import java.util.List;
import java.util.Map;

public interface MemberService {

    int registerMember(MemberDTO memberDTO); // 회원 정보 등록

    MemberDTO getMemberByEmail(String email); // 회원 정보 조회

    List<Integer> getAllId();

    int updateMember(MemberDTO memberDTO);

    int deleteMember(String email);

    MemberVO processSocialLogin(String name);

    boolean selectDupCheckEmail(String email);

    int updateAuthStatus(Map<String, String> map);

    int updateAuthKey(Map<String, String> map);

}
