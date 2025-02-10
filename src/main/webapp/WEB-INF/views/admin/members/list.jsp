<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> --%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원 관리</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        .table-responsive {
            overflow-x: auto;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgb(0, 0, 0);
            background-color: rgba(0, 0, 0, 0.4);
        }

        .modal-content {
            background-color: #fefefe;
            margin: 15% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 80%;
        }

        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
        }

        .close:hover,
        .close:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }
    </style>
</head>
<body>
<div class="container mt-5">
    <h2>회원 목록</h2>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <form id="searchForm" class="d-flex" action="/admin/members/search" method="get">
            <input type="hidden" name="page" value="${pageMaker.pagination.pageNum}">
            <input type="hidden" name="pageSize" value="${pageMaker.pagination.pageSize}">
            <select class="form-select me-2" style="width: auto;" id="searchType" name="searchType">
                <option value="" ${empty param.searchType ? 'selected' : ''}>검색 조건</option>
                <option value="email" ${param.searchType == 'email' ? 'selected' : ''}>이메일</option>
                <option value="name" ${param.searchType == 'name' ? 'selected' : ''}>이름</option>
            </select>
            <input type="text" class="form-control me-2" style="width: 200px;" id="keyword" name="keyword"
                   placeholder="검색어를 입력하세요." value="${param.keyword}">
            <button type="submit" class="btn btn-primary">검색</button>
            <button type="button" class="btn btn-secondary ms-2" onclick="resetForm()">초기화</button>
        </form>
    </div>
    <div class="table-responsive">
        <table class="table table-bordered table-hover">
            <thead>
            <tr>
                <th>회원 ID</th>
                <th>이메일</th>
                <th>이름</th>
                <th>전화번호</th>
                <th>가입일</th>
                <th>수정일</th>
                <th>상태</th>
                <th>관리</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${not empty memberList}">
                    <c:forEach var="member" items="${memberList}">
                        <tr>
                            <td>${member.memberId}</td>
                            <td>${member.email}</td>
                            <td>${member.name}</td>
                            <td>${member.phone}</td>
                            <td>
                                <fmt:formatDate value="${member.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            </td>
                            <td>
                                <fmt:formatDate value="${member.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${member.authStatus == 1}">활성</c:when>
                                    <c:otherwise>비활성</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <button type="button" class="btn btn-sm btn-warning"
                                        onclick="toggleStatus(${member.memberId}, ${member.authStatus})">
                                    <c:choose>
                                        <c:when test="${member.authStatus == 1}">비활성화</c:when>
                                        <c:otherwise>활성화</c:otherwise>
                                    </c:choose>
                                </button>
                                <button type="button" class="btn btn-sm btn-danger"
                                        onclick="openDeleteModal(${member.memberId})">삭제
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="8">조회된 회원이 없습니다.</td>
                    </tr>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
    <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">
            <c:if test="${pageMaker.prev}">
                <li class="page-item">
                    <c:url var="prevLink" value="/admin/members/list">
                        <c:param name="page" value="${pageMaker.startPage - 1}"/>
                        <c:param name="pageSize" value="${pageMaker.pagination.pageSize}"/>
                        <c:if test="${not empty param.searchType}">
                            <c:param name="searchType" value="${param.searchType}"/>
                        </c:if>
                        <c:if test="${not empty param.keyword}">
                            <c:param name="keyword" value="${param.keyword}"/>
                        </c:if>
                    </c:url>
                    <a class="page-link" href="${prevLink}" aria-label="Previous">
                        <span aria-hidden="true">«</span>
                    </a>
                </li>
            </c:if>
            <c:forEach var="i" begin="${pageMaker.startPage}" end="${pageMaker.endPage}">
                <li class="page-item ${pageMaker.pagination.pageNum == i ? 'active' : ''}">
                    <c:url var="pageLink" value="/admin/members/list">
                        <c:param name="page" value="${i}"/>
                        <c:param name="pageSize" value="${pageMaker.pagination.pageSize}"/>
                        <c:if test="${not empty param.searchType}">
                            <c:param name="searchType" value="${param.searchType}"/>
                        </c:if>
                        <c:if test="${not empty param.keyword}">
                            <c:param name="keyword" value="${param.keyword}"/>
                        </c:if>
                    </c:url>
                    <a class="page-link" href="${pageLink}">${i}</a>
                </li>
            </c:forEach>
            <c:if test="${pageMaker.next}">
                <li class="page-item">
                    <c:url var="nextLink" value="/admin/members/list">
                        <c:param name="page" value="${pageMaker.endPage + 1}"/>
                        <c:param name="pageSize" value="${pageMaker.pagination.pageSize}"/>
                        <c:if test="${not empty param.searchType}">
                            <c:param name="searchType" value="${param.searchType}"/>
                        </c:if>
                        <c:if test="${not empty param.keyword}">
                            <c:param name="keyword" value="${param.keyword}"/>
                        </c:if>
                    </c:url>
                    <a class="page-link" href="${nextLink}" aria-label="Next">
                        <span aria-hidden="true">»</span>
                    </a>
                </li>
            </c:if>
        </ul>
    </nav>
</div>

<!-- 삭제 모달 -->
<div id="deleteModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeDeleteModal()">×</span>
        <h3>회원 삭제</h3>
        <p>정말로 삭제하시겠습니까?</p>
        <form id="deleteForm">
            <%-- <sec:csrfInput/> --%>
            <input type="hidden" id="deleteMemberId">
            <div class="form-check">
                <input class="form-check-input" type="checkbox" value="" id="permanentDelete">
                <label class="form-check-label" for="permanentDelete">
                    영구 삭제
                </label>
            </div>
            <button type="button" class="btn btn-danger" onclick="deleteMember()">삭제</button>
            <button type="button" class="btn btn-secondary" onclick="closeDeleteModal()">취소</button>
        </form>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script>
   
   function getContextPath() {
    var hostIndex = location.href.indexOf( location.host ) + location.host.length;
    return location.href.substring( hostIndex, location.href.indexOf('/', hostIndex + 1) );
   }
   
   function path(url){
    return getContextPath() + url;
   }

    function resetForm() {
        document.getElementById('searchType').value = '';
        document.getElementById('keyword').value = '';
        document.getElementById('searchForm').submit();
    }

    function toggleStatus(memberId, currentStatus) {
        if (confirm("정말로 상태를 변경하시겠습니까?")) {
            let newStatus = currentStatus === 1 ? 0 : 1;
            fetch(path('/admin/members/status/update'), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    memberId: memberId,
                    authStatus: newStatus
                   // "<sec:csrfTokenParameterName/>": "<sec:csrfTokenValue/>" // Spring Security 제거
                })
            })
            .then(response => {
                if (response.ok) {
                    alert('회원 상태 변경 완료');
                    window.location.reload();
                } else {
                    alert('회원 상태 변경 실패');
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });
        }
    }

    function openDeleteModal(memberId) {
        document.getElementById('deleteModal').style.display = "block";
        document.getElementById('deleteMemberId').value = memberId;
    }

    function closeDeleteModal() {
        document.getElementById('deleteModal').style.display = "none";
    }

    function deleteMember() {
        if (confirm("정말로 삭제하시겠습니까?")) {
            const memberId = document.getElementById('deleteMemberId').value;
            const permanentDelete = document.getElementById('permanentDelete').checked;
            fetch(path('/admin/members/delete/' + memberId), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    permanent: permanentDelete
                   // "<sec:csrfTokenParameterName/>": "<sec:csrfTokenValue/>"  // Spring Security 제거
                })
            })
            .then(response => {
                if (response.ok) {
                    alert('회원 삭제 완료');
                    window.location.reload();
                } else {
                    alert('회원 삭제 실패');
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });
            closeDeleteModal();
        }
    }
</script>
</body>
</html>
