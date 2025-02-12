package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
