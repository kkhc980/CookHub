package com.dishcovery.project.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FollowVO {
    private int followId;
    private int followerId;
    private int followingId;
    private LocalDateTime createdAt;
}
