<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${noticeBoardVO.noticeBoardTitle }</title>
</head>
<body>
	<h2>공지글 수정 페이지</h2>
	<form action="modify" method="POST">
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