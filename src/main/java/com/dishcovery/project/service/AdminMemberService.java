package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.util.Pagination;

import java.util.List;
import java.util.Map;

public interface AdminMemberService {

    /**
     * 회원 가입
     * @param memberVO 회원 정보
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean registerMember(MemberVO memberVO);

    /**
     * 이메일 중복 체크
     * @param email 이메일 주소
     * @return 중복 여부 (true: 중복, false: 중복 아님)
     */
    boolean isEmailDuplicated(String email);

    /**
     * 이메일 인증 키 업데이트
     * @param map 이메일, 인증 키
     */
    void updateAuthKey(Map<String, String> map);

    /**
     * 이메일로 회원 정보 조회
     * @param email 이메일 주소
     * @return 회원 정보, 존재하지 않으면 null
     */
    MemberVO getMemberByEmail(String email);

    /**
     * 전체 회원 조회 (페이징)
     * @param pagination 페이징 정보
     * @return 회원 목록
     */
    List<MemberVO> getAllMembers(Pagination pagination);

    /**
     * 회원 ID로 회원 정보 조회
     * @param memberId 회원 ID
     * @return 회원 정보, 존재하지 않으면 null
     */
    MemberVO getMemberById(int memberId);

    /**
     * 회원 ID로 권한 목록 조회
     * @param memberId 회원 ID
     * @return 권한 목록
     */
    List<String> getRolesByMemberId(int memberId);

    /**
     * 회원 권한 추가
     * @param memberId 회원 ID
     * @param roleName 권한 이름
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean addMemberRole(int memberId, String roleName);

    /**
     * 회원 권한 전체 삭제
     * @param memberId 회원 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean deleteMemberRole(int memberId);

    /**
     * 회원 권한 삭제 (특정 권한)
     * @param memberId 회원 ID
     * @param roleName 권한 이름
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean deleteMemberRoleByName(int memberId, String roleName);

    /**
     * 전체 권한 목록 조회
     * @return 권한 목록
     */
    List<String> getAllRoles();

    /**
     * 이메일로 회원 검색 (페이징)
     * @param pagination 페이징 정보
     * @param keyword 검색어
     * @return 회원 목록
     */
    List<MemberVO> searchMembersByEmail(Pagination pagination, String keyword);

    /**
     * 이름으로 회원 검색 (페이징)
     * @param pagination 페이징 정보
     * @param keyword 검색어
     * @return 회원 목록
     */
    List<MemberVO> searchMembersByName(Pagination pagination, String keyword);

    /**
     * 전체 회원 수 조회
     * @param pagination 페이징 정보
     * @return 전체 회원 수
     */
    int getTotalMemberCount(Pagination pagination);

    /**
     * 검색 조건에 따른 회원 수 조회
     * @param searchType 검색 타입 (email, name)
     * @param keyword 검색어
     * @return 검색 결과 회원 수
     */
    int getSearchMemberCount(String searchType, String keyword);

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
     * @param password 새로운 비밀번호
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean updatePassword(int memberId, String password);

    /**
     * 회원 정보 업데이트
     * @param memberVO 업데이트할 회원 정보
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean updateMember(MemberVO memberVO);

    /**
     * 회원 삭제
     * @param memberId 회원 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean deleteMember(int memberId);

    /**
     * 회원 인증 상태 업데이트 (활성화/비활성화)
     * @param memberId 회원 ID
     * @param authStatus 변경할 인증 상태 (0: 비활성화, 1: 활성화)
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean updateMemberAuthStatus(int memberId, int authStatus);
}