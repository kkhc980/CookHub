package com.dishcovery.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<List<NestedReplyVO>> readAllNestedReply(
			@PathVariable("replyId") int replyId) {
			log.info("readAllNestedReply()");
			log.info("replyId = " + replyId);
			
			List<NestedReplyVO> list = nestedReplyService.getAllNestedReply(replyId);
			
			return new ResponseEntity<List<NestedReplyVO>>(list, HttpStatus.OK);
	}
	
	@PutMapping("/nestedreplies/{nestedReplyId}")
	public ResponseEntity<Integer> updateNestedReply(
			@PathVariable("nestedReplyId") int nestedReplyId,
			@RequestBody String nestedReplyContentJson) {
	
			log.info("updateNestedReply()");
			log.info("nestedReplyId = " + nestedReplyId);
			log.info("받은 nestedReplyContent(JSON) = " + nestedReplyContentJson);
			
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
	 
	 @DeleteMapping("/nestedreplies/{nestedReplyId}/{replyId}")
	 public ResponseEntity<Integer> deleteNestedReply(
			 @PathVariable("nestedReplyId") int nestedReplyId,
			 @PathVariable("replyId") int replyId) {
		 log.info("deleteNestedReply()");
		 log.info("nestedReplyId = " + nestedReplyId);
		 
		 int result = nestedReplyService.deleteNestedReply(nestedReplyId, replyId);
		 
		 return new ResponseEntity<Integer>(result, HttpStatus.OK);
		 
	 }
	 
}
