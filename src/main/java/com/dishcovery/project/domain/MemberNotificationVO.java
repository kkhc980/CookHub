package com.dishcovery.project.domain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class MemberNotificationVO {
    private int notificationId;
    private int receiverId;
    private int senderId;
    private String type; // "FOLLOW"
    private String message;
    private boolean isRead;
    private Date createdAt;
}