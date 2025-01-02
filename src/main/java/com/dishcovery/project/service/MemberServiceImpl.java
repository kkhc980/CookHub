package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberVO;
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
    public int registerMember(MemberVO memberVO) {
        int result = memberMapper.insert(memberVO);

        return result;
    }

    @Override
    public MemberVO getMemberById(int memberId) {
        return null;
    }

    @Override
    public List<Integer> getAllId() {
        return null;
    }

    @Override
    public int updateMember(MemberVO memberVO) {
        return 0;
    }

    @Override
    public int deleteMember(int memberId) {
        return 0;
    }

    @Override
    public MemberVO processSocialLogin(String name) {
        return null;
    }

    @Override
    public boolean selectDupCheckEmail(String email) {
        int result = memberMapper.selectDupCheckEmail(email);
        if (result == 1) return true;

        return false;
    }

    @Override
    public void updateAuthStatus(Map<String, String> map) {
        memberMapper.updateAuthStatus(map);
    }

    @Override
    public void updateAuthKey(Map<String, String> map) {
        System.out.println("map : " + map);
        memberMapper.updateAuthKey(map);
    }
}
