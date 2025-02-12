package com.dishcovery.project.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
<<<<<<< Updated upstream
import org.springframework.web.bind.annotation.RequestParam;
=======
>>>>>>> Stashed changes
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.FollowVO;
import com.dishcovery.project.service.FollowService;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    // ✅ 팔로우 기능 (POST)
    @PostMapping("/{followingId}")
    public ResponseEntity<String> followUser(@PathVariable int followingId,
                                             @AuthenticationPrincipal CustomUser customUser) {
        int followerId = customUser.getMemberVO().getMemberId();
        followService.followMember(followerId, followingId);
        return ResponseEntity.ok("팔로우 성공");
    }

    // ✅ 언팔로우 기능 (DELETE) - followerId를 URL 파라미터로 받음
    @DeleteMapping("/{followingId}")
    public ResponseEntity<String> unfollowUser(@PathVariable int followingId,
                                               @RequestParam int followerId) {  // 🔹 followerId를 요청 파라미터로 받음
        followService.unfollowMember(followerId, followingId);
        return ResponseEntity.ok("언팔로우 성공");
    }


    // 특정 사용자의 팔로워 목록 가져오기
    @GetMapping("/followers/{memberId}")
    public List<Integer> getFollowers(@PathVariable int memberId) {
        return followService.getFollowers(memberId)
                .stream()
                .map(FollowVO::getFollowerId)
                .collect(Collectors.toList());
    }

    // 특정 사용자가 팔로우하는 사람 목록 가져오기
    @GetMapping("/following/{memberId}")
    public List<Integer> getFollowing(@PathVariable int memberId) {
        return followService.getFollowing(memberId)
                .stream()
                .map(FollowVO::getFollowingId)
                .collect(Collectors.toList());
    }

    // 로그인한 사용자가 특정 사용자를 팔로우하고 있는지 확인
    @GetMapping("/is-following/{followingId}")
    public boolean isFollowing(@PathVariable int followingId, @AuthenticationPrincipal CustomUser customUser) {
        if (customUser == null) {
            return false;
        }

        int followerId = customUser.getMemberVO().getMemberId();
        return followService.isFollowing(followerId, followingId);
    }
}
