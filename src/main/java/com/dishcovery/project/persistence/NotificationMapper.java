package com.dishcovery.project.persistence;

import com.dishcovery.project.domain.MemberNotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
	String getMemberNameById(@Param("senderId") int senderId);
    // 팔로우 알림 저장
    void insertFollowNotification(MemberNotificationVO notification);
   
    // 언팔로우 시 팔로우 알림 삭제
    void deleteFollowNotification(@Param("senderId") int senderId, @Param("receiverId") int receiverId);
    
    // 특정 유저의 최신 5개 읽지 않은 알림 조회
    List<MemberNotificationVO> getUnreadNotifications(@Param("receiverId") int receiverId);

    // 읽지 않은 알림 개수 조회
    int getUnreadNotificationCount(@Param("receiverId") int receiverId);

    // 특정 알림 읽음 처리
    void markAsRead(@Param("notificationId") int notificationId);

    // 모든 알림 읽음 처리
    void markAllAsRead(@Param("receiverId") int receiverId);
}