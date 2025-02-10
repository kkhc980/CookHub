package com.dishcovery.project.persistence;

import com.dishcovery.project.domain.MemberAuth;
import com.dishcovery.project.domain.MemberRole;
import com.dishcovery.project.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

@Mapper
public interface MemberMapper {
    int insert(MemberVO memberVO);
    int insertMemberRole(String email);
    int selectDupCheckEmail(String email);
    int updateExpiresFlag(Map<String, String> map);
    int updateAuthStatus(String email);
    MemberVO selectEmail(String email);
    MemberVO checkUser(String email);
    MemberRole selectRoleByMemberId(int memberId);
    int updateMember(MemberVO memberVO);
    int updateTempPw(Map<String, String> map);
    int insertMemberAuthKey(@Param("email") String email, @Param("authKey") String authKey, @Param("expiresAt") Date expiresAt);
    int deleteAuthKey(String email);
    int deleteMember(String email);
    int deleteMemberRole(String email);
    MemberAuth checkAuth(String email);
    int updateAuthKey(@Param("email") String email, @Param("authKey") String authKey, @Param("expiresAt") Date expiresAt);
}
