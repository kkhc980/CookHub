package com.dishcovery.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	
	@GetMapping("/all/{boardId}") // GET : �뙎湲� �꽑�깮(all)
	public ResponseEntity<List<ReplyVO>> readAllReply(
			@PathVariable("boardId") int boardId){
		// @PathVariable("boardId") : {boardId} 媛믪쓣 �꽕�젙�맂 蹂��닔�뿉 ���옣
		log.info("readdAllReply()");
		log.info("boardId = " + boardId);
		
		List<ReplyVO> list = replyService.getAllReply(boardId);
		// ResponseEntity<T> : T�쓽 ���엯�� �봽濡좏듃 side濡� �쟾�넚�맆 �뜲�씠�꽣 ���엯�쑝濡쒖꽑�뼵
		return new ResponseEntity<List<ReplyVO>>(list, HttpStatus.OK);
	}
	
	@PutMapping("/{replyId}") // PUT : �뙎湲� �닔�젙
	 public ResponseEntity<Integer> updateReply(
		        @PathVariable("replyId") int replyId,
		        @RequestBody String replyContent
		        ){
		log.info("updateReply()");
		log.info("replyId = " + replyId);
		int result = replyService.updateReply(replyId, replyContent);
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
	}
	
	@DeleteMapping("/{replyId}/{boardId}") // DELETE : �뙎湲� �궘�젣
	 public ResponseEntity<Integer> deleteReply(
			 @PathVariable("replyId") int replyId,
			 @PathVariable("boardId") int boardId) {
		log.info("deleteReply()");
		log.info("replyId = " + replyId);
		
		int result = replyService.deleteReply(replyId, boardId);
		
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
		}
			 

}
