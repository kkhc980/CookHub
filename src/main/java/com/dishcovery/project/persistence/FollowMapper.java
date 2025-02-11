package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.FollowVO;

@Mapper
public interface FollowMapper {
    void insertFollow(@Param("followerId") int followerId, @Param("followingId") int followingId);
    void deleteFollow(@Param("followerId") int followerId, @Param("followingId") int followingId);
    List<FollowVO> getFollowers(@Param("memberId") int memberId);
    List<FollowVO> getFollowing(@Param("memberId") int memberId);
    int isFollowing(@Param("followerId") int followerId, @Param("followingId") int followingId);

}
