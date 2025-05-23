package com.dishcovery.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.NestedReplyVO;
import com.dishcovery.project.service.NestedReplyService;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping(value = "/recipeboard")
@Log4j
public class NestedReplyRESTController {
	
	@Autowired
	private NestedReplyService nestedReplyService;
	
	@PreAuthorize("hasRole('ROLE_MEMBER')")
	@PostMapping("/nestedreplies/{replyId}")
	 public ResponseEntity<Integer> createNestedReply(
	            @PathVariable("replyId") int replyId,  // 부모 댓글 ID 받기
	            @RequestBody NestedReplyVO nestedReplyVO) { // NestedReplyVO로 받기
	        log.info("createNestedReply");
	        log.info("replyId: " + replyId);
	        log.info("nestedReplyVO: " + nestedReplyVO);

	        try {
	            int result = nestedReplyService.createNestedReply(nestedReplyVO);
	            return new ResponseEntity<>(result, HttpStatus.OK);
	        } catch (Exception e) {
	            log.error("Error creating NestedReply", e);
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	
	@GetMapping("/nestedreplies/all/{replyId}") // 덧글 선택(all)
	public ResponseEntity<Map<String, Object>> readAllNestedReply(
			@PathVariable("replyId") int replyId) {
			
		 	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        Integer currentUserId = null;
	        
	        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUser) {
	           CustomUser customUser = (CustomUser) authentication.getPrincipal();
	           currentUserId = customUser.getMemberVO().getMemberId();
	       }
	       	        
	       log.info("현재 로그인한 사용자 ID = " + currentUserId);
			
		   log.info("readAllNestedReply()");
		   log.info("replyId = " + replyId);
			
			List<NestedReplyVO> list = nestedReplyService.getAllNestedReply(replyId);
			
		    // 응답 데이터 생성
		    Map<String, Object> response = new HashMap<>();
		    response.put("nestedReplies", list);  // 대댓글 목록
		    response.put("currentUserId", currentUserId);  // 로그인한 사용자 ID
		 
			return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_MEMBER')")
	@PutMapping("/nestedreplies/{nestedReplyId}")
	public ResponseEntity<Integer> updateNestedReply(
			@PathVariable("nestedReplyId") int nestedReplyId,
			@RequestBody String nestedReplyContentJson) {
	
			log.info("updateNestedReply()");
			log.info("nestedReplyId = " + nestedReplyId);
			log.info("받은 nestedReplyContent(JSON) = " + nestedReplyContentJson);
			
			// 현재 로그인한 사용자 ID 가져오기
		    Integer currentUserId = getCurrentUserId();
		    if (currentUserId == null) {
		        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 로그인하지 않은 사용자
		    }
		    
		    // 해당 nestedReplyId의 작성자 정보 조회
		    NestedReplyVO existingNestedReply = nestedReplyService.getNestedReplyById(nestedReplyId);
		    if (existingNestedReply == null) {
		        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 해당 답글이 존재하지 않음
		    }
		    
		    // 작성자와 로그인한 사용자가 다르면 권한 없음
		    if (!currentUserId.equals(existingNestedReply.getMemberId())) {
		        return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 없음
		    }
			
			String nestedReplyContent = extractNestedReplyContent(nestedReplyContentJson);
			log.info("변환된 nestedReplyContent = " + nestedReplyContent);
			
			int result = nestedReplyService.updateNestedReply(nestedReplyId, nestedReplyContent);
			return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	 private String extractNestedReplyContent(String json) {
	       if (json.contains("nestedReplyContent")) {  
	           return json.replaceAll(".*\"nestedReplyContent\":\"([^\"]+)\".*", "$1");  // ✅ 정규식을 활용하여 문자열 추출
	       }
	       return json;  
	 }
	 
	 @PreAuthorize("hasRole('ROLE_MEMBER')")
	 @DeleteMapping("/nestedreplies/{nestedReplyId}/{replyId}")
	 public ResponseEntity<Integer> deleteNestedReply(
			 @PathVariable("nestedReplyId") int nestedReplyId,
			 @PathVariable("replyId") int replyId) {
		 log.info("deleteNestedReply()");
		 log.info("nestedReplyId = " + nestedReplyId);
		 
		 // 현재 로그인한 사용자 ID 가져오기
		    Integer currentUserId = getCurrentUserId();
		    if (currentUserId == null) {
		        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 로그인하지 않은 사용자
		    }
		 
		    // 해당 nestedReplyId의 작성자 정보 조회
		    NestedReplyVO existingNestedReply = nestedReplyService.getNestedReplyById(nestedReplyId);
		    if (existingNestedReply == null) {
		        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 해당 답글이 존재하지 않음
		    }
		    
		    
		 // 작성자와 로그인한 사용자가 다르면 권한 없음
		    if (!currentUserId.equals(existingNestedReply.getMemberId())) {
		        return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 없음
		    }

		 int result = nestedReplyService.deleteNestedReply(nestedReplyId, replyId);
		 
		 return new ResponseEntity<Integer>(result, HttpStatus.OK);
		 
	 }
	 
	   private Integer getCurrentUserId() {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUser) {
	           CustomUser customUser = (CustomUser) authentication.getPrincipal();
	           return customUser.getMemberVO().getMemberId(); // CustomUser에서 memberId를 가져옴
	       }
	       return null; // 인증되지 않은 경우 null 반환
	   }
}
