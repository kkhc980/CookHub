<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<!-- jquery 라이브러리 import -->
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<meta charset="UTF-8">
<title>${NoticeBoardVO.noticeBoardTitle }</title>
</head>
<body>
    <h2>공지글 보기</h2>
    <div>
        <p>글 번호 : ${NoticeBoardVO.noticeBoardId }</p>
    </div>
    <div>
        <p>제목 : </p>
        <p>${NoticeBoardVO.noticeBoardTitle }</p>
    </div>
    <div>
        <p>작성자 : ${NoticeBoardVO.memberId }</p>
        <!-- boardDateCreated 데이터 포멧 변경 -->
        <fmt:formatDate value="${NoticeBoardVO.noticeBoardCreatedDate }"
                    pattern="yyyy-MM-dd HH:mm:ss" var="noticeBoardCreatedDate"/>
        <p>작성일 : ${noticeBoardCreatedDate }</p>
    </div>
    <div>
        <textarea rows="20" cols="120" readonly>${NoticeBoardVO.noticeBoardContent }</textarea>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/noticeboard/list'">글 목록</button>
    <button onclick="location.href='${pageContext.request.contextPath}/noticeboard/modify/${noticeBoard.noticeBoardId}'">글 수정</button>
    <form id="deleteForm" action="${pageContext.request.contextPath}/noticeboard/delete" method="POST">
        <input type="hidden" name="noticeBoardId" value="${noticeBoard.noticeBoardId }">
        <button type="submit" id="deleteNoticeBoard">글 삭제</button>
    </form>
    

    <script type="text/javascript">
        $(document).ready(function(){
            $('#deleteNoticeBoard').click(function(){
                if(confirm('삭제하시겠습니까?')){
                    $('#deleteForm').submit(); // form 데이터 전송
                }
            });
        }); // end document
    </script>

</body>
</html>