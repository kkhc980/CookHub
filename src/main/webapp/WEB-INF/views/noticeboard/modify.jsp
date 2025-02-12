<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공지글 수정 페이지</title>
 <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
<style>
body {
    font-family: Arial, sans-serif;
    background-color: #f4f4f4;
    margin: 20px;
    color: #333;
}

h2 {
    text-align: center;
    margin-bottom: 20px;
    color: #333;
}

form {
    background-color: #fff;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

div {
    margin-bottom: 15px;
}

p {
    margin-bottom: 5px;
    font-weight: bold;
}

input[type="text"],
textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 14px;
    box-sizing: border-box;
}

textarea {
    resize: vertical;
    height: 150px;
    font-family: Arial, sans-serif; /* 텍스트 영역 폰트 설정 */
}

input[type="submit"] {
    background-color: #007bff;
    color: white;
    padding: 10px 15px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.3s ease;
}

input[type="submit"]:hover {
    background-color: #0056b3;
}

input[readonly] { /* 읽기 전용 입력 상자 스타일 */
    background-color: #f0f0f0;
    color: #777;
    cursor: not-allowed;
}
</style>
</head>
<body>
   <h2>공지글 수정 페이지</h2>
   <form action="${pageContext.request.contextPath}/noticeboard/modify/${noticeBoardVO.noticeBoardId}" method="POST">
   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <div>
         <p>번호 : </p>
         <input type="text" name="noticeBoardId" value="${noticeBoardVO.noticeBoardId }" readonly>
      </div>
      <div>
         <p>제목 : </p>
         <input type="text" name="noticeBoardTitle" placeholder="제목 입력" 
maxlength="20" value="${noticeBoardVO.noticeBoardTitle }" required>
      </div>
      <div>
         <p>작성자 : ${noticeBoardVO.memberId} </p>
         
      </div>
      <div>
         <p>내용 : </p>
         <textarea rows="20" cols="120" name="noticeBoardContent" placeholder="내용 입력" 
maxlength="300" required>${noticeBoardVO.noticeBoardContent }</textarea>
      </div>
      <div>
         <input type="submit" value="등록">
      </div>
   </form>
</body>
</html>