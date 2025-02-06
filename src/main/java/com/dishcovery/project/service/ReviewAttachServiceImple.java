package com.dishcovery.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dishcovery.project.domain.ReviewAttachDTO;
import com.dishcovery.project.domain.ReviewAttachVO;
import com.dishcovery.project.persistence.ReviewAttachMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ReviewAttachServiceImple implements ReviewAttachService{
	
	@Autowired
	private ReviewAttachMapper reviewAttachMapper; 
	
	@Override
	public ReviewAttachDTO getAttachById(int attachId) {
		log.info("getReviewAttachById()");
		return toDTO(reviewAttachMapper.selectByAttachId(attachId));
	}
	
	// Attach를 AttachDTO로 변환하는 메서드
	private ReviewAttachDTO toDTO(ReviewAttachVO reviewAttach) {
		ReviewAttachDTO reviewAttachDTO = new ReviewAttachDTO();
		reviewAttachDTO.setAttachId(reviewAttach.getAttachId());
		reviewAttachDTO.setRecipeReviewId(reviewAttach.getRecipeReviewId());
		reviewAttachDTO.setAttachPath(reviewAttach.getAttachPath());
		reviewAttachDTO.setAttachRealName(reviewAttach.getAttachRealName());
		reviewAttachDTO.setAttachChgName(reviewAttach.getAttachChgName());
		reviewAttachDTO.setAttachExtension(reviewAttach.getAttachExtension());
		reviewAttachDTO.setAttachDateCreated(reviewAttach.getAttachDateCreated());

	        return reviewAttachDTO;
	}
}
