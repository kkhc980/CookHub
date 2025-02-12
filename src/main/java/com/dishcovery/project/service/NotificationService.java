package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberNotificationVO;

import java.util.List;

public interface NotificationService {
    void createFollowNotification(int senderId, int receiverId); // 팔로우 시 알림 생성
    void deleteFollowNotification(int senderId, int receiverId); // 팔로우 시 알림 생성
    List<MemberNotificationVO> getUnreadNotifications(int receiverId); // 읽지 않은 최신 5개 조회
    int getUnreadNotificationCount(int receiverId); // 읽지 않은 알림 개수 조회
    void markNotificationAsRead(int notificationId); // 특정 알림 읽음 처리
    void markAllAsRead(int receiverId); // 모든 알림 읽음 처리
}
