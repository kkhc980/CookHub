<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <!-- jquery 라이브러리 import -->
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <meta charset="UTF-8">
    <title>${NoticeBoardVO.noticeBoardTitle }</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f4f4f4;
            color: #333;
        }

        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }

        div {
            margin-bottom: 15px;
            background-color: #fff;
            padding: 15px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        p {
            margin: 5px 0;
            line-height: 1.6;
        }

        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            resize: vertical;
            font-family: Arial, sans-serif;
            font-size: 14px;
            line-height: 1.6;
            box-sizing: border-box;
            background-color: #f9f9f9;
            color: #333;
        }

        button {
            background-color: #007bff;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-right: 10px;
            font-size: 14px;
            transition: background-color 0.3s ease;
        }

        button:hover {
            background-color: #0056b3;
        }

        #deleteForm {
            display: inline;
        }
    </style>
</head>
<body>
    <h2>공지글 보기</h2>
    <div>
        <p><strong>글 번호:</strong> ${NoticeBoardVO.noticeBoardId }</p>
    </div>
    <div>
        <p><strong>제목:</strong></p>
        <p>${NoticeBoardVO.noticeBoardTitle }</p>
    </div>
    <div>
        <p><strong>작성자:</strong> ${NoticeBoardVO.memberId }</p>
        <!-- boardDateCreated 데이터 포멧 변경 -->
        <fmt:formatDate value="${NoticeBoardVO.noticeBoardCreatedDate }"
                    pattern="yyyy-MM-dd HH:mm:ss" var="noticeBoardCreatedDate"/>
        <p><strong>작성일:</strong> ${noticeBoardCreatedDate }</p>
    </div>
    <div>
        <textarea rows="20" cols="120" readonly>${NoticeBoardVO.noticeBoardContent }</textarea>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/noticeboard/list'">글 목록</button>
     <button onclick="location.href='${pageContext.request.contextPath}/noticeboard/modify/${NoticeBoardVO.noticeBoardId}'">글 수정</button>
    <form id="deleteForm" action="${pageContext.request.contextPath}/noticeboard/delete/${NoticeBoardVO.noticeBoardId}" method="POST">
    <sec:csrfInput/>
        <input type="hidden" name="noticeBoardId" value="${NoticeBoardVO.noticeBoardId }">
        <button type="submit" id="deleteNoticeBoard">글 삭제</button>
    </form>
    

    <script type="text/javascript">
        $(document).ready(function(){
            $('#deleteNoticeBoard').click(function(event){
                event.preventDefault(); // 기본 submit 동작 막기
                if(confirm('삭제하시겠습니까?')){
                    $('#deleteForm').submit(); // form 데이터 전송
                }
            });
        }); // end document
    </script>

</body>
</html>