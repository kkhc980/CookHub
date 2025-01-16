<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
</head>
<body>
<sec:authentication property="principal" var="user"/>
<form id="updateForm" action="update" method="POST">
    <div class="update-content">
        <div class="update-group">
            <div class="join-row">
                <h3 class="join-title">
                    <label for="email">이메일</label>
                </h3>
                <span>
          	        ${memberDTO.email }
                    <input id="email" type="hidden" name="email" value="${memberDTO.email }"> <br>
                </span>
            </div>
            <div class="update-row">
                <h3 class="update-title">
                    <label for="password">비밀번호</label>
                </h3>
                <span>
                    <input id="password" type="password" name="password" title="비밀번호" maxlength="16"> <br>
			    </span>
                <span id="pwMsg"></span>

                <h3 class="update-title">
                    <label for="pwConfirm">비밀번호 재확인</label>
                </h3>
                <span>
                    <input id="pwConfirm" type="password" title="비밀번호 확인" maxlength="16"> <br>
			    </span>
                <span id="pwConfirmMsg"></span>

                <h3 class="update-title">
                    <label for="userName">이름</label>
                </h3>
                <span>
                    <input id="userName" type="text" name="name" title="이름" maxlength="10"
                           value="${memberDTO.name }">	<br>
                </span>
                <span id="nameMsg"></span>

                <h3 class="update-title">
                    <label for="phone">전화번호</label>
                </h3>
                <span>
                    <input id="phone" type="text" name="phone" title="전화번호" value="${memberDTO.phone }"> <br>
                </span>
                <span id="phoneMsg"></span>
            </div>
        </div>
        <input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
    </div>
</form>
<br>
<button id="btnModify">제출</button>
<script type="text/javascript">
    $(document).ready(function () {
        var pwFlag = false; // memberPw 유효성 변수
        var pwConfirmFlag = false; // pwConfirm 유효성 변수
        var nameFlag = false; // memberName 유효성 변수
        var phoneFlag = true;

        // 비밀번호 유효성 검사
        $('#password').blur(function () {
            var memberPw = $('#password')
                .val();
            var pwRegExp = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$^&*-]).{8,16}$/;
            // 8 ~ 16 사이의 대문자, 소문자, 숫자, 특수문자를 1개 이상 포함하는 정규 표현식

            if (memberPw === '') { // 비밀번호가 입력되지 않은 경우
                $('#pwMsg').html('필수 입력입니다.');
                $('#pwMsg').css('color', 'red');
                pwFlag = false; // 유효성 false
                return;
            }

            if (!pwRegExp.test(memberPw)) { // 입력한 비밀번호와 정규표현식이 일치하지 않는 경우
                pwFlag = false; // 유효성 false
                $('#pwMsg').html('8~16자 영문 대/소문자, 숫자, 특수문자를 모두 포함하세요');
                $('#pwMsg').css('color', 'red');
            } else {
                $('#pwMsg').html('가능한 비밀번호입니다.');
                $('#pwMsg').css('color', 'green');
                pwFlag = true; // 유효성 true
            }
        }); // end memberPw.blur()

        // 비밀번호 확인 유효성 검사
        $('#pwConfirm').blur(function () {
            var memberPw = $('#password').val();
            var pwConfirm = $('#pwConfirm').val();

            // 비밀번호 확인을 입력하지 않은 경우
            if (pwConfirm === '') {
                $('#pwConfirmMsg').html('필수 입력입니다.');
                $('#pwConfirmMsg').css('color', 'red');
                pwConfirmFlag = false; // 유효성 false

                return;
            }

            // 입력한 비밀번호와 비밀번호 확인이 일치하는 경우
            if (memberPw === pwConfirm) {
                $('#pwConfirmMsg').html('비밀번호가 일치합니다.');
                $('#pwConfirmMsg').css('color', 'green');
                pwConfirmFlag = true; // 유효성 true

            } else {
                $('#pwConfirmMsg').html('비밀번호가 일치하지 않습니다.');
                $('#pwConfirmMsg').css('color', 'red');
                pwConfirmFlag = false; // 유효성 false
            }
        });

        // 이름 유효성 검사
        $('#userName').blur(function () {
            var memberName = $('#userName').val(); // 입력한 데이터 값

            if (memberName.trim() === '') { // 이름이 입력되지 않았을 경우
                $('#nameMsg').html('필수 입력입니다.');
                $('#nameMsg').css('color', 'red');
                nameFlag = false; // 유효성 false
                return;
            } else {
                $('#nameMsg').html('');
                nameFlag = true; // 유효성 true
            }
        });

        // 전화번호 유효성 검사 (input 이벤트로 변경)
        $('#phone').on('input', function () {
            var phoneNumber = $('#phone').val(); // 입력한 데이터 값
            var phoneRegex = /^010-\d{4}-\d{4}$/;

            if (!phoneRegex.test(phoneNumber) && phoneNumber.length > 0) {
                $('#phoneMsg').html('잘못된 전화번호 형식입니다.');
                $('#phoneMsg').css('color', 'red');
                phoneFlag = false;
            }
            else if(phoneNumber.length === 0){
                $('#phoneMsg').html('필수 입력입니다.');
                $('#phoneMsg').css('color', 'red');
                phoneFlag = false;
            }
            else{
                $('#phoneMsg').html('');
                phoneFlag = true;
            }
        });

        // 회원 정보 form 데이터 전송
        $('#btnModify').click(function () {
            console.log('pwFlag : ' + pwFlag);
            console.log('pwConfirmFlag : ' + pwConfirmFlag);
            console.log('nameFlag : ' + nameFlag);
            console.log('phoneFlag : ' + phoneFlag);

            if (pwFlag && pwConfirmFlag && nameFlag && phoneFlag) { // 입력된 데이터가 모두 유효한 경우
                $('#updateForm').submit(); // form 전송 실행
            }
        });
    });
</script>
</body>
</html>