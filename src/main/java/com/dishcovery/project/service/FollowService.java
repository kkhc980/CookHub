package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.FollowVO;

public interface FollowService {
    void followMember(int followerId, int followingId);
    void unfollowMember(int followerId, int followingId);
    List<FollowVO> getFollowers(int memberId);
    List<FollowVO> getFollowing(int memberId);
    boolean isFollowing(int followerId, int followingId);
}
