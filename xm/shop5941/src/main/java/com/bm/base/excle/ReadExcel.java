package com.bm.base.excle;

import com.Shop5941Application;
import com.bm.base.MyParameter;
import com.bm.file.FileService;
import com.bm.file.MyFile;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReadExcel {
	
//	private static final Logger mLogger= LoggerFactory.getLogger(ReadExcel.class);
	
	// 去读Excel的方法readExcel，该方法的入口参数为一个File对象
	public static List<List<String>> readExcel(File file) {
		try {
			// 创建输入流，读取Excel
			InputStream is = new FileInputStream(file.getAbsolutePath());
			// jxl提供的Workbook类
			Workbook wb = Workbook.getWorkbook(is);
			// Excel的页签数量
			// int sheet_size = wb.getNumberOfSheets();
			List<List<String>> outerList = new ArrayList<List<String>>();
			// 每个页签创建一个Sheet对象
			Sheet sheet = wb.getSheet(0);
			// sheet.getRows()返回该页的总行数
			for (int i = 0; i < sheet.getRows(); i++) {
				if (i == 0 || i == 1)
					continue;// 第一行为注释，第二行为标题
				List<String> innerList = new ArrayList<String>();
				// sheet.getColumns()返回该页的总列数
				for (int j = 0; j < sheet.getColumns(); j++) {
					String cellinfo = sheet.getCell(j, i).getContents();
					if (cellinfo.isEmpty())
						innerList.add("");
					else
						innerList.add(cellinfo.trim());
					System.out.print(cellinfo);
				}
				outerList.add(i - 2, innerList);
				Shop5941Application.out("");
			}
			return outerList;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
	// public static List<List<String>> RxcelImage(File excelFile,FileService
	// mFileService) throws Exception {
	// HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(excelFile));
	// HSSFSheet sheet = wb.getSheetAt(0);
	// List<List<String>> ls = new ArrayList<List<String>>();
	// int i=0;
	// int j=0;
	//
	// for (Row row : sheet) {
	// List<String> ll = new ArrayList<String>();
	// j=0;
	// for (Cell cell : row) {
	//
	// try {
	// ll.add(cell.getRichStringCellValue().getString());
	// } catch (Exception e) {
	// throw new
	// RunException("第"+(i+1)+"行"+(j+1)+"列错误，存在不合法数据，纯数字或保留字(true,false)");
	// }
	// j++;
	// }
	// i++;
	// ll.add("暂无图片");
	// ls.add(ll);
	// }
	// String filedate=new
	// SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
	// String basepath = MyParameter.TomcatFileImage+filedate+"/";
	// File file = new File(basepath);
	// // 如果文件夹不存在则创建
	// if (!file.exists() && !file.isDirectory()) {
	// file.mkdir();
	// }
	// ss s1 =findAllPictureDate(sheet);
	// Integer[] ikey =s1.getIkey();
	// Map<String, List<HSSFPictureData>> ivalue =s1.getIvalue();
	//
	// String hashString;
	// for (Integer integer : ikey) {
	// String paths="";
	// for (HSSFPictureData mHSSFPictureData : ivalue.get(integer+"")) {
	// byte[] uploadBytes = mHSSFPictureData.getData();
	// MessageDigest md5 = MessageDigest.getInstance("MD5");
	// uploadBytes = md5.digest(uploadBytes);
	// hashString = new BigInteger(1, uploadBytes).toString(16);
	// hashString = hashString.toUpperCase();
	// Map<String, Object> map =mFileService.getByparameter("md5", hashString);
	// if(map!=null&&map.get("PATH")!=null){
	// if(paths.indexOf(map.get("PATH").toString())==-1)
	// paths = paths+map.get("PATH").toString()+";";
	// }else{
	// String path =basepath+System.currentTimeMillis()+".png";
	// OutputStream out = new FileOutputStream(new File(path));
	// out.write(mHSSFPictureData.getData());
	// path= path.substring(2,path.length());
	// MyFile mf =new MyFile();
	// mf.setFiledate(filedate);
	// mf.setMd5(hashString);
	// mf.setPath(path);
	// mf.setSuffix("png");
	// mFileService.add(mf);
	// if(paths.indexOf(path)==-1)
	// paths=paths+path+";";
	// }
	// }
	// paths = paths.substring(0,paths.length()-1);
	// ls.get(integer).set(j, paths);
	// }
	//
	// ls.remove(0);
	// ls.remove(0);
	// return ls;
	// }
	// 去读Excel的方法readExcel，该方法的入口参数为一个File对象
	public static List<List<String>> RxcelImage(File excelFile, FileService mFileService) throws Exception {
		int[] a = {1000};
		return RxcelImage(excelFile, mFileService, 1, a);

	}
	private final static Logger mLogger= LoggerFactory.getLogger(ReadExcel.class);
	/***
	 * 多维图片
	 * 
	 * @param wd
	 *            维度，
	 * @param wds
	 *            每一个纬度的图片数量，
	 */
	public static List<List<String>> RxcelImage(File excelFile, FileService mFileService, int wd, int[] wds)
			throws Exception {
		List<MyFile> listfile = new ArrayList<>();
		if (wd != 1 && wds.length != wd)
			throw new RunException("图片维度和数目不相等");
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(excelFile));
		HSSFSheet sheet = wb.getSheetAt(0);
		// for (int i = 0; i < 3; i++) {
		// HSSFSheet sheet11 = wb.getSheetAt(i);
		// findAllPictureDate(sheet11);
		// }
		List<List<String>> ls = new ArrayList<List<String>>();
		int i = 0;
		int j = 0;
		int max = 0;

		for (Row row : sheet) {
			List<String> ll = new ArrayList<String>();
			if (j > max)
				max = j;
			j = 0;
			for (Cell cell : row) {
				try {
					if (!Stringutil.isBlank(cell.getRichStringCellValue().getString())) {
						ll.add(cell.getRichStringCellValue().getString());
						j++;
					}
				} catch (Exception e) {
					throw new RunException("第" + (i + 1) + "行" + (j + 1) + "列错误，存在不合法数据，纯数字或保留字(true,false)");
				}

			}
			i++;
			for (int k = 0; k < wd; k++) {
				ll.add("");
			}
			j = j + wd;
			ls.add(ll);
		}
		j = max;
		String filedate = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
		String basepath = MyParameter.TomcatFileImage + filedate + "/";
		File file = new File(basepath);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		List<XY> listxy = findAllPictureDate(sheet, wb, mFileService,listfile);
		for (XY xy : listxy) {
			xy.setList(Sort(xy.getList()));// 自定义排序算法
			int wdIndex = 0;
			int imageIndex = 0;
			
			mLogger.error("第 "+xy.getX()+" 行一共有 "+xy.getList().size()+" 张图片 分别是\n");
			
//			String s  = "";
//			List<Integer> Y1 = new ArrayList<>();  

			for (imageDtaa mimageDtaa : xy.getList()) {
//				boolean b =false;
//				for (Integer integer : Y1) {
//					if(integer==mimageDtaa.getY())
//						b=true;
//				}
//				if(b)continue;
//				else
//					Y1.add(mimageDtaa.getY());
				
				
//				mLogger.error(mimageDtaa.getImages());
//				s=s+mimageDtaa.getImages();
				
					String s =ls.get(xy.getX()).get(ls.get(xy.getX()).size() - (wd - wdIndex));

//					if(s.indexOf(mimageDtaa.getImages())==-1){
						s = s + mimageDtaa.getImages();
						ls.get(xy.getX()).set(ls.get(xy.getX()).size() - (wd - wdIndex),s);
//					}
					
					imageIndex++;
					if (imageIndex == wds[wdIndex]) {
						imageIndex = 0;// 索引置位0
						wdIndex++;// 维度向下偏移
						
					}
				
			
				
			}
			
			if (imageIndex != 0) {
				imageIndex = 0;// 索引置位0
				wdIndex++;// 维度向下偏移
			}

		}
		
		//去除注释和标题
		ls.remove(0);
		ls.remove(0);
		
		//去除最后一个分号
//		int sizea;
//		String str;
//		for (List<String> lsi:ls) {
//			sizea = lsi.size();
//			if(sizea<=wd)continue;
//			for (int a = 1;a<wd+1;a++) {
//				str = lsi.get(sizea-a);
//				if(!str.substring(str.length()-1, str.length()).equals(";"))continue;
//				lsi.set(sizea-a, str.substring(0,str.length()-1));
//			}
//		}
		return ls;
	}

	/**
	 * wd,图片维度 wds,偏移量
	 * 
	 * @throws Exception
	 */
	public static List<XY> findAllPictureDate(HSSFSheet sheet, HSSFWorkbook workbook, FileService mFileService,List<MyFile> listfile)
			throws Exception {
		List<XY> listXY = new ArrayList<>();
		List<HSSFPictureData> pictures = workbook.getAllPictures();
//		mLogger.error("总共有"+pictures.size()+"张图片");
		if (pictures.size() != 0) {
			List<HSSFShape> listHSSFShape = sheet.getDrawingPatriarch().getChildren();
			for (HSSFShape shape : listHSSFShape) {
				HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
				boolean b = false;// 行是否已创建
				HSSFPicture pic = (HSSFPicture) shape;
				String path = saveimage(pictures.get(pic.getPictureIndex() - 1).getData(), mFileService,pictures.get(pic.getPictureIndex() - 1).suggestFileExtension(),listfile);// 保存图片
				imageDtaa mimageDtaa = new imageDtaa();
				mimageDtaa.setImages(path);
				mimageDtaa.setY(anchor.getCol1());
				int size = listXY.size();
				List<imageDtaa> listdata;
				for (int i = 0; i < size; i++) {
					if (listXY.get(i).getX() == anchor.getRow1()) {
						listdata = listXY.get(i).getList();
						listdata.add(mimageDtaa);
						listXY.get(i).setList(listdata);
						b = true;
						break;
					}
				}
				if (!b) {
					XY mxy = new XY();
					mxy.setX(anchor.getRow1());
					listdata = new ArrayList<>();
					listdata.add(mimageDtaa);
					mxy.setList(listdata);
					listXY.add(mxy);
				}

			}
		}

		return listXY;
	}

//	static String filedate = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
	
//	private synchronized static String getmd5(byte[] uploadBytes) throws Exception {
//		String hashString;
//		MessageDigest md5 = MessageDigest.getInstance("MD5");
//		byte[] uploadBytes1 = Arrays.copyOf(uploadBytes, uploadBytes.length);
//		StringBuilder sb = new StringBuilder(uploadBytes.length);
//		for (byte b : uploadBytes1) {
//			sb.append(DESUtils.password((~b)+"")+"dsgx5674/#");
//		}
//		sb.append(sb.length());
//		sb.append(uploadBytes.length);
//		uploadBytes1=	md5.digest(sb.toString().getBytes());
//		hashString = new BigInteger(1, uploadBytes1).toString(16);
////		hashString = hashString.toUpperCase();
//		
//		return hashString;
//
//	}
//	private synchronized static String getpaths(List<MyFile> listfile,String md5) throws Exception {
//		for (MyFile mMyFile : listfile) {
//			if(mMyFile.getMd5().equals(md5)){
//				return mMyFile.getPath();
//			}
//		}
//		return "";
//		
//	}


	private static String saveimage(byte[] uploadBytes, FileService mFileService,String hz,List<MyFile> listfile) throws Exception {
//		Thread.sleep(1);
//		String path = MyParameter.TomcatFileImage +  "20180831/" + System.currentTimeMillis() + "."+hz;
//		 FileOutputStream out = new FileOutputStream(path);
//		out.write(uploadBytes);
//		out.close();
//		Application.out(path);
//		return path.substring(2, path.length())+";";
		String filedate = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
	
		String fileMd5 = mFileService.getFileMd5(uploadBytes);
//		String paths = getpaths(listfile, fileMd5);
		String paths ;
		
//		if(!Stringutil.isBlank(paths))return paths+";";
		
//		Map<String, Object> map = mFileService.getByparameter("md5", fileMd5);
//		if (map != null && map.get("PATH") != null) {
////			if (paths.indexOf(map.get("PATH").toString()) == -1)
//				paths = map.get("PATH").toString() + ";";
//		} else {
			String path = MyParameter.TomcatFileImage + filedate + "/" + System.currentTimeMillis() + "."+hz;
			 FileOutputStream out = new FileOutputStream(path);
			out.write(uploadBytes);
			out.close();
			path = path.substring(2, path.length());
			MyFile mf = new MyFile();
			mf.setFiledate(filedate);
			mf.setMd5(fileMd5);
			mf.setPath(path);
			mf.setSuffix(hz);
			mFileService.add(mf);
			listfile.add(mf);
//			if (paths.indexOf(path) == -1)
				paths =  path + ";";
//		}
		return paths;

	}

	private static class XY {
		private int X;// 图片x坐标
		private List<imageDtaa> list;// 图片

		public int getX() {
			return X;
		}

		public void setX(int x) {
			X = x;
		}

		public List<imageDtaa> getList() {
			return list;
		}

		public void setList(List<imageDtaa> list) {
			this.list = list;
		}

	}

	private static class imageDtaa {
		private int Y;// 图片Y坐标
		private String images;// 图片

		public int getY() {
			return Y;
		}

		public void setY(int y) {
			Y = y;
		}

		public String getImages() {
			return images;
		}

		public void setImages(String images) {
			this.images = images;
		}

	}

	/**
	 * 通过对imageDtaa的Y坐标进行排序
	 */
	public static List<imageDtaa> Sort(List<imageDtaa> list) {
		imageDtaa temp;
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = 0; j < size - 1 - i; j++) {
				if (list.get(j).getY() > list.get(j + 1).getY()) // 交换两数位置
				{
					temp = list.get(j);

					list.set(j, list.get(j + 1));
					list.set(j + 1, temp);
				}
			}
		}
		return list;
	}

}
