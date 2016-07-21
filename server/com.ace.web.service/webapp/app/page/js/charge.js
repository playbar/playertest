//切换标题
$(function(){
	window.onscroll = function () { 
		var top = document.documentElement.scrollTop || document.body.scrollTop; 
		if(top>=0 && top<=400)
		{
			$(".tab1").addClass("cur").parent().siblings().find("a").removeClass("cur");
		}
		if(top>=500 && top<=680)
		{
			$(".tab2").addClass("cur").parent().siblings().find("a").removeClass("cur");
		}
		if( top>=681)
		{
			$(".tab3").addClass("cur").parent().siblings().find("a").removeClass("cur");
		}
		// if(top>=1200)
		// {
		// 	$(".tab4").addClass("cur").parent().siblings().find("a").removeClass("cur");
		// }
		// console.log(top)
	}

	$(".nav a").click(function(){
		$(this).addClass("cur").parent().siblings().find("a").removeClass("cur");
	})
	$(".tab1").click(function(){
		window.scrollTo(0,0)
	})
	$(".tab2").click(function(){
		window.scrollTo(0,600)
	})
	$(".tab3").click(function(){
		window.scrollTo(0,689)
	})
	// $(".tab4").click(function(){
	// 	window.scrollTo(0,1250)
	// })
	$(".charge-tab").click(function(){
		 layer.open({
		        type: 2,
		        title: '充值',
		        shade: false,
		        shadeClose: true, //点击遮罩关闭层
		        offset : ['70px', ''],
	    	    fadeIn: 300,
		        area : ['840px' , '550px'],
		        content: ['http://api.17ace.cn/app/order/index/charge.html','no']
		    });
	})

})


