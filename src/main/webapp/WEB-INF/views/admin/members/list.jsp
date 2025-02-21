<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원 관리</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<style>
/* General Styles */
body {
	font-family: 'Arial', sans-serif;
	background-color: #f8f9fa;
}

.container {
	background-color: #ffffff;
	padding: 20px;
	border-radius: 5px;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

h2 {
	color: #007bff;
	margin-bottom: 20px;
}

/* Table Styles */
.table-responsive {
	overflow-x: auto;
}

.table th {
	background-color: #e9ecef;
	font-weight: bold;
	text-align: center;
}

.table td {
	text-align: center;
	vertical-align: middle; /* Vertically center the content */
}

.table-hover tbody tr:hover {
	background-color: rgba(0, 123, 255, 0.05);
}

/* Button Styles */
.btn-primary {
	background-color: #007bff;
	border-color: #007bff;
}

.btn-primary:hover {
	background-color: #0069d9;
	border-color: #0062cc;
}

.btn-secondary {
	background-color: #6c757d;
	border-color: #6c757d;
}

.btn-secondary:hover {
	background-color: #5a6268;
	border-color: #545b62;
}

.btn-warning {
	background-color: #ffc107;
	border-color: #ffc107;
	color: #212529;
}

.btn-warning:hover {
	background-color: #e0a800;
	border-color: #d39e00;
}

.btn-danger {
	background-color: #dc3545;
	border-color: #dc3545;
}

.btn-danger:hover {
	background-color: #c82333;
	border-color: #bd2130;
}

/* Search Form Styles */
#searchForm .form-control {
	border-radius: 5px;
}

#searchForm .form-select {
	border-radius: 5px;
}

/* Pagination Styles */
.pagination .page-item.active .page-link {
	background-color: #007bff;
	border-color: #007bff;
	color: #fff;
}

.pagination .page-link {
	color: #007bff;
}

.pagination .page-link:hover {
	background-color: #e9ecef;
}

/* Modal Styles */
.modal {
	display: none;
	position: fixed;
	z-index: 1;
	left: 0;
	top: 0;
	width: 100%;
	height: 100%;
	overflow: auto;
	background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
	background-color: #fefefe;
	margin: 15% auto;
	padding: 20px;
	border: 1px solid #888;
	width: 80%;
	border-radius: 5px;
}

.close {
	color: #aaa;
	float: right;
	font-size: 28px;
	font-weight: bold;
}

.close:hover, .close:focus {
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
			<form id="searchForm" class="d-flex"
				action="${pageContext.request.contextPath}/admin/members/search"
				method="get">
				<input type="hidden" name="page"
					value="${pageMaker.pagination.pageNum}"> <input
					type="hidden" name="pageSize"
					value="${pageMaker.pagination.pageSize}"> <select
					class="form-select me-2" style="width: auto;" id="searchType"
					name="searchType">
					<option value="" ${empty param.searchType ? 'selected' : ''}>검색
						조건</option>
					<option value="email"
						${param.searchType == 'email' ? 'selected' : ''}>이메일</option>
					<option value="name"
						${param.searchType == 'name' ? 'selected' : ''}>이름</option>
				</select> <input type="text" class="form-control me-2" style="width: 200px;"
					id="keyword" name="keyword" placeholder="검색어를 입력하세요."
					value="${param.keyword}">
				<button type="submit" class="btn btn-primary">검색</button>
				<button type="button" class="btn btn-secondary ms-2"
					onclick="resetForm()">초기화</button>
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
									<td><fmt:formatDate value="${member.createdAt}"
											pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<td><fmt:formatDate value="${member.updatedAt}"
											pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<td><c:choose>
											<c:when test="${member.authStatus == 0}">인증 대기</c:when>
											<c:when test="${member.authStatus == 1}">활성</c:when>
											<c:otherwise>비활성</c:otherwise>
										</c:choose></td>
									<td><select
										class="update-status form-select form-select-sm"
										data-member-id="${member.memberId}">

											<option value="1" ${member.authStatus == 1 ? 'selected' : ''}>활성</option>
											<option value="2" ${member.authStatus == 2 ? 'selected' : ''}>비활성</option>
									</select>
										<button type="button" class="btn btn-sm btn-danger mt-1"
											onclick="openDeleteModal(${member.memberId})">삭제</button></td>
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
				<c:if
					test="${not empty pageMaker and not empty pageMaker.pagination and pageMaker.isPrev()}">
					<li class="page-item"><c:url var="prevLink"
							value="/admin/members/list">
							<c:param name="page" value="${pageMaker.startNum - 1}" />
							<c:param name="pageSize" value="${pageMaker.pagination.pageSize}" />
							<c:if test="${not empty param.searchType}">
								<c:param name="searchType" value="${param.searchType}" />
							</c:if>
							<c:if test="${not empty param.keyword}">
								<c:param name="keyword" value="${param.keyword}" />
							</c:if>
						</c:url> <a class="page-link" href="${prevLink}" aria-label="Previous">
							<span aria-hidden="true">«</span>
					</a></li>
				</c:if>
				<c:forEach var="i" begin="${pageMaker.startNum}"
					end="${pageMaker.endNum}">
					<li
						class="page-item ${not empty pageMaker and not empty pageMaker.pagination and pageMaker.pagination.pageNum == i ? 'active' : ''}">
						<c:url var="pageLink" value="/admin/members/list">
							<c:param name="page" value="${i}" />
							<c:param name="pageSize" value="${pageMaker.pagination.pageSize}" />
							<c:if test="${not empty param.searchType}">
								<c:param name="searchType" value="${param.searchType}" />
							</c:if>
							<c:if test="${not empty param.keyword}">
								<c:param name="keyword" value="${param.keyword}" />
							</c:if>
						</c:url> <a class="page-link" href="${pageLink}">${i}</a>
					</li>
				</c:forEach>
				<c:if
					test="${not empty pageMaker and not empty pageMaker.pagination and pageMaker.isNext()}">
					<li class="page-item"><c:url var="nextLink"
							value="/admin/members/list">
							<c:param name="page" value="${pageMaker.endNum + 1}" />
							<c:param name="pageSize" value="${pageMaker.pagination.pageSize}" />
							<c:if test="${not empty param.searchType}">
								<c:param name="searchType" value="${param.searchType}" />
							</c:if>
							<c:if test="${not empty param.keyword}">
								<c:param name="keyword" value="${param.keyword}" />
							</c:if>
						</c:url> <a class="page-link" href="${nextLink}" aria-label="Next"> <span
							aria-hidden="true">»</span>
					</a></li>
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
			<form id="deleteForm"
				action="${pageContext.request.contextPath}/admin/members/delete"
				method="post">
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" /> <input type="hidden" id="deleteMemberId"
					name="memberId">
				<div class="form-check">
					<input class="form-check-input" type="checkbox" value="true"
						id="permanent" name="permanent"> <label
						class="form-check-label" for="permanent"> 영구 삭제 </label>
				</div>
				<button type="submit" class="btn btn-danger">삭제</button>
				<button type="button" class="btn btn-secondary"
					onclick="closeDeleteModal()">취소</button>
			</form>
		</div>
	</div>

	<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
	<script>
   function path(url){
        return "${pageContext.request.contextPath}" + url;
    }

    function resetForm() {
        document.getElementById('searchType').value = '';
        document.getElementById('keyword').value = '';
        document.getElementById('searchForm').submit();
    }


    $(document).ready(function() {
        $('.update-status').change(function() {
            var memberId = $(this).data('member-id');
            var authStatus = $(this).val();

            var csrfToken = $("meta[name='_csrf']").attr("content");
            var csrfHeader = $("meta[name='_csrf_header']").attr("content");

            $.ajax({
                url: path('/admin/members/status/update'),
                type: 'POST',
                data: { memberId: memberId, authStatus: authStatus },
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function(response) {
                	 console.log('회원 상태 변경 성공', response);
                    alert('회원 상태 변경 성공');
                    window.location.reload();
                },
                error: function(xhr, status, error) {
                    console.error('회원 상태 변경 실패', error);
                    console.log('xhr:', xhr); // xhr 객체 로그 출력
                    console.log('status:', status); // status 로그 출력
                    alert('회원 상태 변경 실패: ' + error); // 오류 메시지 표시
                }
            });
        });
    });

    function openDeleteModal(memberId) {
        document.getElementById('deleteModal').style.display = "block";
        document.getElementById('deleteMemberId').value = memberId;
    }

    function closeDeleteModal() {
        document.getElementById('deleteModal').style.display = "none";
    }
   
   
    // 삭제 버튼 클릭 시 처리
    document.querySelectorAll('.btn-danger').forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault(); // 폼 기본 제출 막기
            const memberId = document.getElementById('deleteMemberId').value;
            const permanentDelete = document.getElementById('permanent').checked;
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

            if (confirm("정말로 삭제하시겠습니까?")) {
                fetch(path(`/admin/members/delete/${memberId}`), {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        [csrfHeader]: csrfToken,
                    },
                    body: `permanent=${permanentDelete}` // 영구 삭제 여부
                })
                .then(response => {
                    if (response.ok) {
                        alert('회원 삭제 완료');
                        window.location.reload(); // 페이지 새로고침
                    } else {
                        alert('회원 삭제 실패');
                    }
                })
                .catch(error => console.error('삭제 중 오류 발생:', error));
                closeDeleteModal();
            }
        });
    });

    // 모달 닫기 버튼 클릭 시 처리
    document.querySelector('.close').addEventListener('click', closeDeleteModal);

    // 모달 외부 클릭 시 닫기
    window.onclick = function(event) {
        const modal = document.getElementById('deleteModal');
        if (event.target == modal) {
            closeDeleteModal();
        }
    }
</script>
</body>
</html>