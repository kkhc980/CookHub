package com.dishcovery.project.persistence;

import com.dishcovery.project.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    int insert(MemberVO memberVO);
    int selectDupCheckEmail(String email);
    void updateAuthKey(String email, String authKey);
    void updateAuthStatus(String email, String authKey);
}
