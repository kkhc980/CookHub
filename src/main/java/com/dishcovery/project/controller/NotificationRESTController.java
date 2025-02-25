package com.dishcovery.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.MemberNotificationVO;
import com.dishcovery.project.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationRESTController {

	@Autowired
    NotificationService notificationService;

	    // 팔로우 시 알림 생성
	    @PostMapping("/follow")
	    public void createFollowNotification(@RequestParam int senderId, @RequestParam int receiverId) {
	        notificationService.createFollowNotification(senderId, receiverId);
	    }
	
	    // 언팔로우 시 팔로우 알림 삭제
	    @DeleteMapping("/follow")
	    public void deleteFollowNotification(@RequestParam int senderId, @RequestParam int receiverId) {
	        System.out.println("알림 삭제 요청 - senderId: " + senderId + ", receiverId: " + receiverId);
	        notificationService.deleteFollowNotification(senderId, receiverId);
	    }

	    @GetMapping("/unread/{receiverId}")
	    public List<MemberNotificationVO> getUnreadNotifications(@PathVariable int receiverId) {
	        return notificationService.getUnreadNotifications(receiverId);
	    }

	    @GetMapping("/unread/count/{receiverId}")
	    public int getUnreadNotificationCount(@PathVariable int receiverId) {
	        return notificationService.getUnreadNotificationCount(receiverId);
	    }

	    @PostMapping("/read/{notificationId}")
	    public void markNotificationAsRead(@PathVariable int notificationId) {
	        notificationService.markNotificationAsRead(notificationId);
	    }

	    @PostMapping("/readAll/{receiverId}")
	    public void markAllAsRead(@PathVariable int receiverId) {
	        notificationService.markAllAsRead(receiverId);
	    }
	}
