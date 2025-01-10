package com.dishcovery.project.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 페이지 번호와 페이지 사이즈를 바탕으로 시작 번호와 끝 번호를 생성해주는 클래스
@Getter
@Setter
@ToString
public class Pagination {
	private int pageNum; // 현재 페이지 번호
	private int pageSize; // 현재 페이지 사이즈
	private String type; // 검색 항목
	private String keyword; // 검색 키워드 

    // 필터링 조건
    private List<Integer> ingredientIds; // 선택된 재료 ID 리스트
    private Integer typeId; // 타입 ID
    private Integer situationId; // 상황 ID
    private Integer methodId; // 방법 ID
    private String hashtag;
    
	public Pagination() {
		this.pageNum = 1; // 기본 페이지 번호 설정
		this.pageSize = 8; // 기본 페이지 사이즈 설정
	}
	
	public Pagination(int page, int pageSize) {
		this.pageNum = page;
		this.pageSize = pageSize;
	}

	// 선택된 페이지의 시작 글 일련번호(rn) - #{start}
	public int getStart() {
		return (this.pageNum - 1) * this.pageSize + 1;
	}

	// 선택된 페이지의 마지막 글 일련번호(rn) - #{end}
	public int getEnd() {
		return this.pageNum * this.pageSize;
	}
	
	public String getIngredientIdsAsString() {
	    if (ingredientIds == null || ingredientIds.isEmpty()) {
	        return "1"; // 기본값
	    }
	    return ingredientIds.stream()
	                        .map(String::valueOf)
	                        .collect(Collectors.joining(","));
	}

    // ingredientIds를 문자열에서 리스트로 변환
	public void setIngredientIdsFromString(String ingredientIdsStr) {
	    if (ingredientIdsStr != null && !ingredientIdsStr.isEmpty()) {
	        try {
	            // 공백과 대괄호 제거 후 리스트로 변환
	            this.ingredientIds = Arrays.stream(ingredientIdsStr.split(","))
	                                        .filter(id -> !id.trim().isEmpty()) // 빈 문자열 필터링
	                                        .map(Integer::parseInt)
	                                        .collect(Collectors.toList());
	        } catch (NumberFormatException e) {
	            this.ingredientIds = null; // 잘못된 값이 있으면 null로 설정
	        }
	    } else {
	        this.ingredientIds = null; // 기본값으로 null 설정
	    }
	}

    // 기본 상태 설정 (필터 값이 null일 경우 기본값 할당)
    public void setDefaultValues() {
        if (this.ingredientIds == null) {
            this.ingredientIds = List.of(1); // 기본값: "전체"
        }
        if (this.typeId == null) {
            this.typeId = 1; // 기본값: "전체"
        }
        if (this.situationId == null) {
            this.situationId = 1; // 기본값: "전체"
        }
        if (this.methodId == null) {
            this.methodId = 1; // 기본값: "전체"
        }
        if (this.hashtag == null || this.hashtag.trim().isEmpty()) {
            this.hashtag = null; // hashtag는 빈 문자열이면 null로 처리
        }
    }

} // end Pagination
