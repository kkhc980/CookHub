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

import com.dishcovery.project.domain.ReplyVO;
import com.dishcovery.project.service.ReplyService;

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
      log.info("createReply()");
      try {
      int result = replyService.createReply(replyVO);
      return new ResponseEntity<Integer>(result, HttpStatus.OK);
         } catch (Exception e) {
            log.error("Error creating reply", e);
            
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
         }
   }
   
   @GetMapping("/all/{recipeBoardId}") // GET : 댓글 선택(all)
   public ResponseEntity<List<ReplyVO>> readAllReply(
         @PathVariable("recipeBoardId") int recipeBoardId){
      // @PathVariable("recipeBoardId") : {recipeBoardId} 값을 설정된 변수에 저장
      log.info("readdAllReply()");
      log.info("recipeBoardId = " + recipeBoardId);
      
      List<ReplyVO> list = replyService.getAllReply(recipeBoardId);
      // ResponseEntity<T> : T의 타입은 프론트 side로 전송될 데이터 타입으로선언
      return new ResponseEntity<List<ReplyVO>>(list, HttpStatus.OK);
   }
   
   
//   @PreAuthorize("#customUser.memberVO.memberId == #reply.memberId")
   @PutMapping("replies/{replyId}") // PUT : 댓글 수정
    public ResponseEntity<Integer> updateReply(
          @PathVariable("replyId") int replyId,
           @RequestBody String replyContentJson) { // ✅ JSON 문자열을 받음

          log.info("updateReply()");
          log.info("replyId = " + replyId);
          log.info("받은 replyContent(JSON) = " + replyContentJson); // ✅ 디버깅용 로그

          // ✅ JSON에서 실제 replyContent 값만 추출
          String replyContent = extractReplyContent(replyContentJson);
          log.info("변환된 replyContent = " + replyContent); // ✅ 변환된 값 확인

          int result = replyService.updateReply(replyId, replyContent);
          return new ResponseEntity<>(result, HttpStatus.OK);
   }
   
   private String extractReplyContent(String json) {
       if (json.contains("replyContent")) {  
           return json.replaceAll(".*\"replyContent\":\"([^\"]+)\".*", "$1");  // ✅ 정규식을 활용하여 문자열 추출
       }
       return json;  
   }
   
   
//   @PreAuthorize("#customUser.memberVO.memberId == #reply.memberId")
   @DeleteMapping("replies/{replyId}/{recipeBoardId}") // DELETE : 댓글 삭제
    public ResponseEntity<Integer> deleteReply(
          @PathVariable("replyId") int replyId,
          @PathVariable("recipeBoardId") int recipeBoardId) {
      log.info("deleteReply()");
      log.info("replyId = " + replyId);
      
      int result = replyService.deleteReply(replyId, recipeBoardId);   
      
      return new ResponseEntity<Integer>(result, HttpStatus.OK);
      }
   
   

}
