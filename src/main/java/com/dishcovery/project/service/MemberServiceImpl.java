package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberDTO;
import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.persistence.MemberMapper;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@Log4j
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberMapper memberMapper;

    @Override
    public int registerMember(MemberDTO memberDTO, String authKey) {

        Date currentDate = new Date();
        Date expiresAt = DateUtils.addMinutes(currentDate, 60);

        int insertMemberResult = memberMapper.insert(toEntity(memberDTO));
        log.info(insertMemberResult + " member register");

        int insertMemberRoleResult = memberMapper.insertMemberRole(memberDTO.getEmail());
        log.info(insertMemberRoleResult + " member_role register");

        int insertMemberAuthKey = memberMapper.insertMemberAuthKey(memberDTO.getEmail(), authKey, expiresAt);
        log.info(insertMemberAuthKey + " member_authKey register");
        return 1;
    }

    @Override
    public MemberDTO getMemberByEmail(String email) {
        log.info("getMemberByEmail");
        return toDTO(memberMapper.selectEmail(email));
    }

    @Override
    public int updateMember(MemberDTO memberDTO) {
        log.info("updateMember");
        return memberMapper.updateMember(toEntity(memberDTO));
    }

    @Override
    public int deleteMember(String email) {
        log.info("deleteMember");
        log.info("email : " + email);

        int deleteMemberRole = memberMapper.deleteMemberRole(email);
        log.info(deleteMemberRole + "row deleteMemberRole");

        int deleteMember = memberMapper.deleteMember(email);
        log.info(deleteMember + "row deleteMember");

        return 1;
    }

    @Override
    public boolean selectDupCheckEmail(String email) {
        int result = memberMapper.selectDupCheckEmail(email);
        if (result == 1) return true;

        return false;
    }

    @Override
    public int updateAuthStatus(String email) {
        int result = memberMapper.updateAuthStatus(email);
        return result;
    }

    @Override
    public int updateExpiresFlag(Map<String, String> map) {
        int result = memberMapper.updateExpiresFlag(map);
        return result;
    }

    @Override
    public int updateTempPw(Map<String, String> map) {
        int result = memberMapper.updateTempPw(map);
        return result;
    }

    @Override
    public int deleteAuthKey(String email) {
        int result = memberMapper.deleteAuthKey(email);
        return result;
    }

    @Override
    public int updateAuthKey(String email, String authKey) {

        Date currentDate = new Date();
        Date expiresAt = DateUtils.addMinutes(currentDate, 60);

        int result = memberMapper.updateAuthKey(email, authKey, expiresAt);
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
