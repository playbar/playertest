package com.rednovo.tools;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import com.rednovo.ace.constant.Constant;
import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
/**
 * 工具类
 * @author lxg
 *
 */
public class CommonUtils {
	private static Logger logger = Logger.getLogger(CommonUtils.class);
	
	public static final int seconds = 1000 * 1;
	public static final int minute = 60 * seconds;
	public static final int hour = 60 * minute;
	
	/**
	 * 包装字符串，如果为空，则设置默认值0
	 * @param str
	 * @author lxg
	 * @return
	 */
	public static String wapperStr(String str){
		if(str == null){
			return "0";
		}else{
			return str;
		}
	}
	
	/**
	 * 写入数据到文件中
	 * @param path 项目绝对路径
	 * @param uuid 随机id
	 * @param data 图片数据
	 * @param suffix 图片后缀
	 * @author lxg
	 * @return
	 */
	public static Constant.OperaterStatus writeFile(String path, String uuid, byte[] data, String sign, String suffix){
		File file = new File(path + File.separator + uuid + sign +  suffix);
		FileOutputStream fis;
		try {
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			fis = new FileOutputStream(file);
			fis.write(data);
			fis.flush();
			fis.close();
		} catch (Exception e) {
			logger.error("上传图像失败", e);
			e.printStackTrace();
			return Constant.OperaterStatus.FAILED;
		}
		return Constant.OperaterStatus.SUCESSED;
	}
	
	/**
	 * 导出数据到excel文件中
	 * @param os
	 * @throws WriteException
	 * @throws IOException
	 */
	public static void createExcel(OutputStream os){
		WritableWorkbook workbook = null;
		WritableSheet sheet=null;
		try{
        workbook = Workbook.createWorkbook(os);
        //创建新的一页
        sheet = workbook.createSheet("First Sheet",0);
        //创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
        Label xuexiao = new Label(0,0,"学校");
        sheet.addCell(xuexiao);
        Label zhuanye = new Label(1,0,"专业");
        sheet.addCell(zhuanye);
        Label jingzhengli = new Label(2,0,"专业竞争力");
        sheet.addCell(jingzhengli);
        
        Label qinghua = new Label(0,1,"清华大学");
        sheet.addCell(qinghua);
        Label jisuanji = new Label(1,1,"计算机专业");
        sheet.addCell(jisuanji);
        Label gao = new Label(2,1,"高");
        sheet.addCell(gao);
        
        Label beida = new Label(0,2,"北京大学");
        sheet.addCell(beida);
        Label falv = new Label(1,2,"法律专业");
        sheet.addCell(falv);
        Label zhong = new Label(2,2,"中");
        sheet.addCell(zhong);
        
        Label ligong = new Label(0,3,"北京理工大学");
        sheet.addCell(ligong);
        Label hangkong = new Label(1,3,"航空专业");
        sheet.addCell(hangkong);
        Label di = new Label(2,3,"低");
        sheet.addCell(di);
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
