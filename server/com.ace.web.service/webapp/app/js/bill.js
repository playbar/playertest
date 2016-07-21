$(function(){
	var content = document.location.search; 
	var userId =content.slice(8);
	$("#records").load("receive.jsp?userId="+userId);
	var defaultSelect = document.getElementById("stage0");
	var defaultSelectLine =document.getElementById("line0");
	for(var i=0;i<3;i++)
	{
		var single = document.getElementById("stage"+i);
		single.index = i;
		single.addEventListener("click",function(evt){
			defaultSelect.className = "stage";
			var target = document.getElementById("stage"+this.index);
			target.className = "stage cur";
			defaultSelect = target;
			defaultSelectLine.className = "select-line";
			var targetLine =document.getElementById("line"+this.index);
			targetLine.className = "select-line cur-select-line";
			defaultSelectLine = targetLine;
			window.scrollTo(0,0);
			switch(this.index)
			{
				case 0:
					$("#records").load("receive.jsp?userId="+userId);
				break;
				case 1:
					$("#records").load("consume.jsp?userId="+userId);
				break;
				case 2:
					$("#records").load("recharge.jsp?userId="+userId);
				break;
			}
		},false);
	}
})