<%@ page import="com.alibaba.fastjson.JSONObject,
                 com.alibaba.fastjson.JSONArray,
				 com.rednovo.tools.web.HttpSender,
				 java.util.Date,
				 java.util.Map,
				 java.util.HashMap,
				 java.text.SimpleDateFormat,
				 com.rednovo.tools.DateUtil " 
         language="java" 
         contentType="text/html;charset=UTF-8"%>
         
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
   String userId = request.getParameter("userId");
   if(userId == null){
	   userId = "";
   }
   String startTime = lastYear();
   String endTime = DateUtil.getStringDate();   
   HashMap<String,String> params = new HashMap<String,String>();
   params.put("userId", userId);
   params.put("startTime", startTime);
   params.put("endTime",endTime);
   params.put("page","1");
   params.put("pageSize", "2000");
   params.put("channel", "0"); //出账
   //http request 
   String requestJson = HttpSender.httpClientRequest("http://api.17ace.cn/service/001-006", params);
   
   StringBuffer outHtml = new StringBuffer("");
   
   //http request is not null  
   if(requestJson!=null && !"".equals(requestJson)){
	   JSONObject jsonObj = JSONObject.parseObject(requestJson);
	   
	   if("1".equals(jsonObj.get("exeStatus"))){
		   JSONArray olj = jsonObj.getJSONArray("accountList");
		   String stringDateShore = getStringDateShort();
		   Map<String,String> mapString = new HashMap<String,String>();
		   
		   for(int i=0;i<olj.size();i++){
			   
			   JSONObject oj = olj.getJSONObject(i);
			   String ymd = oj.getString("createTime").substring(0,10);
			   
			   if(mapString.get(ymd) == null){
				 mapString.put(ymd,ymd);
                 if(i!=0){
                	 outHtml.append("</table>");
                 }
                 outHtml.append("<table id=\"This Month\">");
                 outHtml.append("<tr>");
                 outHtml.append("<th><div class=\"round\"></div><p class=\"thTxt\">");
                 outHtml.append(ymd.replace("-", "."));
                 outHtml.append("</p></th>");
                 outHtml.append("</tr>");
			   }
			   outHtml.append("<tr>");
			   outHtml.append("<td id=\"this month bar\" class=\"bar\" style=\"position: relative;\">");
			   outHtml.append("<div class=\"text leftTxt \">");
			   outHtml.append("<p> 金额：<span>"+oj.get("amount")+"</span> </p>");
			   outHtml.append("<div style=\"position: absolute;right: 0;width: 13em;height: 2em;top: 1em;\">"); 
			   outHtml.append("<p style=\"position: absolute;left: 0;\">类型：<span>"+getTypeName(oj.getString("channel"))+"</span></p>");
			   outHtml.append("</div>");
			   outHtml.append("<div class=\"text rightTxt\">");
			   outHtml.append("<p class=\"colorGray\" style=\"position: absolute;right: 15em\">"+oj.getString("createTime").substring(10)+"</p>");
			   outHtml.append("</div>");
			   outHtml.append("<br/>");
			   outHtml.append("</div>");
			   outHtml.append("</td>");
			   outHtml.append("</tr>");
	       }
		   if(olj.size()>1){
			   outHtml.append("</table>"); 
		   }
       }
   }
%>
<html>
	<head>
		<title>消费记录</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" type="text/css" href="css/style.css"/>
	</head>
	<body>
	<div class="container">
	    <div class="link bar border-bottom ">
	        <div class="floatLeft width50  text-align-center margin-top25   goldTxt">
	            <p class="font-size30 margin-left50 color-gold">全部</p>
	        </div>
	
	        <div class="floatRight width50 text-align-center margin-top25 linkTxt  border-left">
	            <a href="consume_gift.jsp?userId=<%=userId%>" class="font-size30 margin-right50">礼物</a>
	        </div>
	        <div class="clear"></div>
	    </div>	
	     <%=outHtml.toString()%>
    </div>
      <%if(outHtml.toString().equals("")){%>
	      <div class="pic-box">
	      	 <br/> 
	         <br/> 
	         <br/> 
	         <br/>
	         <br/>
	         <img src="img/notcontent.png" alt=""/>
	      </div>
      <%}%>    
	</body>
</html>

<%!
	//获得一年前的日期
	public String lastYear(){
	    Date date = new Date();
	    int year=Integer.parseInt(new SimpleDateFormat("yyyy").format(date))-1;
	    int month=Integer.parseInt(new SimpleDateFormat("MM").format(date));
	    int day=Integer.parseInt(new SimpleDateFormat("dd").format(date));
	    String hhmmss = new SimpleDateFormat("HH:mm:ss").format(date);
	    if(day > 28 && month == 2){
	      if(year%4 == 0 && year%100 != 0 || year%400 == 0){
	        day=29;
	      }else{
	    	day=28; 
	      } 
	    }
	    StringBuffer dateStr = new StringBuffer();
	    dateStr.append(year);
	    dateStr.append("-"+(month<10?"0"+month:month+""));
	    dateStr.append("-"+(day<10?"0"+day:day+""));
	    dateStr.append(" "+hhmmss);
	    return dateStr.toString();
	 }

	/**
	 * 获取现在时间
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getStringDateShort() {
	
		Date currentTime = new Date();
	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
		String dateString = formatter.format(currentTime);
	
		return dateString;
	
	}
	
	public static String getTypeName(String type){
	   if("1".equals(type)){
	     return  "订单充值";
	   }else if("2".equals(type)){
	      return "赠送礼物";
	   }else if("3".equals(type)){
	      return "系统加币";
	   }else{
		 return "";
	   }
	}
%>