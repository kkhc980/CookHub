<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원 상세 정보</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
      <style>
        .modal {
            display: none; /* Hidden by default */
            position: fixed; /* Stay in place */
            z-index: 1; /* Sit on top */
            left: 0;
            top: 0;
            width: 100%; /* Full width */
            height: 100%; /* Full height */
            overflow: auto; /* Enable scroll if needed */
            background-color: rgb(0,0,0); /* Fallback color */
            background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
        }

        /* Modal Content/Box */
        .modal-content {
            background-color: #fefefe;
            margin: 15% auto; /* 15% from the top and centered */
            padding: 20px;
            border: 1px solid #888;
            width: 80%; /* Could be more or less, depending on screen size */
        }

        /* The Close Button */
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
    <h2>회원 상세 정보</h2>
    <div class="card">
        <div class="card-body">
            <h5 class="card-title">기본 정보</h5>
            <p><strong>회원 ID:</strong> ${member.memberId}</p>
            <p><strong>이메일:</strong> ${member.email}</p>
            <p><strong>이름:</strong> ${member.name}</p>
            <p><strong>전화번호:</strong> ${member.phone}</p>
            <p><strong>가입일:</strong> <fmt:formatDate value="${member.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
            <p><strong>수정일:</strong>  <fmt:formatDate value="${member.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
              <p>
              <strong>상태 :</strong>
                 <c:choose>
                   <c:when test="${member.authStatus == 1}">
                         활성
                     </c:when>
                       <c:otherwise>
                            비활성
                         </c:otherwise>
                      </c:choose>
                </p>
            </div>
            </div>
          <div class="card mt-3">
            <div class="card-body">
                <h5 class="card-title">권한 정보</h5>
                    <ul class="list-group">
                          <c:forEach items="${member.roles}" var="role">
                             <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span >${role}</span>
                                <button class="btn btn-sm btn-danger" onclick="openRemoveRoleModal('${member.memberId}', '${role}')">삭제</button>
                             </li>
                           </c:forEach>
                   </ul>
                  <button class="btn btn-primary mt-3" onclick="openAddRoleModal(${member.memberId})" >권한 추가</button>
                  <a href="/admin/members/list" class="btn btn-secondary ms-2 mt-3">목록으로</a>
              </div>
        </div>
</div>
    
<!-- 권한 추가 모달 -->
  <div id="addRoleModal" class="modal">
       <div class="modal-content">
           <span class="close" onclick="closeAddRoleModal()">×</span>
            <h3>회원 권한 추가</h3>
           <form id="addRoleForm">
                <input type="hidden" id="addRoleId">
                  <select id="roleNameAdd" class="form-select mb-2"></select>
             <button type="button" class="btn btn-primary" onclick="addRole()">추가</button>
             <button type="button" class="btn btn-secondary" onclick="closeAddRoleModal()">취소</button>
           </form>
        </div>
   </div>
<!-- 권한 제거 모달 -->
   <div id="removeRoleModal" class="modal">
       <div class="modal-content">
        <span class="close" onclick="closeRemoveRoleModal()">×</span>
           <h3>회원 권한 삭제</h3>
           <p>해당 권한을 삭제하시겠습니까?</p>
             <form id="removeRoleForm">
                 <input type="hidden" id="removeRoleId">
                 <input type="hidden" id="removeRoleName">
                 <button type="button" class="btn btn-danger" onclick="removeRole()">삭제</button>
                <button type="button" class="btn btn-secondary" onclick="closeRemoveRoleModal()">취소</button>
          </form>
      </div>
    </div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"></script>
  <script>
        function openAddRoleModal(memberId) {
              const addRoleModal = document.getElementById('addRoleModal');
              const addRoleIdInput = document.getElementById('addRoleId');
                 const roleNameAddSelect = document.getElementById('roleNameAdd');
          addRoleIdInput.value = memberId;
              fetch('/admin/members/roles')
            .then(response => response.json())
           .then(roles => {
              roleNameAddSelect.innerHTML = '';
                  roles.forEach(role =>{
                        const option = document.createElement('option');
                          option.value = role;
                          option.textContent = role;
                       roleNameAddSelect.appendChild(option);
              })
              addRoleModal.style.display ="block";
           });
      }
     function closeAddRoleModal(){
          document.getElementById('addRoleModal').style.display="none";
      }
      function addRole() {
        const memberId = document.getElementById('addRoleId').value;
        const roleName = document.getElementById('roleNameAdd').value;
          fetch('/admin/members/role/add', {
              method:'POST',
             headers: {
              'Content-Type':'application/x-www-form-urlencoded'
            },
             body :new URLSearchParams({
            memberId: memberId,
             roleName: roleName
            })
        })
          .then(response => {
               if(response.ok){
                 alert('권한 추가 완료');
              window.location.reload();
              }else{
              alert('권한 추가 실패');
          }
         })
     .catch(error =>{
              console.error("Error :",error)
        })
      closeAddRoleModal();
        }

       function openRemoveRoleModal(memberId,roleName) {
           document.getElementById('removeRoleModal').style.display = "block";
            document.getElementById('removeRoleId').value=memberId;
         document.getElementById('removeRoleName').value=roleName;
         }

    function closeRemoveRoleModal() {
         document.getElementById('removeRoleModal').style.display = "none";
        }

     function removeRole(){
        const memberId = document.getElementById('removeRoleId').value;
           const roleName =  document.getElementById('removeRoleName').value;
      fetch('/admin/members/role/remove',{
          method:'POST',
        headers : {
           'Content-Type':'application/x-www-form-urlencoded'
           },
         body : new URLSearchParams({
             memberId : memberId,
              roleName : roleName
        })
       })
    .then(response =>{
          if(response.ok){
              alert('권한 삭제 완료');
            window.location.reload();
            }else {
           alert('권한 삭제 실패');
        }
       })
    .catch(error =>{
           console.error("Error:", error);
      })
      closeRemoveRoleModal();
    }

 </script>
</body>
</html>