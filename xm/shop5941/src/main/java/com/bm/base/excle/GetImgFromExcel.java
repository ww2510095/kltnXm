package com.bm.base.excle;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

import com.Shop5941Application;
 
/**
 * @since 2013-04-22
 
 * @author Gerrard
 
 * 获取excel中 图片，并得到图片位置，支持03 07 多sheet
 
 */
public class GetImgFromExcel {
 
    /**
     * @param args
     * @throws IOException 
     * @throws InvalidFormatException 
     */
    public static void main(String[] args) throws InvalidFormatException, IOException {
        
        // 创建文件
        File file = new File("C:\\Users\\Administrator\\Desktop\\a\\123.xls");
        
        // 创建流
        InputStream input = new FileInputStream(file);
        
        // 获取文件后缀名
        String fileExt =  file.getName().substring(file.getName().lastIndexOf(".") + 1);
        
        // 创建Workbook
        Workbook wb = null;
        
        // 创建sheet
        Sheet sheet = null;
 
        //根据后缀判断excel 2003 or 2007+
        if (fileExt.equals("xls")) {
            wb = (HSSFWorkbook) WorkbookFactory.create(input);
        } else {
            wb = new XSSFWorkbook(input);
        }
        
        //获取excel sheet总数
        int sheetNumbers = wb.getNumberOfSheets();
        
        // sheet list
        List<Map<String, PictureData>> sheetList = new ArrayList<Map<String, PictureData>>();
        
        // 循环sheet
        for (int i = 0; i < sheetNumbers; i++) {
            
            sheet = wb.getSheetAt(i);
            // map等待存储excel图片
            Map<String, PictureData> sheetIndexPicMap; 
            
            // 判断用07还是03的方法获取图片
            if (fileExt.equals("xls")) {
                sheetIndexPicMap = getSheetPictrues03(i, (HSSFSheet) sheet, (HSSFWorkbook) wb);
            } else {
                sheetIndexPicMap = getSheetPictrues07(i, (XSSFSheet) sheet, (XSSFWorkbook) wb);
            }
            // 将当前sheet图片map存入list
            sheetList.add(sheetIndexPicMap);
        }
        
        printImg(sheetList);
 
    }
    
    /**
     * 获取Excel2003图片
     * @param sheetNum 当前sheet编号
     * @param sheet 当前sheet对象
     * @param workbook 工作簿对象
     * @return Map key:图片单元格索引（0_1_1）String，value:图片流PictureData
     * @throws IOException
     */
    public static Map<String, PictureData> getSheetPictrues03(int sheetNum,
            HSSFSheet sheet, HSSFWorkbook workbook) {
 
        Map<String, PictureData> sheetIndexPicMap = new HashMap<String, PictureData>();
        List<HSSFPictureData> pictures = workbook.getAllPictures();
        if (pictures.size() != 0) {
            Shop5941Application.out("======================");
            Shop5941Application.out( sheet.getDrawingPatriarch());
            Shop5941Application.out("======================");
//        	Application.out( sheet.getDrawingPatriarch().getChildren().size());
            Shop5941Application.out("======================");
            for (HSSFShape shape : sheet.getDrawingPatriarch().getChildren()) {
                HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
                if (shape instanceof HSSFPicture) {
                    HSSFPicture pic = (HSSFPicture) shape;
                    int pictureIndex = pic.getPictureIndex() - 1;
                    HSSFPictureData picData = pictures.get(pictureIndex);
                    String picIndex = String.valueOf(sheetNum) + "_"
                            + String.valueOf(anchor.getRow1()) + "_"
                            + String.valueOf(anchor.getCol1());
                    sheetIndexPicMap.put(picIndex, picData);
                }else{
                	
                }
            }
            return sheetIndexPicMap;
        } else {
            return null;
        }
    }
 
    /**
     * 获取Excel2007图片
     * @param sheetNum 当前sheet编号
     * @param sheet 当前sheet对象
     * @param workbook 工作簿对象
     * @return Map key:图片单元格索引（0_1_1）String，value:图片流PictureData
     */
    public static Map<String, PictureData> getSheetPictrues07(int sheetNum,
            XSSFSheet sheet, XSSFWorkbook workbook) {
        Map<String, PictureData> sheetIndexPicMap = new HashMap<String, PictureData>();
 
        for (POIXMLDocumentPart dr : sheet.getRelations()) {
            if (dr instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) dr;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture pic = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = pic.getPreferredSize();
                    CTMarker ctMarker = anchor.getFrom();
                    String picIndex = String.valueOf(sheetNum) + "_"
                            + ctMarker.getRow() + "_" + ctMarker.getCol();
                    sheetIndexPicMap.put(picIndex, pic.getPictureData());
                }
            }
        }
 
        return sheetIndexPicMap;
    }
    
    public static void printImg(List<Map<String, PictureData>> sheetList) throws IOException {
        
        for (Map<String, PictureData> map : sheetList) {
            Object key[] = map.keySet().toArray();
            for (int i = 0; i < map.size(); i++) {
                // 获取图片流
                PictureData pic = map.get(key[i]);
                // 获取图片索引
                String picName = key[i].toString();
                // 获取图片格式
                String ext = pic.suggestFileExtension();
                
                byte[] data = pic.getData();
                
                FileOutputStream out = new FileOutputStream("D:\\pic" + picName + "." + ext);
                out.write(data);
                out.close();
            }
        }
        
    }
  
}
