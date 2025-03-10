package com.dishcovery.project.util;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dishcovery.project.config.ServletConfig;
import com.dishcovery.project.persistence.ReviewAttachMapper;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
@EnableScheduling
public class ReviewImageScheduler {
	
	@Autowired
	private ReviewAttachMapper reviewAttachMapper;
	
	// 이미지가 저장된 폴더 경로
	@Autowired
	private String uploadPath;
	
	
	@Scheduled(cron = "0 0 4 * * ?")
//	@Scheduled(fixedRate = 10000) // 10초마다 실행 (테스트용)
	public void cleanUpUnusedReviewImages() {
		log.info("🕒 스케줄러 실행됨 (현재 시간: " + System.currentTimeMillis() + ")");
		
		log.info("🛠️ 현재 업로드 경로: " + uploadPath);
		
		log.info("리뷰 이미지 정리 스케줄러 실행");
		
		// 1️⃣ DB에서 현재 존재하는 리뷰 이미지 목록 조회
		List<String> dbFileList = reviewAttachMapper.getAllAttachFileNames();
		
		// 2️⃣ 서버 디렉토리에서 현재 존재하는 파일 목록 조회
		File folder = new File(uploadPath);
		if (!folder.exists()) {
			log.warn("업로드 폭더가 존재하지 않습니다: " + uploadPath);
			return;
		}
		
		File[] files = folder.listFiles();
		if (files == null) {
			log.warn("폴더가 비어 있습니다: " + uploadPath);
			return;
		}
		
		List<String> dbFileNames = dbFileList.stream().map(String::trim).collect(Collectors.toList());
		
		// 3️⃣ DB에 없는 파일 삭제
        int deletedCount = 0;
        for (File file : files) {
            if (!dbFileNames.contains(file.getName())) {
            	log.info("🛠️ 삭제 시도 파일: " + file.getAbsolutePath());
            	
            	if (!file.exists()) {
                    log.warn("❌ 파일이 존재하지 않음: " + file.getAbsolutePath());
                    continue;
                }
            	
                if (file.delete()) {
                    deletedCount++;
                    log.info("🗑️ 삭제된 파일: " + file.getName());
                } else {
                    log.warn("⚠️ 파일 삭제 실패: " + file.getName());
                }
            }
        }

        log.info("✅ 정리 완료: " + deletedCount + "개 파일 삭제됨");
    }
		
}

