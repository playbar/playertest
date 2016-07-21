<%@page import="java.util.ArrayList"%>
<%@page import="java.math.BigDecimal"%>
<%@ page import="com.alibaba.fastjson.JSONObject,
                 com.alibaba.fastjson.JSONArray,
				 com.rednovo.tools.web.HttpSender,
				 com.rednovo.tools.PPConfiguration,
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
   //http request 
   String url = PPConfiguration.getProperties("cfg.properties").getString("http.server.url");
   String requestJson = HttpSender.httpClientRequest(""+url+"/001-005", params);
   oneArray.clear();
   StringBuffer outHtml = new StringBuffer();
   //http request is not null  
   if(requestJson!=null && !"".equals(requestJson)){
	   JSONObject jsonObj = JSONObject.parseObject(requestJson);
	   if("1".equals(jsonObj.get("exeStatus"))){
		   JSONArray olj = jsonObj.getJSONArray("payList");
		   String stringDateShore = getStringDateShort();
		   Map<String,String> mapString = new HashMap<String,String>();
		   for(int i=0;i<olj.size();i++){
			 	BigDecimal rmb = getRmbAmount(olj,i);
			   JSONObject oj = olj.getJSONObject(i);
			   String ymd = oj.getString("createTime").substring(0,7);
			   if(mapString.get(ymd) == null){
				 mapString.put(ymd,ymd);
                 if(i!=0){
                	 outHtml.append("</div>");
                 }
                 outHtml.append("<div class=\"title\"><p>");
                 outHtml.append(ymd);
                 outHtml.append("</p><p>充值:￥"+rmb+"</p>");
                 outHtml.append("</div>");
			   }
			   	outHtml.append("<div class=\"list\">");
			   	outHtml.append("<div class=\"list-single\">");
			   	outHtml.append("<p>"+oj.getString("coinAmount")+"金币</p>");
			    outHtml.append("<p>￥"+oj.getString("rmbAmount")+"</p>");
			    outHtml.append("<p> 订单号："+oj.getString("orderId")+"</p>");
			    outHtml.append("<p>"+oj.getString("createTime").substring(0,10)+"");
			    outHtml.append(""+oj.getString("createTime").substring(10)+"</p>");
			    outHtml.append("<div class=\"line\"></div></div>");
			    outHtml.append("</div>");
	       }
		   if(olj.size()>1){
			   outHtml.append("</div>"); 
		   }
       }
   }
%>
<html>
	<head>
		<title>充值记录</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/charge_record.css"/>
	</head>
	<body>
		<div class="charge-record">
       		<%=outHtml.toString()%>
        </div>
        
      <%if(outHtml.toString().equals("")){%>
	      <div class="charge-record">
	      	 <br/> 
	         <br/> 
	         <br/> 
	         <br/>
	         <br/>
	         <img src="img/notcontent.png" alt="" style="width:60%;margin-left:20%;"/>
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

	ArrayList<String> oneArray = new ArrayList<String>();
	public BigDecimal getRmbAmount(JSONArray ja,int i){
		JSONObject oj1 = ja.getJSONObject(i);
		String one = oj1.getString("createTime").substring(0,7);
		BigDecimal rmb = new BigDecimal(0);
		if(!oneArray.contains(one)){
			oneArray.add(one);
			for(int n=i;n<ja.size();n++){
				JSONObject oj = ja.getJSONObject(n);
				String ymd = oj.getString("createTime").substring(0,7);
				if(one.equals(ymd)){
					rmb = rmb.add(new BigDecimal(oj.getString("rmbAmount"))); 
				}
			}
		}
		return rmb;
	}

	/**
	 * 获取现在时间
	 * @return 返回短时间字符串格式yyyy-MM
	 */
	public static String getStringDateShort() {
	
		Date currentTime = new Date();
	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
	
		String dateString = formatter.format(currentTime);
	
		return dateString;
	
	}
%>