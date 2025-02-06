package com.dishcovery.project.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnails;

@Log4j
public class ImageUploadUtil {
    
    /**
     * 파일 이름에서 확장자를 제외한 실제 파일 이름을 추출
     * 
     * @param fileName 파일 이름
     * @return 실제 파일 이름
     */
    public static String subStrName(String fileName) {
    	// FilenameUtils.normalize() : 파일 이름 정규화 메서드
        String normalizeName = FilenameUtils.normalize(fileName);
        int dotIndex = normalizeName.lastIndexOf('.');

        String realName = normalizeName.substring(0, dotIndex);
        return realName;
    }
    
    /**
     * 파일 이름에서 확장자를 추출
     * 
     * @param fileName 파일 이름
     * @return 확장자
     */
    public static String subStrExtension(String fileName) {
        // 파일 이름에서 마지막 '.'의 인덱스를 찾습니다.
        int dotIndex = fileName.lastIndexOf('.');

        // '.' 이후의 문자열을 확장자로 추출합니다.
        String extension = fileName.substring(dotIndex + 1);

        return extension.toLowerCase(); // 소문자로 변경
    }
    
    /**
     * 파일이 저장되는 폴더 이름을 날짜 형식(yyyy/MM/dd)으로 생성
     * 
     * @return 날짜 형식의 폴더 이름
     */
    public static String makeDatePath() {
        Calendar calendar = Calendar.getInstance();
        
        String yearPath = String.valueOf(calendar.get(Calendar.YEAR));
        log.info("yearPath: " + yearPath);
        
        String monthPath = yearPath
                + File.separator
                + new DecimalFormat("00")
                    .format(calendar.get(Calendar.MONTH) + 1);
        log.info("monthPath: " + monthPath);
        
        
        String datePath = monthPath
                + File.separator
                + new DecimalFormat("00")
                    .format(calendar.get(Calendar.DATE));
        log.info("datePath: " + datePath);
        
        return datePath;
    }
    
    /**
     * 파일을 저장
     * 
     * @param uploadPath 파일 업로드 경로
     * @param file 업로드된 파일
     * @param chgName UUID
     */
    public static void saveFile(String uploadPath, MultipartFile file, String chgName) {
        
        File realUploadPath = new File(uploadPath, makeDatePath());
        if (!realUploadPath.exists()) {
            realUploadPath.mkdirs();
            log.info(realUploadPath.getPath() + " successfully created.");
        } else {
            log.info(realUploadPath.getPath() + " already exists.");
        }
        
        File saveFile = new File(realUploadPath, chgName);
        try {
            file.transferTo(saveFile);
            
            log.info("file upload scuccess");
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } 
        
    }
    
    /**
     * 파일을 삭제
     * 
     * @param uploadPath 파일 업로드 경로
     * @param path 파일이 저장된 날짜 경로
     * @param chgName 저장된 파일 이름
     */
    public static void deleteFile(String uploadPath, String path, String chgName) {
        // 삭제할 파일의 전체 경로 생성
        String fullPath = uploadPath + File.separator + path + File.separator + chgName;
        
        // 파일 객체 생성
        File file = new File(fullPath);
        
        // 파일이 존재하는지 확인하고 삭제
        if(file.exists()) {
            if(file.delete()) {
                System.out.println(fullPath + " file delete success.");
            } else {
                System.out.println(fullPath + " file delete failed.");
            }
        } else {
            System.out.println(fullPath + " file not found.");
        }
    }
    
    /**
     * 이미지 파일인지 확인
     * 
     * @param file 전송된 파일 객체
     * @return 파일이면 true
     */
    public static boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // ContentType 정보 참조
        String contentType = file.getContentType();

        // Content Type이 "image/"로 시작하는지 확인
        return contentType != null && contentType.startsWith("image/");
    }
    
    /**
     * 원본 이미지로 섬네일 파일을 생성
     * 
     * @param uploadPath 업로드된 파일의 기본 경로
     * @param path 업로드된 파일의 상세 경로
     * @param chgName 변경된 파일명
     * @param extension 파일 확장자
     */
    public static void createThumbnail(String uploadPath, String path, 
    		String chgName, String extension) {
    	String realUploadPath = uploadPath + File.separator + path;
    	String thumbnailName = "t_" + chgName; // 섬네일 파일 이름
    	
    	// 섬네일 파일 저장 경로 및 이름
    	File destPath = new File(realUploadPath, thumbnailName); 
    	// 원본 파일 저장 경로 및 이름
        File savePath = new File(realUploadPath, chgName); 
    	try {
			Thumbnails.of(savePath)
			          .size(100, 100) // 썸네일 크기 지정
			          .outputFormat(extension) // 확장자 설정
			          .toFile(destPath); // 저장될 경로와 이름
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
}
