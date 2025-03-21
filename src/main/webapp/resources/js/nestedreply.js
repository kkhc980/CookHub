// nestedreply.js

$(document).ready(function() {

    // 대댓글 작성 모달 창 열기
    $(document).on('click', '.btn_reply', function() {
     
        openNestedReplyModal(this);
         
    });

    // 대댓글 제출 이벤트
    $(document).on('click', '#submitNestedReply', function() {
        // submitNestedReply();
	var nestedReplyContent = $(this).siblings('#nestedReplyContent').val();
    var replyId = $(this).siblings('#parentReplyId').val();
    var nestedReplyModal = document.getElementById("nestedReplyModal");

    // Get memberId from hidden input
    var memberId = $(this).siblings('#nestedReplyMemberId').val();
	
	console.log("🚀 [Debug] replyId:", replyId);  // 확인용 로그
    console.log("🚀 [Debug] nestedReplyContent:", nestedReplyContent);
    console.log("🚀 [Debug] memberId:", memberId);

    if (!replyId || !nestedReplyContent || !memberId) {
        alert("댓글 ID 또는 내용이 없습니다.");
        return;
    }
	
	
    // AJAX 요청을 통해 답글을 서버에 저장
    $.ajax({
        type: 'POST',
        url: '/project/recipeboard/nestedreplies/' + replyId,
        contentType: 'application/json',
        data: JSON.stringify({
        	replyId: replyId,
            nestedReplyContent: nestedReplyContent,
            memberId: memberId
        }),
        success: function(response) {
            alert("답글이 작성되었습니다.");
            nestedReplyModal.style.display = "none";
            window.getAllReply(); // ✅ 전역 함수로 호출
        },
        error: function(xhr, status, error) {
            console.error("에러 발생:", error);
            alert("답글 작성에 실패했습니다.");
        }
    });
        
    });
    
    $(document).on('click', '#nestedReplyModal .nested-close', function() {
        console.log("✅ 모달 닫기 버튼 클릭됨!"); // ✅ 디버깅용 로그 출력

        var nestedReplyModal = $('#nestedReplyModal'); // ✅ 모달 ID를 사용하여 선택
        console.log("🔹 닫힐 모달 요소:", nestedReplyModal);

        if (nestedReplyModal.length === 0) {
            alert("❌ 닫을 모달을 찾을 수 없습니다.");
            return;
        }

        nestedReplyModal.hide(); // ✅ 모달 닫기
    });$(document).on('click', '#nestedReplyModal .nested-close', function() {
        console.log("✅ 모달 닫기 버튼 클릭됨!"); // ✅ 디버깅용 로그 출력

        var nestedReplyModal = $('#nestedReplyModal'); // ✅ 모달 ID를 사용하여 선택
        console.log("🔹 닫힐 모달 요소:", nestedReplyModal);

        if (nestedReplyModal.length === 0) {
            alert("❌ 닫을 모달을 찾을 수 없습니다.");
            return;
        }

        nestedReplyModal.hide(); // ✅ 모달 닫기
    });
});

 // 대댓글 삭제 버튼 클릭 이벤트
    $(document).on('click', '.btn_delete_nested_reply', function() {
        var nestedReplyId = $(this).data('nested-reply-id');
        if (confirm("정말로 대댓글을 삭제하시겠습니까?")) {
            deleteNestedReply(nestedReplyId);
        }
    });

// 대댓글 삭제
function deleteNestedReply(nestedReplyId) {
    // Get memberId from hidden input
    var memberId = $('#nestedReplyMemberId').val();
    var replyId = $('.btn_reply').data('reply-id');

    $.ajax({
        type: 'DELETE',
        url: '/project/recipeboard/nestedreplies/' + nestedReplyId + '/' + replyId,
        contentType: 'application/json',
        success: function (response) {
            alert("대댓글이 삭제되었습니다.");
            window.getAllReply(); // ✅ 전역 함수로 호출
        },
        error: function (xhr, status, error) {
            console.error("대댓글 삭제 실패:", error);
            alert("대댓글 삭제에 실패했습니다.");
        }
    });
}

// 대댓글 HTML
function getAllNestedReply(replyId) {
    var url = '/project/recipeboard/nestedreplies/all/' + replyId; // 대댓글 목록을 가져오는 URL

    $.getJSON(url, function(data) {
        console.log("Nested Replies for replyId " + replyId + ":", data);

        var nestedReplyList = '';

        $(data).each(function() {
            var nestedReplyDateCreated = new Date(this.nestedReplyDateCreated);
            
             var nestedReplyformattedDate = nestedReplyDateCreated.toLocaleString("ko-KR", { 
	                   		    year: "numeric", 
	                   		    month: "2-digit", 
	                   		    day: "2-digit", 
	                   		 	hourCycle: "h23",  // ✅ 24시간 형식 강제 적용
	                   		    hour: "2-digit", 
	                   		    minute: "2-digit", 
	                   		    second: "2-digit" 
	                   		})
							.replace(/\. /g, '-')  // "2025. 03. 18. 10:36:48" → "2025-03-18-10:36:48"
							.replace(/-(\d{2}):/, ' $1:');  // ✅ 날짜와 시간 사이의 `-`을 공백으로 변경
            

            nestedReplyList += '<div class="nested_reply_item" data-nested-reply-id="' + this.nestedReplyId + '">' +
                '<pre>' +
                '<input type="hidden" class="nestedReplyId" value="' + this.nestedReplyId + '">' +
                this.memberId +
                '  ' +
                '<span class="nestedReplyContentDisplay">' + this.nestedReplyContent + '</span>' +
                '  ' +
                nestedReplyformattedDate +
                '  ' +
                '<div class="nested_reply_buttons" data-nested-reply-id="' + this.nestedReplyId + '">' +
                '<button class="btn_update_nested_reply" data-nested-reply-id="' + this.nestedReplyId + '">수정</button>' +
                '<button class="btn_delete_nested_reply" data-nested-reply-id="' + this.nestedReplyId + '">삭제</button>' +
                '</div>' + // 닫는 div 추가
                '</pre>' +
                '</div>';
        });

        $('#nested_replies_' + replyId).html(nestedReplyList); // 해당 댓글 아래에 대댓글 목록 표시
        
        // 대댓글 수정 버튼 클릭 이벤트
	$('#nested_replies_' + replyId).on('click', '.btn_update_nested_reply', function() {
		var nestedReplyId = $(this).data('nested-reply-id'); // data 속성에서 nestedReplyId 가져오기
		var nestedReplyItem = $('.nested_reply_item[data-nested-reply-id="' + nestedReplyId + '"]'); //해당 대댓글 요소
		var nestedReplyContentSpan = nestedReplyItem.find('.nestedReplyContentDisplay');
		var currentNestedReplyContent = nestedReplyContentSpan.text().trim(); // 기존 텍스트 내용 가져오기
		
		// span 요소를 text input으로 변경
		nestedReplyContentSpan.replaceWith('<input type="text" class="nestedReplyContentInput" data-nested-reply-id="' + nestedReplyId + '" value="' + currentNestedReplyContent + '">');
		
		// 수정 완료 버튼으로 변경
		$(this).replaceWith('<button class="btn_update_done" data-nested-reply-id="' + nestedReplyId + '">수정 완료</button>');
		
		// 수정완료 버튼 클릭 이벤트
		$('#nested_replies_' + replyId).off('click', '.btn_update_done').on('click', '.btn_update_done', function() {
		
		var nestedReplyId = $(this).data('nested-reply-id'); // data 속성에서 nestedReplyId 가져오기
		var nestedReplyItem = $('.nested_reply_item[data-nested-reply-id="' + nestedReplyId + '"]'); //해당 대댓글 요소
		var nestedReplyContentInput = nestedReplyItem.find('.nestedReplyContentInput');
		var updatedNestedReplyContent = nestedReplyContentInput.val().trim();
		
			console.log("nestedReplyId : " + nestedReplyId + ", 수정할 내용 : " + updatedNestedReplyContent);
			
			$.ajax({
				type: 'PUT',
				url: '/project/recipeboard/nestedreplies/' + nestedReplyId,
	        	contentType: 'application/json',
	        	data: JSON.stringify({
	        	
	            nestedReplyContent: updatedNestedReplyContent
	            
	        }),
	        success: function (response) {
	        
	        	$('.nestedReplyContentDisplay[data-nested-reply-id="' + nestedReplyId + '"]').text(response.nestedReplyContent); // 수정된 내용만 삽입
	        	
	            if (response == 1) {
	            	alert('답글 수정 성공!');
	            	
	            // 기존 input을 다시 span으로 변경하여 수정 내요 반영
	            nestedReplyContentInput.replaceWith('<span class="nestedReplyContentDisplay">' + updatedNestedReplyContent + '</span>');
	            
	            // "수정 완료" 버튼을 다시 "수정" 버튼으로 변경
	            nestedReplyItem.find('.btn_update_done').replaceWith('<button class="btn_update_nested_reply" data-nested-reply-id="' + nestedReplyId + '">수정</button>');
	            
	            }
	        },
	        error: function (xhr, status, error) {
	            console.error("답글 수정 실패:", error);
	            alert("답글 수정에 실패했습니다.");
	        }
	       }); 
	        
			});
		});

    });
}




// 모달 열기
function openNestedReplyModal(element) {
    var replyId = $(element).data('reply-id');
	let nestedReplyModal = document.getElementById("nestedReplyModal");
	
	    if (!nestedReplyModal) {
	        console.error("nestedReplyModal element with ID 'nestedReplyModal' not found.");
	        return;
	    }
	    
   // var nestedReplyModal = document.getElementById("nestedReplyModal");

    // Set the parent reply ID
    $("#parentReplyId").val(replyId);

    // Display the nestedReplyModal
    nestedReplyModal.style.display = "block";

    // Close button functionality
    var closeBtn = document.getElementsByClassName("nested-close")[0];
    closeBtn.onclick = function() {
      let nestedReplyModal = document.getElementById("nestedReplyModal");
      if (nestedReplyModal) {
            nestedReplyModal.style.display = "none";
       } else {
           console.log("nestedReplyModal element not found!");
       }
    }

    // 외부 클릭시 모달 닫힘
    window.onclick = function(event) {
        if (event.target == nestedReplyModal) {
            nestedReplyModal.style.display = "none";
        }
    }
}

// 모달 제출
function submitNestedReply() {
    var nestedReplyContent = $('#nestedReplyContent').val();
    var replyId = $('#parentReplyId').val();
    var nestedReplyModal = document.getElementById("nestedReplyModal");

    // Get memberId from hidden input
    var memberId = $('#nestedReplyMemberId').val();
	
	console.log("🚀 [Debug] replyId:", replyId);  // 확인용 로그
    console.log("🚀 [Debug] nestedReplyContent:", nestedReplyContent);
    console.log("🚀 [Debug] memberId:", memberId);

    if (!replyId || !nestedReplyContent || !memberId) {
        alert("댓글 ID 또는 내용이 없습니다.");
        return;
    }
    
    // 모달 닫기
    nestedReplyModal.style.display = "none";
	    
}