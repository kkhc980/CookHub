package com.dishcovery.project.persistence;

import com.dishcovery.project.domain.MemberRole;
import com.dishcovery.project.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MemberMapper {
    int insert(MemberVO memberVO);
    int insertMemberRole(int memberId);
    int selectDupCheckEmail(String email);
    int updateAuthKey(Map<String, String> map);
    int updateAuthStatus(Map<String, String> map);
    MemberVO selectEmail(String email);
    MemberVO checkUser(String email);
    MemberRole selectRoleByMemberId(int memberId);
    int updateMember(MemberVO memberVO);
    int updateMemberAuthStatus(String email);
    int updateMemberRole(int memberId);
}
