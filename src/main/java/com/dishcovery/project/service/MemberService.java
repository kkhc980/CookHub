package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberVO;

import java.util.List;
import java.util.Map;

public interface MemberService {

    int registerMember(MemberVO memberVO);

    MemberVO getMemberById(int memberId);


    List<Integer> getAllId();

    int updateMember(MemberVO memberVO);

    int deleteMember(int memberId);

    MemberVO processSocialLogin(String name);

    boolean selectDupCheckEmail(String email);

    void updateAuthStatus(Map<String, String> map);

    void updateAuthKey(Map<String, String> map);

}
