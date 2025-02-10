package com.dishcovery.project.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import com.dishcovery.project.domain.ReviewAttachDTO;
import com.dishcovery.project.service.ReviewAttachService;
import com.dishcovery.project.util.ImageUploadUtil;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping(value = "/image")
@Log4j
public class ImageUploadRESTController {
	
	@Autowired
    private String uploadPath;
	
	@Autowired
	private ReviewAttachService reviewAttachService;
	
	@PostMapping
	public ResponseEntity<ArrayList<ReviewAttachDTO>> createImage(@RequestParam("files")MultipartFile[] files) {
		log.info("요청됨: createImage()");
		
		ArrayList<ReviewAttachDTO> list = new ArrayList<>();
		
		for (MultipartFile file : files) {
			// UUID 생성
			String chgName = UUID.randomUUID().toString();
			// 파일 저장
			ImageUploadUtil.saveFile(uploadPath, file, chgName);
			
			String path = ImageUploadUtil.makeDatePath();
			String extension = ImageUploadUtil.subStrExtension(file.getOriginalFilename());
			
			ImageUploadUtil.createThumbnail(uploadPath, path, chgName, extension);
			//util 작성
			
			ReviewAttachDTO reviewAttachDTO = new ReviewAttachDTO();
			// 파일 경로 설정
			reviewAttachDTO.setAttachPath(path);
			// 파일 실제 이름 설정
			reviewAttachDTO.setAttachRealName(ImageUploadUtil.subStrName(file.getOriginalFilename()));
			// 파일 변경 이름(UUID) 설정
			reviewAttachDTO.setAttachChgName(chgName);
			// 파일 확장자 설정
			reviewAttachDTO.setAttachExtension(extension);
			
			list.add(reviewAttachDTO);
			
			
		}
		
		return new ResponseEntity<ArrayList<ReviewAttachDTO>>(list, HttpStatus.OK);
		
	}
	
	@GetMapping("/display")
	public ResponseEntity<byte[]> displayImage(String attachPath, String attachChgName, String attachExtension) {
		log.info("display()");
		ResponseEntity<byte[]> entity = null;
		try {
			// 파일을 읽어와서 byte 배열로 변환
			String savedPath = uploadPath + File.separator
					+ attachPath + File.separator + attachChgName;
			if(attachChgName.startsWith("t_")) { // 섬네일 파일에는 확장자 추가
				savedPath += "." + attachExtension;
			} 
			Path path = Paths.get(savedPath);
			byte[] imageBytes = Files.readAllBytes(path);
			
			Path extensionPath = Paths.get("." + attachExtension);
			// 이미지의 MIME 타입 확인하여 적절한 Content-Type 지정
			String contentType = Files.probeContentType(extensionPath);
			
			// HTTP 응답에 byte 배열과 Content-Type을 설정하여 전송
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.parseMediaType(contentType));
			entity = new ResponseEntity<byte[]>(imageBytes, httpHeaders, HttpStatus.OK);
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.notFound().build();
			}
		
			return entity;
		}
	
	@GetMapping("/get")
	public ResponseEntity<byte[]> getImage(int attachId, String attachExtension){
		log.info("getImage()");
		
		ReviewAttachDTO reviewAttachDTO = reviewAttachService.getAttachById(attachId);
		ResponseEntity<byte[]> entity = null;
		
		try {
			// 파일을 읽어와서 byte 배열로 변환
			String savedPath = uploadPath + File.separator 
					+ reviewAttachDTO.getAttachPath() + File.separator;
			
			if(attachExtension != null) {
				savedPath += "t_" + reviewAttachDTO.getAttachChgName() + "." + reviewAttachDTO.getAttachExtension();					
			} else {
				savedPath += reviewAttachDTO.getAttachChgName();
			}
			Path path = Paths.get(savedPath);
			byte[] imageBytes = Files.readAllBytes(path);
			
			Path extensionPath = Paths.get("." + reviewAttachDTO.getAttachExtension());
			// 이미지의 MIME 타입 확인하여 적절한 Content-Type 지정
			String contentType = Files.probeContentType(extensionPath);
			
			// HTTP 응답에 byte 배열과 Content-Type을 설정하여 전송
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.parseMediaType(contentType));
			entity = new ResponseEntity<byte[]>(imageBytes, httpHeaders, HttpStatus.OK);
			
	} catch (IOException e) {
		e.printStackTrace();
		return ResponseEntity.notFound().build(); // 파일 찾을 수 없음을 클라이언트에 알림
		}
		
		return entity;
	}
	
	// 섬네일 및 원본 이미지 삭제 기능
	@PostMapping("/delete")
	public ResponseEntity<Integer> deleteImage(String attachPath, String attachChgName, String attachExtension) {
		log.info("deleteAttach()");
		log.info(attachChgName);
		
		String thumbnailName = "t_" + attachChgName + "." + attachExtension;
		ImageUploadUtil.deleteFile(uploadPath, attachPath, thumbnailName);
		
		return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}
	
	}
	
	

