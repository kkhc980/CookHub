package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberDTO;
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
    public int registerMember(MemberDTO memberDTO) {
        int insertMemberResult = memberMapper.insert(toEntity(memberDTO));
        log.info(insertMemberResult + " member register");

        int memberId = memberMapper.checkUser(memberDTO.getEmail()).getMemberId();
        log.info(memberId + " check");

        int insertMemberRoleResult = memberMapper.insertMemberRole(memberId);
        log.info(insertMemberRoleResult + " member_role register");
        return 1;
    }

    @Override
    public MemberDTO getMemberByEmail(String email) {
        log.info("getMemberByEmail");
        return toDTO(memberMapper.selectEmail(email));
    }

    @Override
    public List<Integer> getAllId() {
        return null;
    }

    @Override
    public int updateMember(MemberDTO memberDTO) {
        log.info("updateMember");
        return memberMapper.updateMember(toEntity(memberDTO));
    }

    @Override
    public int deleteMember(String email) {
        log.info("deleteMember");

        MemberDTO memberDTO = getMemberByEmail(email);

        int deleteMember = memberMapper.updateMemberAuthStatus(email);
        log.info(deleteMember + "row deleteMember");

        int deleteMemberRole = memberMapper.updateMemberRole(toEntity(memberDTO).getMemberId());
        log.info(deleteMemberRole + "row deleteMemberRole");

        return 1;
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
    public int updateAuthStatus(Map<String, String> map) {
        int result = memberMapper.updateAuthStatus(map);
        return result;
    }

    @Override
    public int updateAuthKey(Map<String, String> map) {
        int result = memberMapper.updateAuthKey(map);
        return result;
    }

    public MemberDTO toDTO(MemberVO memberVO) {
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setMemberId(memberVO.getMemberId());
        memberDTO.setEmail(memberVO.getEmail());
        memberDTO.setPassword(memberVO.getPassword());
        memberDTO.setName(memberVO.getName());
        memberDTO.setPhone(memberVO.getPhone());
        memberDTO.setCreatedAt(memberVO.getCreatedAt());
        memberDTO.setUpdatedAt(memberVO.getUpdatedAt());
        memberDTO.setAuthKey(memberVO.getAuthKey());
        memberDTO.setAuthStatus(memberVO.getAuthStatus());

        return memberDTO;
    }

    public MemberVO toEntity(MemberDTO memberDTO) {
        MemberVO entity = new MemberVO();

        entity.setMemberId(memberDTO.getMemberId());
        entity.setEmail(memberDTO.getEmail());
        entity.setPassword(memberDTO.getPassword());
        entity.setName(memberDTO.getName());
        entity.setPhone(memberDTO.getPhone());
        entity.setCreatedAt(memberDTO.getCreatedAt());
        entity.setUpdatedAt(memberDTO.getUpdatedAt());
        entity.setAuthKey(memberDTO.getAuthKey());
        entity.setAuthStatus(memberDTO.getAuthStatus());

        return entity;
    }
}
