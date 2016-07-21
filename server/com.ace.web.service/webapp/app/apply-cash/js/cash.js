function GetQueryString(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null)
			return decodeURI(r[2]);
		return null;
	}

var mobileId;
var allrmb = 0;
$(function(){
	var userId = GetQueryString("userId")
	$("#doc").click(function(){
		window.location.href="./cash-doc.html";
	});
	$.ajax({
		type : "post",
		url : "/service/001-019",
		data : "userId="+userId,
		success : function(data) {
			var json = eval('(' + data + ')');
			if(json.exeStatus == 1){
				$("#income").html(json.balance);
				allrmb = parseInt(json.balance/json.rate/100)*100
				$("#rmb").html(allrmb);
			}
		}
	});
	$.ajax({
		type : "post",
		url : "/service/001-024",
		data : {"userId":userId,"channel":"2"},
		success : function(data) {
			var json = eval('(' + data + ')');
			if(json.exeStatus == 1){
				$("#weChatId").html(json.bind.weChatId);
				$("#mobileId").html(json.bind.mobileId);
				mobileId = json.bind.mobileId;
			}
		}
	});
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
				$.ajax({
					type : "post",
					url : "/service/009-003",
					data : "mobileNo="+mobileId,
					success : function(data) {
						var json = eval('(' + data + ')');
						if(json.exeStatus == 0){
							$("#res").html("验证码发送失败");
							return;
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
	
//申请提现
	$("#cash-btn").click(function(){
		//验证金额
		var rmbAmount = $('#rmbAmount');
		if(rmbAmount.val()>allrmb)
		{
			rmbAmount.focus();
			$("#res").html("提现金额不能大于最大值");
			return;
		}
		//100的整数倍
		if( !(/^([1-9][0-9]*)00$/.test(rmbAmount.val())))
		{
			rmbAmount.focus();
			$("#res").html("提现金额只可提取100的整数倍");
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
			url : "/service/001-026",
			data:{"userId":userId,"rmbAmount":$("#rmbAmount").val(),"verifyCode":$("#verifyCode").val(),"mobileId":$("#mobileId").html(),"channel":"2"},
			success : function(data) {
				var json = eval('(' + data + ')');
				if(json.exeStatus == 0){
					if(json.errCode == 216){
						$("#res").html("验证码错误，请重新获取");
					}else{
						$("#res").html("保存失败，请重试");
					}
				}else{
					window.location.href=encodeURI("./cash_succ.html");
				}
			}
		});
	});
})
