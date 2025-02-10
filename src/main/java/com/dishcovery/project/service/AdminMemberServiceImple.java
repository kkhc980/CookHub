package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.persistence.AdminMemberMapper;
import com.dishcovery.project.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class AdminMemberServiceImple implements AdminMemberService {

    private final AdminMemberMapper memberMapper;

    @Autowired
    public AdminMemberServiceImple(AdminMemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public boolean registerMember(MemberVO memberVO) {
        return memberMapper.insert(memberVO) > 0;
    }

    @Override
    public boolean isEmailDuplicated(String email) {
        return memberMapper.selectDupCheckEmail(email) > 0;
    }

    @Override
    public void updateAuthKey(Map<String, String> map) {
        memberMapper.updateAuthKey(map);
    }

    @Override
    public MemberVO getMemberByEmail(String email) {
        return memberMapper.selectEmail(email);
    }

    @Override
    public List<MemberVO> getAllMembers(Pagination pagination) {
        return memberMapper.selectAllMembers(pagination);
    }

    @Override
    public MemberVO getMemberById(int memberId) {
        return memberMapper.selectMemberById(memberId);
    }

    @Override
    public List<String> getRolesByMemberId(int memberId) {
        return memberMapper.selectRolesByMemberId(memberId);
    }

    @Override
    public boolean addMemberRole(int memberId, String roleName) {
        Map<String, Object> map = Map.of("memberId", memberId, "roleName", roleName);
        memberMapper.insertMemberRole(map);
        return true;
    }

    @Override
    public boolean deleteMemberRole(int memberId) {
        memberMapper.deleteMemberRole(memberId);
        return true;
    }

    @Override
    public boolean deleteMemberRoleByName(int memberId, String roleName) {
        Map<String, Object> map = Map.of("memberId", memberId, "roleName", roleName);
        memberMapper.deleteMemberRoleByName(map);
        return true;
    }

    @Override
    public List<String> getAllRoles() {
        return memberMapper.selectAllRoles();
    }

    @Override
    public List<MemberVO> searchMembersByEmail(Pagination pagination, String keyword) {
        return memberMapper.selectMemberByEmail(pagination, keyword);
    }

    @Override
    public List<MemberVO> searchMembersByName(Pagination pagination, String keyword) {
        return memberMapper.selectMemberByName(pagination, keyword);
    }

    @Override
    public int getTotalMemberCount(Pagination pagination) {
        return memberMapper.getTotalCount(pagination);
    }

    @Override
    public int getSearchMemberCount(String searchType, String keyword) {
        Map<String, Object> map = Map.of("searchType", searchType, "keyword", keyword);
        return memberMapper.getTotalCountSearch(map);
    }

    @Override
    public int getTotalMemberCount() {
        return memberMapper.getTotalMemberCount();
    }

    @Override
    public int getActiveMemberCount() {
        return memberMapper.getActiveMemberCount();
    }

    @Override
    public int getInactiveMemberCount() {
        return memberMapper.getInactiveMemberCount();
    }

    @Override
    public int getNewMemberCount() {
        return memberMapper.getNewMemberCount();
    }

    @Override
    public List<Map<String, Object>> getRoleCounts() {
        return memberMapper.getRoleCounts();
    }

    @Override
    public boolean updatePassword(int memberId, String password) {
        memberMapper.updatePassword(memberId, password);
        return true;
    }

    @Override
    public boolean updateMember(MemberVO memberVO) {
        memberMapper.updateMember(memberVO);
        return true;
    }

    @Override
    public boolean deleteMember(int memberId) {
        memberMapper.delete(memberId);
        return true;
    }

    @Override
    @Transactional
    public boolean updateMemberAuthStatus(int memberId, int authStatus) {
        int result = memberMapper.updateAuthStatus(memberId, authStatus);
        return result > 0;
    }
}