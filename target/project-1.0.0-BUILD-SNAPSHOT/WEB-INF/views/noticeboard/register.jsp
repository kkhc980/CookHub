<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글 작성 페이지</title>
</head>
<body>
   <h2>글 작성 페이지</h2>
   <form action="register" method="POST">
   <!-- input 태그의 name은 boardVO의 멤버 변수 이름과 동일하게 작성 -->
      <div>
         <p>제목 : </p>
         <input type="text" name="noticeBoardTitle" 
         placeholder="제목 입력" maxlength="20" required>
      </div>
      <div>
         <p>작성자 : </p>
         <input type="text" name="memberId" maxlength="10" required>
      </div>
      <div>
         <p>내용 : </p>
         <textarea rows="20" cols="120" name="noticeBoardContent" 
         placeholder="내용 입력" maxlength="300" required></textarea>
      </div>
      <div>
         <input type="submit" value="등록">
      </div>
   </form>
</body>
</html>