/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播移动
 *                  公司名称:美播娱乐
 *                  开发作者:sg.z
 *       			开发时间:2014年7月29日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自美播娱乐研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：meibo-admin
 *                  fileName：UserHandler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler.jsonData;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * @author wenhui.Wang/2016年5月7日
 */
public class OrderJsonHandler extends BasicServiceAdapter {
	Logger logger = Logger.getLogger(OrderJsonHandler.class);
	
	@Override
	protected void service() {
		
		String key = this.getKey();
		
		if ("001-073".equals(key)){//-提现管理
			this.getMoneyManage();
		} else if ("001-083".equals(key)){//-提现管理-更改提现状态
			this.updateStatus();
		} else if ("001-360".equals(key)){//提现管理--导出打款记录
			try {
				this.creatMoneyeRecordExcel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 提现管理查询
	 */
	public void getMoneyManage(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String startTime = this.getWebHelper().getString("startTime");
		if(startTime!=null&&!startTime.equals("")){
			para.put("startTime", startTime);			
		}
		
		String endTime = this.getWebHelper().getString("endTime");
		if(endTime!=null&&!endTime.equals("")){
			para.put("endTime", endTime);			
		}
		
		String status="";
		int statu = this.getWebHelper().getInt("status");
		para.put("status", status.valueOf(statu));		
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		StringBuffer sbf = new StringBuffer(jsonData);
		sbf.replace(2, 14, "rows");
		sbf.replace(0, 1, "{\"total\":\"4\",");
		
		JSONObject jsonObject = JSONObject.parseObject(sbf.toString());
		this.setJsonObj(jsonObject);	
	}
	/**
	 * 修改提现管理的状态
	 */
	public void updateStatus(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String id = this.getWebHelper().getString("id");
		para.put("id", id);
		
		String status = "";
		int statu = this.getWebHelper().getInt("status");
		para.put("status", status.valueOf(statu+1));	

		String path = this.getWebHelper().getString("path");
		try {
			String jsonData = getJsonData(path, para);	
			this.setSuccess();
		} catch (Exception e) {
			this.setError("修改提现状态失败！");
		}
	
	}	
	
	/**
	 * 提现管理--导出打款记录
	 * 
	 * @author wenhui.Wang
	 * @throws Exception 
	 * @since 2016年5月11日
	 */
	private void creatMoneyeRecordExcel() throws Exception{
		HashMap<String, String> para = new HashMap<String, String>();
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		JSONObject jsonObject =  JSONObject.parseObject(jsonData);
		
	 	HttpServletResponse response = this.getWebHelper().getResponse();
	    OutputStream os = response.getOutputStream();//
	    response.reset();
	    //下面是对中文文件名的处理
	    String fname = "导出打款记录";
	    response.setCharacterEncoding("UTF-8");//设置相应内容的编码格式
	    fname = java.net.URLEncoder.encode(fname,"UTF-8");
	    response.setHeader("Content-Disposition","attachment;filename="+new String(fname.getBytes("UTF-8"),"GBK")+".xls");
	    response.setContentType("application/msexcel");//定义输出类型
	    createExcel(response.getOutputStream(),jsonObject);
	}
	
	/**
	 * 导出
	 * 
	 * @author wenhui.Wang
	 * @throws Exception 
	 * @since 2016年5月12日
	 */
	public static void createExcel(OutputStream os,JSONObject jsonObject ){
			
		WritableWorkbook workbook = null;
		WritableSheet sheet=null;
		try{
		
			workbook = Workbook.createWorkbook(os);
	        //创建新的一页
	        sheet = workbook.createSheet("First Sheet",0);
	        //创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
	        
	        JSONArray jsonArray =jsonObject.getJSONArray("exchangeList");
	        
	        Label labelCoinAmount = new Label(0,0,"coinAmount");
		    sheet.addCell(labelCoinAmount);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("coinAmount");
				Label coinAmount = new Label(0,++i,string);
			    sheet.addCell(coinAmount);
			}
			
			Label labelCreateTime = new Label(1,0,"创建时间");
		    sheet.addCell(labelCreateTime);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("createTime");
				Label createTime = new Label(1,++i,string);
			    sheet.addCell(createTime);
			}
			
			Label labelId = new Label(2,0,"ID");
		    sheet.addCell(labelId);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("id");
				Label id = new Label(2,++i,string);
			    sheet.addCell(id);
			}
	        
			Label labelMobileId = new Label(3,0,"MobileId");
		    sheet.addCell(labelMobileId);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("mobileId");
				Label mobileId = new Label(3,++i,string);
			    sheet.addCell(mobileId);
			}
			
			Label labelPayerId = new Label(4,0,"PayerId");
		    sheet.addCell(labelPayerId);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("payerId");
				Label payerId = new Label(4,++i,string);
			    sheet.addCell(payerId);
			}
			
			Label labelPayerName = new Label(5,0,"PayerName");
		    sheet.addCell(labelPayerName);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("payerName");
				Label payerName = new Label(5,++i,string);
			    sheet.addCell(payerName);
			}
			
			Label labelRmbAmount = new Label(6,0,"RmbAmount");
		    sheet.addCell(labelRmbAmount);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("rmbAmount");
				Label rmbAmount = new Label(6,++i,string);
			    sheet.addCell(rmbAmount);
			}
			
			Label labelStatus = new Label(7,0,"Status");
		    sheet.addCell(labelStatus);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("status");
				Label status = new Label(7,++i,string);
			    sheet.addCell(status);
			}
			
			Label labelUpdateTime = new Label(8,0,"UpdateTime");
		    sheet.addCell(labelUpdateTime);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("updateTime");
				Label updateTime = new Label(8,++i,string);
			    sheet.addCell(updateTime);
			}
			
			Label labelUserId = new Label(9,0,"UserId");
		    sheet.addCell(labelUserId);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("userId");
				Label userId = new Label(9,++i,string);
			    sheet.addCell(userId);
			}
			
			Label labelUserName= new Label(10,0,"UserName");
		    sheet.addCell(labelUserName);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("userName");
				Label userName = new Label(10,++i,string);
			    sheet.addCell(userName);
			}
			
			Label labelWeChatId= new Label(11,0,"WeChatId");
		    sheet.addCell(labelWeChatId);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				String string = jo.getString("weChatId");
				Label weChatId = new Label(11,++i,string);
			    sheet.addCell(weChatId);
			}
			
	        workbook.write();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
		        try {
		        	if(workbook != null){
		        		workbook.close();
		        	}
				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		        try {
		        	if(os != null){
		        		os.close();
		        	}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
}
