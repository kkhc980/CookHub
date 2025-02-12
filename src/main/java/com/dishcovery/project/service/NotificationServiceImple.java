package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.MemberNotificationVO;
import com.dishcovery.project.persistence.NotificationMapper;

@Service
public class NotificationServiceImple implements NotificationService {

	@Autowired
	private NotificationMapper notificationMapper;

	@Transactional
    @Override
    public void createFollowNotification(int senderId, int receiverId) {
        // 메시지 기본값 설정
        String message = "사용자 " + senderId + "님이 당신을 팔로우했습니다.";

        // VO 객체 생성 후 데이터 설정
        MemberNotificationVO notification = new MemberNotificationVO();
        notification.setSenderId(senderId);
        notification.setReceiverId(receiverId);
        notification.setType("FOLLOW");
        notification.setMessage(message); // 안전한 값 설정
        notification.setRead(false);

        // 알림 저장
        notificationMapper.insertFollowNotification(notification);
    }

    @Transactional
    @Override
    public void deleteFollowNotification(int senderId, int receiverId) {
        notificationMapper.deleteFollowNotification(senderId, receiverId);
    }

    @Override
    public List<MemberNotificationVO> getUnreadNotifications(int receiverId) {
        return notificationMapper.getUnreadNotifications(receiverId);
    }

    @Override
    public int getUnreadNotificationCount(int receiverId) {
        return notificationMapper.getUnreadNotificationCount(receiverId);
    }

    @Override
    public void markNotificationAsRead(int notificationId) {
        notificationMapper.markAsRead(notificationId);
    }

    @Override
    public void markAllAsRead(int receiverId) {
        notificationMapper.markAllAsRead(receiverId);
    }
}