<%@page import="java_cup.sym"%>
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
				 com.rednovo.ace.globalData.StaticDataManager,
				 com.rednovo.ace.entity.Gift,
				 com.rednovo.ace.globalData.UserManager,
				 com.rednovo.ace.entity.User,
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
   params.put("senderId", "");
   params.put("receiverId", userId);
   params.put("startTime", startTime);
   params.put("endTime",endTime);
   params.put("page","1");
   params.put("pageSize", "2000");
   params.put("channel", "0"); //出账
   //http request 
   String url = PPConfiguration.getProperties("cfg.properties").getString("http.server.url");
   String requestJson = HttpSender.httpClientRequest(""+url+"/001-048", params);
   oneArray.clear();
   dayArray.clear();
   receiverArray.clear();
   StringBuffer outHtml = new StringBuffer("");
   if(requestJson!=null && !"".equals(requestJson)){
	   JSONObject jsonObj = JSONObject.parseObject(requestJson);
	   if("1".equals(jsonObj.get("exeStatus"))){
		   JSONArray olj = jsonObj.getJSONArray("giftDetailList");
		   String stringDateShore = getStringDateShort();
		   Map<String,String> mapString = new HashMap<String,String>();
		   for(int i=0;i<olj.size();i++){
			   JSONObject oj = olj.getJSONObject(i);
			   String ymd = oj.getString("day").substring(0,7);
			   String day = oj.getString("day");
			   BigDecimal price = getRmbAmount(olj,i);
			   Map<String,String>  map = getDesc(olj,i);
			   if(mapString.get(ymd) == null){
				 mapString.put(ymd,ymd);
                 if(i!=0){
                	 outHtml.append("</div>");
                 }
                 outHtml.append("<div class=\"title\"><p>");
                 outHtml.append(ymd);
                 outHtml.append("</p><p>总计:"+price+"(A豆)</p>");
                 outHtml.append("</div>");
			   }
			   
			   if(mapString.get(day) == null){
				   	mapString.put(day,day); 
				   	outHtml.append("<div class=\"list\">");
				   	outHtml.append("<div class=\"list-single\">");
				    outHtml.append("<p>收到 : "+map.get("nickName")+"</p>");
				   	outHtml.append("<p>"+map.get("sendPrice")+"</p>");
				   	String description = map.get("description");
				   	description = description.substring(0,description.length()-1);
				    outHtml.append("<p>详情：送我"+description+"</p>");
				    outHtml.append("<p>"+oj.getString("day")+"");
				    outHtml.append("<div class=\"line\"></div></div>");
				    outHtml.append("</div>");
			   }
			   if(map.get("mark") != null){
				   String mark = map.get("mark");
				   mark = mark.replace("[", "");
				   mark = mark.replace("]", "");
				   mark = mark.replace(" ", "");
				   String[] markList = mark.split(",");
				   for(int a=0; a<markList.length; a++){
					   String n = markList[a];
						outHtml.append("<div class=\"list\">");
					   	outHtml.append("<div class=\"list-single\">");
					    outHtml.append("<p>收到 : "+map.get(n+"nickName")+"</p>");
					   	outHtml.append("<p>"+map.get(n+"sendPrice")+"</p>");
					   	String description = map.get(n+"description");
					   	description = description.substring(0,description.length()-1);
					    outHtml.append("<p>详情：送我"+description+"</p>");
					    outHtml.append("<p>"+oj.getString("day")+"");
					    outHtml.append("<div class=\"line\"></div></div>");
					    outHtml.append("</div>"); 
				   }
			   }
			   
	       }
		   if(olj.size()>1){
			   outHtml.append("</div>"); 
		   }
       }
   }
%>
<html>
	<head>
		<title>收礼记录</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/consume_record.css"/>
	</head>
	<body>
	<div class="consume-record">
      <%=outHtml.toString()%> 
    </div>
      <%if(outHtml.toString().equals("")){%>
	      <div class="consume-record">
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
	
	ArrayList<String> oneArray = new ArrayList<String>();
	public BigDecimal getRmbAmount(JSONArray ja,int i){
		JSONObject oj1 = ja.getJSONObject(i);
		String one = oj1.getString("day").substring(0,7);
		BigDecimal rmb = new BigDecimal(0);
		if(!oneArray.contains(one)){
			oneArray.add(one);
			for(int n=i;n<ja.size();n++){
				JSONObject oj = ja.getJSONObject(n);
				String ymd = oj.getString("day").substring(0,7);
				Gift gift =  StaticDataManager.getGift("5");
				BigDecimal price = gift.getSendPrice();
				BigDecimal cnt = new BigDecimal(oj.getString("cnt"));
				price = price.multiply(cnt);
				if(one.equals(ymd)){
					rmb = rmb.add(price); 
				}
			}
		}
		return rmb;
	}
	
	ArrayList<String> dayArray = new ArrayList<String>();
	ArrayList<String> receiverArray = new ArrayList<String>();
	public Map<String,String> getDesc(JSONArray ja,int i){
		Map<String,String> map = new HashMap<String,String>();
		ArrayList<Integer> mark = new ArrayList<Integer>(); 
		JSONObject oj = ja.getJSONObject(i);
		String day = oj.getString("day");
		String receiverId = oj.getString("senderId");
		StringBuffer sbf =  new StringBuffer();
		StringBuffer sbff =  new StringBuffer();
		BigDecimal rmb = new BigDecimal(0);
		if(!dayArray.contains(day)){
			dayArray.add(day);
			if(!receiverArray.contains(receiverId)){
				receiverArray.add(receiverId);
				for(int n=i; n<ja.size(); n++){
					JSONObject obj = ja.getJSONObject(n);
					String ymd = obj.getString("day");
					String receiver = obj.getString("senderId");
					Gift gift =  StaticDataManager.getGift("5");
					BigDecimal price = gift.getSendPrice();
					String giftName = gift.getName();
					BigDecimal number = new BigDecimal(obj.getString("cnt"));
					User user =UserManager.getUser(receiver);
					price = price.multiply(number);
					if(day.equals(ymd)){
						if(receiverId.equals(receiver)){
							map.put("description", sbf.append(giftName+"X"+number+",").toString());
							rmb = rmb.add(price); 
							map.put("nickName", user.getNickName());
							map.put("sendPrice", rmb.toString());
						}else{
							if(receiverArray.contains(receiver)){
								String nnn = map.get("nnn");
								StringBuffer string = new StringBuffer();
								String description = map.get(nnn+"description");
								String pricebd = map.get(nnn+"sendPrice");
								string.append(description);
								BigDecimal bd = new BigDecimal(pricebd);
								bd = bd.add(price); 
								
								map.put(nnn+"description", string.append(giftName+"X"+number+",").toString());
								map.put(nnn+"nickName", user.getNickName());
								map.put(nnn+"sendPrice", bd.toString());
							}else{
								BigDecimal rmbb = new BigDecimal(0);
								mark.add(n);
								rmbb = rmbb.add(price); 
								receiverArray.add(receiver);
								sbff.setLength(sbff.length()-sbff.length());
								map.put(n+"description", sbff.append(giftName+"X"+number+",").toString());
								map.put(n+"nickName", user.getNickName());
								map.put(n+"sendPrice", rmbb.toString());
								map.put("mark", mark.toString());
								map.put("nnn", String.valueOf(n));
							}
						}
					}else{
						receiverArray.clear();
						return map;
					}
				}
			}
		}
		receiverArray.clear();
		return map;
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