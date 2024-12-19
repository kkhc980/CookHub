package com.dishcovery.project.service;

import com.dishcovery.project.domain.Member;

import java.util.List;
import java.util.Map;

public interface MemberService {
    int registerMember(Member member);
    Member getMemberById(int memberId);
    List<Integer> getAllId();
    int updateMember(Member member);
    int deleteMember(int memberId);
    Member processSocialLogin(String name);
    boolean selectDupCheckEmail(String email);
    int updateAuthStatus(Map<String, String> map);
}
