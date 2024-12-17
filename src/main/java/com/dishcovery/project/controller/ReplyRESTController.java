package com.dishcovery.project.controller;

import com.dishcovery.project.domain.ReplyVO;
import com.dishcovery.project.service.ReplyService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/reply")
@Log4j
public class ReplyRESTController {
	@Autowired
	private ReplyService replyService;
	
	@PostMapping
	public ResponseEntity<Integer> createReply(@RequestBody ReplyVO replyVO){
		log.info("createReply()");
		
		int result = replyService.createReply(replyVO);
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
	}
	
	@GetMapping("/all/{boardId}") // GET : 댓글 선택(all)
	public ResponseEntity<List<ReplyVO>> readAllReply(
			@PathVariable("boardId") int boardId){
		// @PathVariable("boardId") : {boardId} 값을 설정된 변수에 저장
		log.info("readdAllReply()");
		log.info("boardId = " + boardId);
		
		List<ReplyVO> list = replyService.getAllReply(boardId);
		// ResponseEntity<T> : T의 타입은 프론트 side로 전송될 데이터 타입으로선언
		return new ResponseEntity<List<ReplyVO>>(list, HttpStatus.OK);
	}
	
	@PutMapping("/{replyId}") // PUT : 댓글 수정
	 public ResponseEntity<Integer> updateReply(
		        @PathVariable("replyId") int replyId,
		        @RequestBody String replyContent
		        ){
		log.info("updateReply()");
		log.info("replyId = " + replyId);
		int result = replyService.updateReply(replyId, replyContent);
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
	}
	
	@DeleteMapping("/{replyId}/{boardId}") // DELETE : 댓글 삭제
	 public ResponseEntity<Integer> deleteReply(
			 @PathVariable("replyId") int replyId,
			 @PathVariable("boardId") int boardId) {
		log.info("deleteReply()");
		log.info("replyId = " + replyId);
		
		int result = replyService.deleteReply(replyId, boardId);
		
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
		}
			 

}
