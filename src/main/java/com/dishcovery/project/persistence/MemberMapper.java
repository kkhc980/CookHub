package com.dishcovery.project.persistence;

import com.dishcovery.project.domain.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    int insert(Member member);
    Member selectByMemberId(int memberId);
    List<Integer> selectIdList();
    int update(Member member);
    int delete(int memberId);
    int selectDupCheckId(String email);
}
