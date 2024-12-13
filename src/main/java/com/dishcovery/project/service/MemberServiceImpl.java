package com.dishcovery.project.service;

import com.dishcovery.project.domain.Member;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j
public class MemberServiceImpl implements MemberService {
    @Override
    public int createMember(Member member) {
        return 0;
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
}
