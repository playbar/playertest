$(function(){
	$(".week_rank span").click(function(){
		$(".week_rank").addClass("cur_select").siblings().removeClass("cur_select");
		$("#rank_title img").attr('src',"image/week_r_1.png"); 
		// 第一周周榜夺争夺中（5月16日00:00-5月22日24:00）
		
	})
	
	$(".gold_rank span").click(function(){
		$(".gold_rank").addClass("cur_select").siblings().removeClass("cur_select");
		$("#rank_title img").attr('src',"image/gold_r.png"); 
		// 金榜题名（5月16日00:00-6月7日24:00）    
		// var index=$(this).index();
		// $(".main .tab").eq(index).show().siblings().hide();
		
	})

	$(".finals_rank span").click(function(){
		$(".finals_rank").addClass("cur_select").siblings().removeClass("cur_select");
		$("#rank_title img").attr('src',"image/finals_r.png"); 
		
	})
});