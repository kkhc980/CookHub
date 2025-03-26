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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.ReplyVO;
import com.dishcovery.project.service.ReplyService;
import com.dishcovery.project.util.PageMaker;
import com.dishcovery.project.util.Pagination;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping(value = "/recipeboard")
@Log4j
public class ReplyRESTController {
   @Autowired
   private ReplyService replyService;
   
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @PostMapping("/replies/detail")
   public ResponseEntity<Integer> createReply(@RequestBody ReplyVO replyVO){
      try {
      int result = replyService.createReply(replyVO);
      return new ResponseEntity<Integer>(result, HttpStatus.OK);
         } catch (Exception e) {
            log.error("Error creating reply", e);
            
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
         }
   }
   
   @GetMapping("/all/{recipeBoardId}") // GET : 댓글 선택(all)
   public ResponseEntity<Map<String, Object>> readAllReply(
	        @PathVariable("recipeBoardId") int recipeBoardId,
	        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {
      // @PathVariable("recipeBoardId") : {recipeBoardId} 값을 설정된 변수에 저장
	   
	   Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       Integer currentUserId = null;
       
       if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUser) {
           CustomUser customUser = (CustomUser) authentication.getPrincipal();
           currentUserId = customUser.getMemberVO().getMemberId();
       }
       
       log.info("현재 로그인한 사용자 ID = " + currentUserId);
	   
   // Pagination 객체를 명시적으로 생성
      Pagination pagination = new Pagination(pageNum, 5); 
      
      int replyTotalCount = replyService.getTotalReplyCount(recipeBoardId);
           
   // PageMaker 객체 생성 및 설정
      PageMaker pageMaker = new PageMaker();
      pageMaker.setPagination(pagination);
      pageMaker.setReplyTotalCount(replyTotalCount);
      
      List<ReplyVO> list = replyService.getAllReply(recipeBoardId, pagination);
            
      // ResponseEntity<T> : T의 타입은 프론트 side로 전송될 데이터 타입으로선언
      Map<String, Object> result = new HashMap<>();
      result.put("replies", list);
      result.put("pagination", pageMaker);
      result.put("currentUserId", currentUserId); // ✅ 현재 로그인한 사용자 ID 추가
      
      System.out.println("응답 데이터: " + result); // 🔥 콘솔에 전체 데이터 출력해서 확인

      
      return new ResponseEntity<>(result, HttpStatus.OK);
   }
   
   
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @PutMapping("replies/{replyId}") // PUT : 댓글 수정
    public ResponseEntity<Integer> updateReply(
          @PathVariable("replyId") int replyId,
          @RequestBody String replyContentJson) { // ✅ JSON 문자열을 받음
	   
	   	// 현재 로그인한 사용자 ID 가져오기
	    Integer currentUserId = getCurrentUserId();
	    if (currentUserId == null) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 로그인하지 않은 사용자
	    }
	    
		// 해당 replyId의 작성자 정보 조회
	    ReplyVO existingReply = replyService.getReplyById(replyId);
	    if (existingReply == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 해당 답글이 존재하지 않음
	    }	
	    
	    // 현재 사용자와 리뷰 작성자가 동일한지 확인
	      if (!currentUserId.equals(existingReply.getMemberId())) {
	          return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 없음
	      }
	    
          // ✅ JSON에서 실제 replyContent 값만 추출
          String replyContent = extractReplyContent(replyContentJson);
          
          int result = replyService.updateReply(replyId, replyContent);
          return new ResponseEntity<>(result, HttpStatus.OK);
   }
   
   private String extractReplyContent(String json) {
       if (json.contains("replyContent")) {  
           return json.replaceAll(".*\"replyContent\":\"([^\"]+)\".*", "$1");  // ✅ 정규식을 활용하여 문자열 추출
       }
       return json;  
   }
   
   
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @DeleteMapping("replies/{replyId}/{recipeBoardId}") // DELETE : 댓글 삭제
    public ResponseEntity<Integer> deleteReply(
          @PathVariable("replyId") int replyId,
          @PathVariable("recipeBoardId") int recipeBoardId) {
	   
	   		// 현재 로그인한 사용자 ID 가져오기
		    Integer currentUserId = getCurrentUserId();
		    if (currentUserId == null) {
		        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 로그인하지 않은 사용자
		    }
	    	
	    	// 해당 replyId의 작성자 정보 조회
		    ReplyVO existingReply = replyService.getReplyById(replyId);
		    if (existingReply == null) {
		        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 해당 답글이 존재하지 않음
		    }	
	      
	      // 현재 사용자와 리뷰 작성자가 동일한지 확인
	      if (!currentUserId.equals(existingReply.getMemberId())) {
	          return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 없음
	      }
	   
      int result = replyService.deleteReply(replyId, recipeBoardId);   
      
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
