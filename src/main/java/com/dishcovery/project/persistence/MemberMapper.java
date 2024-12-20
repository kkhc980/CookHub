package com.dishcovery.project.persistence;

import com.dishcovery.project.domain.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    int insert(MemberVO memberVO);
    MemberVO selectByMemberId(int memberId);
    List<Integer> selectIdList();
    int update(MemberVO memberVO);
    int delete(int memberId);
    int selectDupCheckEmail(String email);
}
