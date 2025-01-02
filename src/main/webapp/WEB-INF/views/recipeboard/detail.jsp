<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<!-- jquery 라이브러리 import -->
<base href="${pageContext.request.contextPath}/">
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<meta charset="UTF-8">
<title>${recipeBoard.recipeBoardTitle }</title>
</head>
<body>
    <h2>글 보기</h2>
   
    <div>
        <p>제목 : </p>
        <p>${recipeBoard.recipeBoardTitle }</p>
    </div>
    <div>
        <p>작성자 : ${recipeBoard.memberId }</p>
        <!-- boardDateCreated 데이터 포멧 변경 -->
        <fmt:formatDate value="${recipeBoard.recipeBoardCreatedDate }"
                    pattern="yyyy-MM-dd HH:mm:ss" var="recipeBoardCreatedDate"/>
        <p>작성일 : ${recipeBoardCreatedDate }</p>
    </div>
    <div>
        <textarea rows="20" cols="120" readonly>${recipeBoard.recipeBoardContent }</textarea>
    </div>
    
     <div>
        <p>타입 : ${typeName}</p>
    </div>
    <div>
        <p>방법 : ${methodName}</p>
    </div>
     <div>
        <p>상황 : ${situationName}</p>
     </div>
    <div>
       <p>재료 :</p>
       <ul>
         <c:forEach var="ingredient" items="${ingredients}">
          <li>${ingredient.ingredientName}</li>
        </c:forEach>
      </ul>
  </div>
 <div>
    <p>첨부 이미지 :</p>
        <c:forEach var="attach" items="${attachList}">
            <img src="${pageContext.request.contextPath}${attach.recipeBoardPath}" alt="첨부 이미지" style="max-width: 300px;"><br>
        </c:forEach>
   </div>
    <button onclick="location.href='recipeboard/list'">글 목록</button>
    <button onclick="location.href='recipeboard/update/${recipeBoard.recipeBoardId}'">글 수정</button>
    <button type="button" id="deleteBoard">글 삭제</button>
    <form id="deleteForm" action="recipeboard/delete/${recipeBoard.recipeBoardId}" method="POST">
        <input type="hidden" name="recipeBoardId" value="${recipeBoard.recipeBoardId}">
    </form>
   <div style="text-align: center;">
      <input type="text" id="memberId"> <input type="text"
         id="replyContent">
      <button id="btnAdd">작성</button>
   </div>

    <script type="text/javascript">
        $(document).ready(function(){
            $('#deleteBoard').click(function(){
                if(confirm('삭제하시겠습니까?')){
                    $('#deleteForm').submit(); // form 데이터 전송
                }
            });
        }); // end document
    </script>
   
   

   <hr>
   <div style="text-align: center;">
      <div id="replies"></div>
   </div>

	<input type="hidden" id="recipeBoardId" value="${recipeBoard.recipeBoardId }">
	
		
   <script type="text/javascript">
      $(document)
            .ready(
                  function() {
                     getAllReply(); // reply 함수 호출
                  
                     
                 
                     $('#btnAdd').click(function() {
                        var recipeBoardId = $('#recipeBoardId').val(); // 게시판 번호 데이터
                        var memberId = $('#memberId').val(); // 작성자 데이터
                        var replyContent = $('#replyContent').val(); // 댓글 내용
                        // JS객체 생성
                        var obj = {
                           'recipeBoardId' : recipeBoardId, // 게시글 ID 전달
                           'memberId' : memberId,
                           'replyContent' : replyContent
                        }
                        console.log(obj);

                        // $.ajax로 송수신
                        $.ajax({
                           type : 'POST', // 메서드 타입
                           url : '/project/recipeboard/replies/detail', // url
                           headers : {// 헤더 정보
                              'Content-Type' : 'application/json' // json content-type 설정   
                           },
                           data : JSON.stringify(obj), // JSON으로 변환
                           success : function(result) { // 전송 성공 시 서버에서 result값 전송
                              console.log(result);
                              if (result == 1) {
                                 alert('댓글 입력 성공');
                                 getAllReply(); // 함수 호출
                              } else {
                                 alert('댓글 입력 실패');
                              }
                           }
                        });
                     }); // end btn Add.click()

                     // 게시판 댓글 전체 가져오기
                     function getAllReply() {
                        var recipeBoardId = $('#recipeBoardId').val();

                        var url = '/project/recipeboard/all/'
                              + recipeBoardId;

                        $
                              .getJSON(
                                    url,
                                    function(data) {
                                       // data : 서버에서 전송 받은 list 데이터가 저장되어 있음.
                                       // getJSON()에서 json 데이터는
                                       // javascript object로 자동 parsing됨.
                                       console.log(data);

                                       var list = ''; // 댓글 데이터를 HTML에 표현할 문제열 변수

                                       // $(컬렉션).each() : 컬렉션 데이터를 반복문으로 꺼내는 함수
                                       $(data)
                                             .each(
                                                   function() {
                                                      // this : 컬렉션의 각 인덱스 데이터를 의미
                                                      console
                                                            .log(this);

                                                      // 전송된 replyDateCreated는 문자열 형태이므로 날짜 형태로 변환이 필요
                                                      var replyDateCreated = new Date(
                                                            this.replyDateCreated)

                                                      list += '<div class="reply_item">'
                                                            + '<pre>'
                                                            + '<input type="hidden" id="replyId" value="'+ this.replyId +'">'
                                                            + this.memberId
                                                            + '&nbsp;&nbsp;' // 공백
                                                            + '<input type="text" id="replyContent" value="'+ this.replyContent +'">'
                                                            + '&nbsp;&nbsp;'
                                                            + replyDateCreated
                                                            + '&nbsp;&nbsp;'
                                                            + '<button class="btn_update" >수정</button>'
                                                            + '<button class="btn_delete" >삭제</button>'
                                                            + '</pre>'
                                                            + '</div>';
                                                   }); // end each()

                                       $('#replies').html(list); // 저장된 데이터를 replies div 표현   
                                    } // end function()
                              ); // end getJSON()
                     } // end getAllReply()
                     
                     
                     // 수정 버튼을 클릭하면 선택된 댓글 수정
                     $('#replies')
                           .on(
                                 'click',
                                 '.reply_item .btn_update',
                                 function() {
                                    console.log(this);

                                    // 선택된 댓글의 replyId, replyContent 값을 저장
                                    // prevAll() : 선택된 노드 이전에 있는 모든 형제 노드를 접근
                                    var replyId = $(this).prevAll(
                                          '#replyId').val();
                                    var replyContent = $(this)
                                          .prevAll(
                                                '#replyContent')
                                          .val();
                                    console.log("선택된 댓글 번호 : "
                                          + replyId
                                          + ", 댓글 내용 : "
                                          + replyContent);

                                    // ajax 요청
                                    $
                                          .ajax({
                                             type : 'PUT',
                                             url : '/project/recipeboard/replies/'
                                                   + replyId,
                                             headers : {
                                                'Content-Type' : 'application/json'
                                             },
                                             data : replyContent,
                                             success : function(
                                                   result) {
                                                console
                                                      .log(result);
                                                if (result == 1) {
                                                   alert('댓글 수정 성공!');
                                                   getAllReply();
                                                }
                                             }
                                          });

                                 }); // end replies.on()   

                     // 삭제 버튼을 클릭하면 선택된 댓글 삭제
                     $('#replies')
                           .on(
                                 'click',
                                 '.reply_item .btn_delete',
                                 function() {
                                    console.log(this);
                                    var recipeBoardId = $(
                                          "#recipeBoardId").val(); // 게시판 번호 데이터
                                    var replyId = $(this).prevAll(
                                          '#replyId').val(); // 댓글 번호 데이터

                                    $
                                          .ajax({
                                             type : 'DELETE',
                                             url : '/project/recipeboard/replies/'
                                                   + replyId
                                                   + '/'
                                                   + recipeBoardId,
                                             headers : {
                                                'content-Type' : 'application/json'
                                             },
                                             success : function(
                                                   result) {
                                                console
                                                      .log(result);
                                                if (result == 1) {
                                                   alert('댓글 삭제 성공!');
                                                   getAllReply();
                                                }
                                             }
                                          });
                                 }); // end replies.on                               
                            
                  }); // end document()
                  
                                   
   </script>
   
  
</body>
</html>