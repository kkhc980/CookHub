package com.dishcovery.project.service;

import com.dishcovery.project.domain.Member;
import com.dishcovery.project.persistence.MemberMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Log4j
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberMapper memberMapper;

    @Override
    public int registerMember(Member member) {
        int result = memberMapper.insert(member);
        return result;
    }

    @Override
    public Member getMemberById(int memberId) {
        return null;
    }

    @Override
    public List<Integer> getAllId() {
        return null;
    }

    @Override
    public int updateMember(Member member) {
        return 0;
    }

    @Override
    public int deleteMember(int memberId) {
        return 0;
    }

    @Override
    public Member processSocialLogin(String name) {
        return null;
    }

    @Override
    public boolean selectDupCheckEmail(String email) {
        int result = memberMapper.selectDupCheckEmail(email);
        if (result == 1 ) return true;
        return false;
    }

    @Override
    public int updateAuthStatus(Map<String, String> map) {
        return 0;
    }
}
