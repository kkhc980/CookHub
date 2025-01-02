package com.dishcovery.project.persistence;

import com.dishcovery.project.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MemberMapper {
    int insert(MemberVO memberVO);
    int selectDupCheckEmail(String email);
    void updateAuthKey(Map<String, String> map);
    void updateAuthStatus(Map<String, String> map);
}
