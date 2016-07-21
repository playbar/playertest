
function GetQueryString(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null)
			return decodeURI(r[2]);
		return null;
	}

//获取验证码
$(function(){
	var countdown = 120;
	var interval = 0;
	function onCountDownHandler()
	{
		countdown--;
		$(".verify>p").html(countdown+"s");
		if(countdown<=0)
		{
			countdown = 120;
			clearInterval(interval);
			$(".verify>p").html("获取验证码");
			$(".verify").css("background-color","#0090ff");
			addClick();
		}
	}
	function addClick()
	{
		$(".verify").one("click",function(){
			var reg = /^0?1[3|4|5|8][0-9]\d{8}$/;
			if($("#mobileId").val().length!=11 || !reg.test($("#mobileId").val()))
			{
				$(".fail-prompt p").html("手机号格式不正确");
				addClick();
				return;
			}
			$(".fail-prompt p").html("");
			$.ajax({
				type : "post",
				url : "/service/009-003",
				data : "mobileNo="+$("#mobileId").val(),
				success : function(data) {
					var json = eval('(' + data + ')');
					if(json.exeStatus == 0){
						$("#res").html("验证码发送失败");
					}/*else{
						alert("yifasong");
					}*/
				}
			});
			$(".verify>p").html(countdown+"s");
			$(".verify").css("background-color","#aaaaaa");
			$(".verify").css("enable",false);
			interval = setInterval(onCountDownHandler,1000);
		})
	}
	addClick();
})

$(function(){
	var userId = GetQueryString("userId")
	$("#cash-btn").click(function(){
		if($("#weChatId").val()=='')
		{
			$("#weChatId").focus();
			$(".fail-prompt p").html("请输入帐号");
			return;
		}
		if($("#userName").val()=='')
		{
			$("#userName").focus();
			$(".fail-prompt p").html("请输入帐号对应的姓名");
			return;
		}
		if($("#mobileId").val().length!=11)
		{
			$("#mobileId").focus();
			$(".fail-prompt p").html("手机格式不正确");
			return;
		}
		if( $("#verifyCode").val()=='')
		{
			$("#verifyCode").focus();
			$("#res").html("请输入验证码");
			return;
		}
		$.ajax({
			type : "post",
			url : "/service/001-025",
			data:{"userId":userId,"weChatId":$("#weChatId").val(),"userName":$("#userName").val(),"mobileId":$("#mobileId").val(),"verifyCode":$("#verifyCode").val(),"channel":"2"},
			success : function(data) {
				var json = eval('(' + data + ')');
				if(json.exeStatus == 0){
					if(json.errCode == 216){
						$("#res").html("验证码错误，请重新获取");
					}else{
						$("#res").html("保存失败，请重试");
					}
				}else{
					window.location.href=encodeURI("./cash.html?userId="+userId);
				}
			}
		});
	});
})
