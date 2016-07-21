var ischeck = false;
var preSelect = null;
var dataList;
//选择金额
$(function(){
	$.ajax({
		type : "POST",
		url : "/service/001-004",
		success : function(res) {
			var data = eval('(' + res + ')');
			dataList = data.goodList;
			var html="";
			var size=0;
			for (var i = 0; i < dataList.length; i++) {
				if(dataList[i].type != 1)
				{
					continue;
				}	
				size++;
				var goodrmbPrice = dataList[i].rmbPrice;
				var goodcoinPrice = dataList[i].coinPrice;
				html+="<div id='index"+i+"' class='single-charge'><p class='charge-num'>"+
					goodcoinPrice+"币</p><p class='charge-select'><img src='./images/gou.png'></p><p class='charge-cost'>￥"+
					goodrmbPrice+"</p></div>";
			}
			$("#goods").html(html);
			var row = Math.ceil(size/2)
			document.getElementById("startpay").style.marginTop =4*row+2+"em";
			for(var i=0;i<dataList.length;i++)
			{
				if(dataList[i].type != 1)
				{
					continue;
				}	
				var single = document.getElementById("index"+i);
				single.index = i;
				single.addEventListener("click",function(evt){
					if(preSelect!=null)
					{
						preSelect.className = "single-charge";
					}
					ischeck = true;
					var target = document.getElementById("index"+this.index);
					target.className = "single-charge single-charge-select";
					preSelect = target;
					document.getElementById('money').innerHTML = dataList[this.index].rmbPrice+"元";
					document.getElementById('goodId').value = dataList[this.index].id;
				},false);
			}
		
		}
	});
	
})
//开始支付
$(function(){
	$("#startpay").click(function(){
		if(ischeck)
		{$.ajax({
			type : "post",
			url : "/service/001-001",
			data : "starId="+$("#userId").val(),
			success : function(data) {
				var json = eval('(' + data + ')');
				if(json.exeStatus == 0){
					$("#failprompt").show();
					$("#res").hide();
				}else{
					$("#res").hide();
					$("#failprompt").hide();
					document.getElementById('payerId').innerHTML = json.user.userId;
					document.getElementById('nickName').innerHTML = json.user.nickName;
					document.getElementById('profile').src = json.user.profile;
					$(".pay_confirm").css("display","block");
				}
			}
		});
		}else{
			$("#res").show();
		}
		
	})
})
//确认支付
$(function(){
	$(".qd").click(function(){
		var goodId = document.getElementById('goodId').value;
		var payerId = document.getElementById('payerId').innerHTML;
		$.ajax({
			type : "post",
			url : "/service/001-033",
			data:{"goodId":goodId,"goodCnt":1,"payerId":payerId,"receiverId":payerId,"payChannel":"2"},
			success : function(res) {
				var data = eval('(' + res + ')');
				window.location.href="http://api.17ace.cn/app/order/pay.jsp?WIDout_trade_no="+data.WIDout_trade_no+"&WIDsubject="+data.WIDsubject+"&WIDtotal_fee="+data.WIDtotal_fee+"&WIDbody="+data.WIDbody+"&channel=pc";
			}
		});
	})
})
//关闭支付
$(function(){
	$(".close-btn").click(function(){
		$(".pay_confirm").css("display","none");
	})
})