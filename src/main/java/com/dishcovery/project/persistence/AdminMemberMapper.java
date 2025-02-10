package com.dishcovery.project.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.util.Pagination;

@Mapper
public interface AdminMemberMapper {
    /**
     * 회원 가입
     * @param memberVO 회원 정보
     * @return 삽입된 행 수
     */
    int insert(MemberVO memberVO);

    /**
     * 이메일 중복 체크
     * @param email 이메일 주소
     * @return 중복된 이메일 수
     */
    int selectDupCheckEmail(String email);

    /**
     * 이메일 인증 키 업데이트
     * @param map 이메일, 인증 키
     */
    void updateAuthKey(Map<String, String> map);

    /**
     * 이메일로 회원 정보 조회
     * @param email 이메일 주소
     * @return 회원 정보
     */
    MemberVO selectEmail(String email);

    // 회원 관리 기능

    /**
     * 전체 회원 조회 (페이징)
     * @param pagination 페이징 정보
     * @return 회원 목록
     */
    List<MemberVO> selectAllMembers(Pagination pagination);

    /**
     * 회원 ID로 회원 정보 조회
     * @param memberId 회원 ID
     * @return 회원 정보
     */
    MemberVO selectMemberById(int memberId);

    /**
     * 회원 ID로 권한 목록 조회
     * @param memberId 회원 ID
     * @return 권한 목록
     */
    List<String> selectRolesByMemberId(int memberId);

    /**
     * 회원 권한 추가
     * @param map 회원 ID, 권한 이름
     */
    void insertMemberRole(Map<String, Object> map);

    /**
     * 회원 권한 전체 삭제
     * @param memberId 회원 ID
     */
    void deleteMemberRole(int memberId);

    /**
     * 회원 권한 삭제 (특정 권한)
     * @param map 회원 ID, 권한 이름
     */
    void deleteMemberRoleByName(Map<String, Object> map);

    /**
     * 전체 권한 목록 조회
     * @return 권한 목록
     */
    List<String> selectAllRoles();

    /**
     * 이메일로 회원 검색 (페이징)
     * @param pagination 페이징 정보
     * @param keyword 검색어
     * @return 회원 목록
     */
    List<MemberVO> selectMemberByEmail(Pagination pagination, @Param("keyword") String keyword);

    /**
     * 이름으로 회원 검색 (페이징)
     * @param pagination 페이징 정보
     * @param keyword 검색어
     * @return 회원 목록
     */
    List<MemberVO> selectMemberByName(Pagination pagination, @Param("keyword") String keyword);

    /**
     * 전체 회원 수 조회
     * @param pagination 페이징 정보
     * @return 전체 회원 수
     */
    int getTotalCount(Pagination pagination);

    /**
     * 검색 조건에 따른 회원 수 조회
     * @param map 검색 조건 (searchType, keyword)
     * @return 검색 결과 회원 수
     */
    int getTotalCountSearch(Map<String, Object> map);

    /**
     * 전체 회원 수 조회
     * @return 전체 회원 수
     */
    int getTotalMemberCount();

    /**
     * 활성 회원 수 조회
     * @return 활성 회원 수
     */
    int getActiveMemberCount();

    /**
     * 비활성 회원 수 조회
     * @return 비활성 회원 수
     */
    int getInactiveMemberCount();

    /**
     * 신규 회원 수 조회 (최근 7일)
     * @return 신규 회원 수
     */
    int getNewMemberCount();

    /**
     * 권한별 회원 수 조회
     * @return 권한별 회원 수
     */
    List<Map<String, Object>> getRoleCounts();

    /**
     * 비밀번호 변경
     * @param memberId 회원 ID
     * @param password 새로운 비밀번호 (암호화된)
     */
    void updatePassword(@Param("memberId") int memberId, @Param("password") String password);

    /**
     * 회원 정보 업데이트
     * @param memberVO 업데이트할 회원 정보
     */
    void updateMember(MemberVO memberVO);

    /**
     * 회원 삭제
     * @param memberId 회원 ID
     */
    void delete(int memberId);

    /**
     * 회원 인증 상태 업데이트
     * @param memberId 회원 ID
     * @param authStatus 인증 상태
     * @return 업데이트된 행 수
     */
    int updateAuthStatus(@Param("memberId") int memberId, @Param("authStatus") int authStatus);
}