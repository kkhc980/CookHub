package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dishcovery.project.domain.FollowVO;
import com.dishcovery.project.persistence.FollowMapper;

@Service
public class FollowServiceImple implements FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Override
    public void followMember(int followerId, int followingId) {
        if (followMapper.isFollowing(followerId, followingId) == 0) {
            followMapper.insertFollow(followerId, followingId);
        }
    }

    @Override
    public void unfollowMember(int followerId, int followingId) {
        followMapper.deleteFollow(followerId, followingId);
    }

    @Override
    public List<FollowVO> getFollowers(int memberId) {
        return followMapper.getFollowers(memberId);
    }

    @Override
    public List<FollowVO> getFollowing(int memberId) {
        return followMapper.getFollowing(memberId);
    }

    @Override
    public boolean isFollowing(int followerId, int followingId) {
        return followMapper.isFollowing(followerId, followingId) > 0;
    }

}
