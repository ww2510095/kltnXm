package com.example.fw.base.Util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import com.example.fw.base.MyParameter;
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


public class ExportExcel {

	private static String EX = MyParameter.TomcatFile+"a.xls";
	
    public static String getEX() {
		return EX;
	}

	public static synchronized void setEX(String eX) {
		EX = eX;
	}


	/**
     * @param a     第一行的注释
     * @param title 标题
     * @param key   mapkey字段
     * @param data  数据
     */
    public static synchronized void Export(String a, List<String> title, List<String> key, List<Map<String, Object>> data, Integer[] ys, HttpServletResponse response) throws IOException {
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
        HSSFCellStyle mHSSFCellStyle = null;
        if (ys != null && ys.length != 0) {
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.RED.index);
            mHSSFCellStyle = wb.createCellStyle();
            mHSSFCellStyle.setFont(font);
            //设置格式
            cell.setCellStyle(mHSSFCellStyle);
        }
        // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, size - 1));
        // 在sheet里创建第二行
        HSSFRow row2 = sheet.createRow(1);
        // 创建单元格并设置单元格内容
        for (int i = 0; i < size; i++)
            row2.createCell(i).setCellValue(title.get(i));//创建标题

        size = data.size();
        int i1 = key.size();

        boolean b1;
        for (int i = 0; i < size; i++) {
            b1 = false;
            //创建数据行
            HSSFRow row3 = sheet.createRow(i + 2);
            if (ys != null && ys.length != 0) {
                for (Integer mys : ys) {
                    b1 = mys == i;
                    if (b1) break;
                }

            }
            for (int j = 0; j < i1; j++) {
                if (b1) {
                    HSSFCell cell1 = row3.createCell(j);
                    //设置数据
                    String strdata = data.get(i).get(key.get(j).toUpperCase()).toString();
                    if (strdata.contains(MyParameter.TomcatFileImage)) {
                        BufferedImage bufferImg = ImageIO.read(new File(strdata));//图片
                        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                        ImageIO.write(bufferImg, "jpg", byteArrayOut);
                        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                        //插入Excel表格
                        HSSFClientAnchor anchor = new HSSFClientAnchor(500, 10, 0, 0,
                                (short) j, (i + 1), (short) (j + 1), (i + 2));
                        patriarch.createPicture(anchor, wb.addPicture(byteArrayOut
                                .toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
                    } else {
                        cell1.setCellValue(data.get(i).get(key.get(j).toUpperCase()).toString());
                        //设置格式
                        cell1.setCellStyle(mHSSFCellStyle);
                    }


                } else {
                    //设置数据
                    try {
                        //设置数据
                        Object strdata = data.get(i).get(key.get(j).toUpperCase());
                        if (strdata instanceof List) {
                            List<String> liststr = (List<String>) strdata;
                            for (String str:liststr
                                 ) {
                                str = str.replace("//", "/");
                                str = str.replace("/", "\\");
                                BufferedImage bufferImg = ImageIO.read(new File(str));//图片
                                ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                                ImageIO.write(bufferImg, "jpg", byteArrayOut);
                                HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                                //插入Excel表格
                                HSSFClientAnchor anchor = new HSSFClientAnchor(10, 10, 10, 10,
                                        (short) j, (i + 2), (short) (j + 1), (i + 3));
                                patriarch.createPicture(anchor, wb.addPicture(byteArrayOut
                                        .toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
                            }

                        } else {
                            row3.createCell(j).setCellValue(data.get(i).get(key.get(j).toUpperCase()).toString());
                        }

                    } catch (Exception e) {
                        row3.createCell(j).setCellValue("");
                    }

                }

            }

        }
        
        if(response==null){
        	OutputStream mOutputStream =new FileOutputStream(new File(EX));
        	wb.write(mOutputStream);
        	mOutputStream.close();
        }else{
        	   // 输出Excel文件
            OutputStream output = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + System.currentTimeMillis() + ".xls");
            response.setContentType("application/msexcel");
            wb.write(output);
            output.close();
        }

     
    }

    
    /**
     * @param a     第一行的注释
     * @param title 标题
     * @param key   mapkey字段
     * @param data  数据
     */
    public static void Export(String a, List<String> title, List<String> key, List<Map<String, Object>> data, HttpServletResponse response) throws IOException {
        Export(a, title, key, data, null, response);
    }

    /**
     * 导出excel图片处理，
     *
     * @param listmap 数据源
     * @param strs    图片字段
     */
    public static void ImagePathKey(List<Map<String, Object>> listmap, String... strs) {
        if (strs == null || strs.length == 0) return;
        for (Map<String, Object> mmap : listmap) {
            java.util.Collection<String> co = mmap.keySet();
            for (String string : co) {
                if (keyset(string, strs)) {
                    try {
                        String str = mmap.get(string).toString();
                        if (str.substring(0, 1).equals("/")) {
                            List<String> strs1 = Arrays.asList(str.split(";"));
                            //str = str.substring(1, str.length());
                            for (String ms:
                                 strs1) {
                                ms=MyParameter.TomcatSD +ms;
                            }
                            mmap.put(string,  strs1);
                        }

                    } catch (Exception e) {
                    }

                }

            }
        }
    }

    private static boolean keyset(String key, String... strs) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals(key)) return true;
        }
        return false;
    }


}
