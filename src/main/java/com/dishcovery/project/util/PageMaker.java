package com.dishcovery.project.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 페이지 번호를 구성하기 위한 클래스
@Getter
@Setter
@ToString
public class PageMaker {
	private Pagination pagination; // 페이징 처리 객체
	private int totalCount; // 전체 게시글 수
	private int pageCount; // 화면에 표시되는 페이지 번호 수
	private int replyTotalCount; // 총 댓글 수
	private int reviewTotalCount; // 촐 리뷰 수
	
	public PageMaker() {
		this.pageCount = 5; // 5개의 페이지 번호만 화면에 표시
	}	
	
	
	// reply의 총 수
		public void setReplyTotalCount(int replyTotalCount) {
			this.replyTotalCount = replyTotalCount; // ✅ 제대로 값이 들어가는지 디버깅
		    System.out.println("setReplyTotalCount 호출됨: " + this.replyTotalCount);
		}
	
	// review의 총 수
		public void setReviewTotalCount(int reviewTotalCount) {
			this.reviewTotalCount = reviewTotalCount; 
			System.out.println("setReviewTotalCount 호출됨: " + this.reviewTotalCount);					
		}
		
	// ✅ 댓글 페이지 개수 계산
	private int calcReplyTotalPageNum() {
	return (int) Math.ceil((double) replyTotalCount / pagination.getPageSize());
	
	}
	
	 // ✅ 리뷰 페이지 개수 계산
    private int calcReviewTotalPageNum() {
    return (int) Math.ceil((double) reviewTotalCount / pagination.getPageSize());
    
    }
		
	// 전체 페이지 번호 계산값
	private int calcTotalPageNum() {
		// Math.ceil (올림)
		// Math.floor (버림)		
		return (int) Math.ceil((double) totalCount / pagination.getPageSize()); 
		
	} // end calcTotalPageNum()
	
	// 임시 끝 번호 계산값
	private int tempEndNum() {
		return (int) Math.ceil((double) pagination.getPageNum() / pageCount) * pageCount;
	} // end tempEndNum()
	
	// 시작 페이지 번호 계산
	public int getStartNum() {
		return tempEndNum() - (pageCount - 1);
	} // end getStartNum()
	
	// 끝 페이지 번호 계산
	// ✅ 끝 페이지 번호 계산 (각 데이터별 페이지 수 고려)
    public int getEndNum() {
        int tempEndNum = tempEndNum();
        int totalPageNum = Math.max(calcTotalPageNum(), Math.max(calcReplyTotalPageNum(), calcReviewTotalPageNum()));

        return Math.min(tempEndNum, totalPageNum);
    }
	
	// 화면에 보이는 시작 페이지 번호보다 작은 숫자의 페이지 번호 존재 여부
	public boolean isPrev() {
		// prev 유무 확인
		if (getStartNum() <= 1) {
			return false;
		} else {
			return true;
		}
	} // end isPrev()
	
	// ✅ 다음 페이지 존재 여부
    public boolean isNext() {
        return getEndNum() < Math.max(calcTotalPageNum(), Math.max(calcReplyTotalPageNum(), calcReviewTotalPageNum()));
    }
	
} // end PageMaker