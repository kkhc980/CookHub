<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <script type="text/javascript">
        let isEmailChecked = false; // 이메일 중복 확인 여부를 저장하는 변수

        $(document).ajaxSend(function (e, xhr, opt) {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");

            xhr.setRequestHeader(header, token);
        });

        function validate() {
            //전송 보내기전 (submit 버튼 클릭시) 입력값들이 유효한지 검사

            //암호와 암호 확인이 일치하는지 체크
            var pwd1 = document.getElementById("upwd1").value;
            var pwd2 = $('#upwd2').val();

            if (pwd1 !== pwd2) {
                alert("암호와 암호 확인이 일치하지 않습니다.\n" + "다시 입력하세요.");
                document.getElementById("upwd1").select();
                return false; //전송 안 함
            }

            // 이메일 중복 확인을 했는지 체크
            if (!isEmailChecked) {
                alert("이메일 중복 확인을 해주세요.");
                $('#email').focus();
                return false; //전송 안 함
            }

            return true; //전송함
        }

        //아이디 중복을 확인하기 위한 ajax 요청 처리용 함수
        function dupCheckId() {
            var email = $('#email').val().trim();
            if (email === '') {
                alert('이메일을 입력해주세요.');
                $('#email').focus();
                return false;
            }
            $.ajax({
                url: "../member/emailCheck",
                type: "post",
                data: {email: email},
                success: function (data) {
                    console.log("success : " + data);
                    if (data == 'ok') {
                        alert("사용 가능한 이메일입니다.");
                        $('#upwd1').focus();
                        isEmailChecked = true; // 중복 확인 완료
                    } else if (data == 'registered') {
                        alert("이미 가입된 이메일입니다.");
                        $('#email').select();
                        isEmailChecked = false; // 중복 확인 실패
                    } else if (data == 'pending') {
                        alert("이메일 인증 진행 중인 아이디입니다.");
                        $('#email').select();
                        isEmailChecked = false; // 중복 확인 실패
                    } else {
                        alert("오류가 발생했습니다. 다시 시도해주세요.");
                        isEmailChecked = false;
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("error : " + jqXHR.responseText + ", " + textStatus + ", " + errorThrown);
                    isEmailChecked = false; // 에러 발생 시 중복 확인 실패 처리
                }
            });
        }

        // 이메일 필드에 이벤트 리스너 추가 (이메일 변경 시 중복 확인 초기화)
        $('#email').on('input', function () {
            isEmailChecked = false; // 이메일 변경 시 중복 확인 초기화
        });
    </script>
    <style>
        body {
            font-family: sans-serif;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            margin: 0;
            background-color: #f4f4f4;
        }

        .container {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .signup-container {
            width: 400px;
            padding: 30px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .signup-container h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #333;
        }

        .signup-form { /* 폼 전체에 flexbox 레이아웃 적용 */
            display: flex;
            flex-direction: column;
        }

        .form-group { /* 각 입력 그룹에 flexbox 레이아웃 적용 */
            display: flex;
            flex-direction: column;
            margin-bottom: 10px;
        }

        .form-group label {
            font-weight: bold;
            margin-bottom: 5px;
        }

        .signup-container input[type="text"],
        .signup-container input[type="password"],
        .signup-container input[type="tel"] {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-sizing: border-box;
        }

        .email-group { /* 이메일 입력 그룹에 flexbox 레이아웃 적용 */
            display: flex;
        }

        .email-group input[type="text"] {
            flex: 1; /* 이메일 입력 필드가 남은 공간을 채우도록 설정 */
            margin-right: 10px;
        }

        .signup-container input[type="button"],
        .signup-container input[type="submit"] {
            padding: 10px;
            border: none;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 10px;
        }

        .signup-container input[type="button"]:hover,
        .signup-container input[type="submit"]:hover {
            background-color: #0056b3;
        }

        .signup-container input[type="button"] {
            background-color: #e0e0e0;
            color: #333;
            white-space: nowrap; /* 버튼 내용이 줄바꿈되지 않도록 설정 */
        }

        .signup-container input[type="button"]:hover {
            background-color: #bdbdbd;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="signup-container">
        <h2>회원가입</h2>
        <form class="signup-form" action="../member/signup" method="post" onsubmit="return validate();">
            <div class="form-group">
                <label for="name">이 름</label>
                <input type="text" name="name" id="name" required>
            </div>
            <div class="form-group email-group">
                <label for="email">이메일</label>
                <input type="text" name="email" id="email" required>
                <input type="button" value="중복확인" onclick="dupCheckId();">
            </div>
            <div class="form-group">
                <label for="upwd1">비밀번호</label>
                <input type="password" name="password" id="upwd1" required>
            </div>
            <div class="form-group">
                <label for="upwd2">비밀번호 확인</label>
                <input type="password" id="upwd2" required>
            </div>
            <div class="form-group">
                <label for="phone">전화번호</label>
                <input type="tel" name="phone" id="phone">
            </div>
            <input type="submit" value="가입하기">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        </form>
    </div>
</div>
</body>
</html>