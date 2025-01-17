package com.dishcovery.project.util;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dishcovery.project.persistence.RecipeBoardMapper;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class BoardScheduler {

    private final RecipeBoardMapper mapper;

    public BoardScheduler(RecipeBoardMapper mapper) {
        this.mapper = mapper;
    }

    @Scheduled(cron = "0 30 12 * * ?")// 매일 12시 30분 실행
    public void scheduledDeleteOldViewLogs() {
        deleteOldViewLogs();
        log.info("Scheduled task executed: Old view logs deleted.");
    }

    private void deleteOldViewLogs() {
        log.info("Starting deleteOldViewLogs...");
        int deletedRows = mapper.deleteOldViewLogs(); // SQL 실행
        log.info("Number of rows deleted: " + deletedRows);

        if (deletedRows > 0) {
            log.info("Old view logs deleted successfully.");
        } else {
            log.info("No old view logs to delete.");
        }
    }
    
//    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE) // 10초 후 스케줄러 실행 (테스트용)
    @Scheduled(cron = "0 30 12 * * ?") // 매일 12시 30분 실행
    public void cleanUnusedThumbnails() {
        log.info("Starting cleanup of unused thumbnails...");

        try {
            // 데이터베이스에 저장된 모든 썸네일 경로 가져오기
            List<String> dbThumbnails = mapper.getAllThumbnailPaths();

            // 업로드 디렉터리 설정
            File uploadDir = new File("C:" + File.separator + "uploads");
            if (!uploadDir.exists() || !uploadDir.isDirectory()) {
                log.warn("Upload directory does not exist: " + uploadDir.getPath());
                return;
            }

            // 데이터베이스 경로를 Set으로 변환 (검색 속도 향상)
            Set<String> dbThumbnailsSet = new HashSet<>(dbThumbnails);

            // 디렉터리 탐색 및 정리
            cleanDirectory(uploadDir, dbThumbnailsSet);

            log.info("Cleanup of unused thumbnails completed.");
        } catch (Exception e) {
            log.error("Error during cleanup of unused thumbnails", e);
        }
    }

    private void cleanDirectory(File dir, Set<String> dbThumbnailsSet) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // 하위 디렉터리 탐색
                cleanDirectory(file, dbThumbnailsSet);
            } else {
                // 절대 경로에서 상대 경로 추출
                String relativePath = file.getPath().substring("C:\\uploads\\".length()).replace(File.separator, "/");

                // 데이터베이스 경로와 비교하여 삭제
                if (!dbThumbnailsSet.contains(relativePath)) {
                    if (file.delete()) {
                        log.info("Deleted unused file: " + file.getPath());
                    } else {
                        log.warn("Failed to delete file: " + file.getPath());
                    }
                }
            }
        }
    }
}