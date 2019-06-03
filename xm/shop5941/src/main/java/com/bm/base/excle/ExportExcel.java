package com.bm.base.excle;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.bm.base.MyParameter;
import com.bm.base.util.MyDate;
import com.bm.webapp.data.SaleDara;

public class ExportExcel {
	
	public static class Exa{
		private String code;
		private List<SaleDara> mSaleDara;
		private String name;
		private String address;
		private String phone;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public List<SaleDara> getmSaleDara() {
			return mSaleDara;
		}
		public void setmSaleDara(List<SaleDara> mSaleDara) {
			this.mSaleDara = mSaleDara;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		
		
	}
	
	
	
	/**
	 * 
	 * 为供应部导出销售销售数据
	 * */
	public static void ExportA(List<Exa> listExa,HttpServletResponse response) throws IOException {

		// 创建HSSFWorkbook对象(excel的文档对象)
		HSSFWorkbook wb = new HSSFWorkbook();
		// 建立新的sheet对象（excel的表单）
		HSSFSheet sheet = wb.createSheet();
		
		HSSFCellStyle cellStyle = wb.createCellStyle();  
		
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框    
		cellStyle.setAlignment(HSSFCellStyle.VERTICAL_CENTER); // 居中  
		
		int rows=0;
		for (Exa mExa : listExa) {
			//标题
			HSSFRow row = sheet.createRow(rows);
			
			HSSFCell createCell1=row.createCell(0);
			createCell1.setCellValue("订单号");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(1);
			createCell1.setCellValue("商品条码");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(2);
			createCell1.setCellValue("助记码");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(3);
			createCell1.setCellValue("商品名");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(4);
			createCell1.setCellValue("颜色");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(5);
			createCell1.setCellValue("尺码");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(6);
			createCell1.setCellValue("数量");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(7);
			createCell1.setCellValue("厂家");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(8);
			createCell1.setCellValue("品牌");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(9);
			createCell1.setCellValue("订单状态");
			createCell1.setCellStyle(cellStyle);
			
			createCell1=row.createCell(10);
			createCell1.setCellValue("下单时间");
			createCell1.setCellStyle(cellStyle);
			
			
			rows=rows+1;
			int ca = rows;
			for (SaleDara mSaleDara : mExa.getmSaleDara()) {
				HSSFRow rowa = sheet.createRow(rows);
				
				HSSFCell createCell=rowa.createCell(0);
				createCell.setCellValue(mExa.getCode());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(1);
				createCell.setCellValue(mSaleDara.getBarcode());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(2);
				createCell.setCellValue(mSaleDara.getCode());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(3);
				createCell.setCellValue(mSaleDara.getName());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(4);
				createCell.setCellValue(mSaleDara.getColour());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(5);
				createCell.setCellValue(mSaleDara.getSize());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(6);
				createCell.setCellValue(mSaleDara.getNumber());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(7);
				createCell.setCellValue(mSaleDara.getManufactor());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(8);
				createCell.setCellValue(mSaleDara.getBrand());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(9);
				createCell.setCellValue(mSaleDara.getType());
				createCell.setCellStyle(cellStyle);
				
				createCell=rowa.createCell(10);
				createCell.setCellValue(MyDate.stampToDate(Long.valueOf(mExa.getCode())));
				createCell.setCellStyle(cellStyle);
				
				rows=rows+1;
			}
			sheet.addMergedRegion(new CellRangeAddress(ca, rows-1, 0,0));
			//姓名
			
			sheet.addMergedRegion(new CellRangeAddress(rows, rows, 0,10));
			HSSFRow rowa = sheet.createRow(rows);
			HSSFCell 
			createCell=rowa.createCell(0);
			createCell.setCellValue("姓名： "+mExa.getName());
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(1);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(2);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(3);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(4);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(5);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(6);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(7);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(8);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(9);
			createCell.setCellStyle(cellStyle);
			createCell=rowa.createCell(10);
			createCell.setCellStyle(cellStyle);
			
			rows=rows+1;
			//地址
			
			sheet.addMergedRegion(new CellRangeAddress(rows, rows, 0,10));
			HSSFRow rowa1 = sheet.createRow(rows);
			HSSFCell 
			createCell2=rowa1.createCell(0);
			createCell2.setCellValue("地址： "+mExa.getAddress());
			createCell2.setCellStyle(cellStyle);
			createCell2=rowa1.createCell(1);
			createCell2.setCellStyle(cellStyle);
			createCell2=rowa1.createCell(2);
			createCell2.setCellStyle(cellStyle);
			createCell2=rowa1.createCell(3);
			createCell2.setCellStyle(cellStyle);
			createCell2=rowa1.createCell(4);
			createCell2.setCellStyle(cellStyle);
			createCell2=rowa1.createCell(5);
			createCell2.setCellStyle(cellStyle);
			createCell2=rowa1.createCell(6);
			createCell2.setCellStyle(cellStyle);
			createCell2=rowa1.createCell(7);
			createCell2.setCellStyle(cellStyle);
			
//			createCell1=sheet.createRow(rows).createCell(0);
			
			createCell2.setCellStyle(cellStyle);
			rows=rows+1;
			//电话
			sheet.addMergedRegion(new CellRangeAddress(rows, rows, 0,10));
//			createCell1=sheet.createRow(rows).createCell(0);
			HSSFRow rowa2 = sheet.createRow(rows);
			HSSFCell 
			createCell3=rowa2.createCell(0);
			createCell3.setCellValue("电话： "+mExa.getPhone());
			createCell3.setCellStyle(cellStyle);
			createCell3=rowa2.createCell(1);
			createCell3.setCellStyle(cellStyle);
			createCell3=rowa2.createCell(2);
			createCell3.setCellStyle(cellStyle);
			createCell3=rowa2.createCell(3);
			createCell3.setCellStyle(cellStyle);
			createCell3=rowa2.createCell(4);
			createCell3.setCellStyle(cellStyle);
			createCell3=rowa2.createCell(5);
			createCell3.setCellStyle(cellStyle);
			createCell3=rowa2.createCell(6);
			createCell3.setCellStyle(cellStyle);
			createCell3=rowa2.createCell(7);
			createCell3.setCellStyle(cellStyle);
			
			rows=rows+1;
			
			
			
			rows=rows+1;
			
		}

		// 输出Excel文件
		OutputStream output = response.getOutputStream();
		response.reset();
		response.setHeader("Content-disposition", "attachment; filename=" + System.currentTimeMillis() + ".xls");
		response.setContentType("application/msexcel");
		wb.write(output);
		output.close();
		
	}
	
	/**
	 * @param a  第一行的注释
	 * @param title 标题
	 * @param key mapkey字段
	 * @param data 数据
	 * @ys 颜色行
	 * */
	public static void Export(String a,List<String> title,List<String> key,List<Map<String, Object>> data, Integer[] ys,HttpServletResponse response) throws IOException {
		int size = title.size();
		
		
		// 创建HSSFWorkbook对象(excel的文档对象)
		HSSFWorkbook wb = new HSSFWorkbook();
		// 建立新的sheet对象（excel的表单）
		HSSFSheet sheet = wb.createSheet();
		// 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
		HSSFRow row1 = sheet.createRow(0);
		// 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
		HSSFCell cell = row1.createCell(0);
		// 设置单元格内容
		cell.setCellValue(a);
		//字体颜色
		HSSFCellStyle mHSSFCellStyle=null;
		if(ys!=null&&ys.length!=0){
			HSSFFont font =  wb.createFont();
			font.setColor(HSSFColor.RED.index);
			mHSSFCellStyle=wb.createCellStyle();
			mHSSFCellStyle.setFont(font);
			//设置格式
			cell.setCellStyle(mHSSFCellStyle);
		}
		
		// 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0,size-1 ));
		// 在sheet里创建第二行
		HSSFRow row2 = sheet.createRow(1);
		// 创建单元格并设置单元格内容
		for (int i = 0; i < size; i++)
			row2.createCell(i).setCellValue(title.get(i));//创建标题
		
		size = data.size();
		int i1 = key.size();
		
		boolean b1;
		for (int i =0; i< size; i++) {
			 b1 = false;
			//创建数据行
			HSSFRow row3 = sheet.createRow(i+2);
			if(ys!=null&&ys.length!=0){
				for (Integer mys : ys) {
					b1=mys==i;
					if(b1) break;
				}
						
			}
			for (int j = 0; j < i1; j++) {
				if(b1){
					HSSFCell cell1 = row3.createCell(j);
					//设置数据
					String strdata=data.get(i).get(key.get(j).toUpperCase()).toString();
					if(strdata.contains(MyParameter.TomcatFileImage)){
						BufferedImage bufferImg=ImageIO.read(new File(strdata));//图片
						 ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream(); 
						 ImageIO.write(bufferImg, "jpg", byteArrayOut);  
						 HSSFPatriarch patriarch = sheet.createDrawingPatriarch(); 
		    	        //插入Excel表格
		    	        HSSFClientAnchor anchor = new HSSFClientAnchor(500, 10,0, 0,  
		    	        		 (short) j, (i+1), (short)(j+1) , (i+2)); 
		                patriarch.createPicture(anchor, wb.addPicture(byteArrayOut  
		                        .toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
					}else{
						cell1.setCellValue(data.get(i).get(key.get(j).toUpperCase()).toString());
						//设置格式
						cell1.setCellStyle(mHSSFCellStyle);
					}
					
					
				}else{
					//设置数据
					try {
						//设置数据
						String strdata=data.get(i).get(key.get(j).toUpperCase()).toString();
						if(strdata.contains(MyParameter.TomcatFileImage)){
							strdata=strdata.replace("//", "/");
							strdata=strdata.replace("/", "\\");
							BufferedImage bufferImg=ImageIO.read(new File(strdata));//图片
							 ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream(); 
							 ImageIO.write(bufferImg, "jpg", byteArrayOut);  
							 HSSFPatriarch patriarch = sheet.createDrawingPatriarch(); 
			    	        //插入Excel表格
			    	        HSSFClientAnchor anchor = new HSSFClientAnchor(10, 10,10, 10,  
			    	        		 (short) j, (i+2), (short)(j+1) , (i+3)); 
			                patriarch.createPicture(anchor, wb.addPicture(byteArrayOut  
			                        .toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
						}else{
							row3.createCell(j).setCellValue(data.get(i).get(key.get(j).toUpperCase()).toString());
						}
						
					} catch (Exception e) {
						row3.createCell(j).setCellValue("");
					}
					
				}
				
			}
			
		}
		

		// 输出Excel文件
		OutputStream output = response.getOutputStream();
		response.reset();
		response.setHeader("Content-disposition", "attachment; filename=" + System.currentTimeMillis() + ".xls");
		response.setContentType("application/msexcel");
		wb.write(output);
		output.close();
	}
	/**
	 * @param a  第一行的注释
	 * @param title 标题
	 * @param key mapkey字段
	 * @param data 数据
	 * 
	 * */
	public static void Export(String a,List<String> title,List<String> key,List<Map<String, Object>> data, HttpServletResponse response) throws IOException {
		Export(a, title, key, data,null, response);
	}
//	/**
//	 * @param a  第一行的注释
//	 * @param title 标题
//	 * @param key mapkey字段
//	 * @param data 数据
//	 * @同一列相同数值融合
//	 * */
//	public static void Export(String a,List<String> title,List<String> key,List<Map<String, Object>> data, HttpServletResponse response) throws IOException {
//		int size = title.size();
//		
//		
//		// 创建HSSFWorkbook对象(excel的文档对象)
//		HSSFWorkbook wb = new HSSFWorkbook();
//		// 建立新的sheet对象（excel的表单）
//		HSSFSheet sheet = wb.createSheet();
//		// 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
//		HSSFRow row1 = sheet.createRow(0);
//		// 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
//		HSSFCell cell = row1.createCell(0);
//		// 设置单元格内容
//		cell.setCellValue(a);
//		//字体颜色
//		HSSFCellStyle mHSSFCellStyle=null;
//		if(ys!=null&&ys.length!=0){
//			HSSFFont font =  wb.createFont();
//			font.setColor(HSSFColor.RED.index);
//			mHSSFCellStyle=wb.createCellStyle();
//			mHSSFCellStyle.setFont(font);
//			//设置格式
//			cell.setCellStyle(mHSSFCellStyle);
//		}
//		
//		// 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
//		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0,size-1 ));
//		// 在sheet里创建第二行
//		HSSFRow row2 = sheet.createRow(1);
//		// 创建单元格并设置单元格内容
//		for (int i = 0; i < size; i++)
//			row2.createCell(i).setCellValue(title.get(i));//创建标题
//		
//		size = data.size();
//		int i1 = key.size();
//		
//		boolean b1;
//		for (int i =0; i< size; i++) {
//			b1 = false;
//			//创建数据行
//			HSSFRow row3 = sheet.createRow(i+2);
//			if(ys!=null&&ys.length!=0){
//				for (Integer mys : ys) {
//					b1=mys==i;
//					if(b1) break;
//				}
//				
//			}
//			for (int j = 0; j < i1; j++) {
//				if(b1){
//					HSSFCell cell1 = row3.createCell(j);
//					//设置数据
//					cell1.setCellValue(data.get(i).get(key.get(j).toUpperCase()).toString());
//					//设置格式
//					cell1.setCellStyle(mHSSFCellStyle);
//				}else{
//					//设置数据
//					try {
//						row3.createCell(j).setCellValue(data.get(i).get(key.get(j).toUpperCase()).toString());
//					} catch (Exception e) {
//						row3.createCell(j).setCellValue("");
//					}
//					
//				}
//				
//			}
//			
//		}
//		
//		
//		// 输出Excel文件
//		OutputStream output = response.getOutputStream();
//		response.reset();
//		response.setHeader("Content-disposition", "attachment; filename=" + System.currentTimeMillis() + ".xls");
//		response.setContentType("application/msexcel");
//		wb.write(output);
//		output.close();
//	}


}
