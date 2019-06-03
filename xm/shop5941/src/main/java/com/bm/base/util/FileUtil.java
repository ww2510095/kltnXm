package com.bm.base.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.file.FileService;
import com.bm.file.MyFile;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

public final class FileUtil extends BaseController	{

	// // 临时目录名缺省值
	// private static String tempPath ="view\\upload\\temp";
	// // 真实目录名缺省值
	// private static String filePath ="view\\upload\\file";
	// 允许的文件类型 缺省值
	private static String fileType = "txt,doc,docx,xls,xlsx,ppt,pptx,zip,rar";
	// 允许上传文件最大大小(M) 缺省值
	private static String maxSize = "10000";
	// 根物理路径
	private static String realPathRoot;


	/**
	 * 上传文件文件，返回文件路径,多个文件路径已;号隔开
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	public static List<FileBen> doFileUpload(StandardMultipartHttpServletRequest request,FileService mFileService)
			throws Exception {
		List<FileBen> ls = new ArrayList<FileBen>();

		// 获取上传的文件集合
		Iterator<String> iterator = request.getFileNames();
		while (iterator.hasNext()) {
			FileBen fl = new FileBen();
			fl.setKey(iterator.next());
			fl.setPath(saveMultipartFile(request.getFile(fl.getKey()),mFileService));
			ls.add(fl);
		}
		return ls;
	}

	/**
	 * 只上传第一个文件，忽略文件名
	 * @throws Exception 
	 */
	public static String doFileUploadOne(StandardMultipartHttpServletRequest request,FileService mFileService)
			throws Exception {
		// 获取上传的文件集合
		Iterator<String> iterator = request.getFileNames();
		if (iterator.hasNext())
			return saveMultipartFile(request.getFile(iterator.next()),mFileService);
		return null;
	}

	/**
	 * 保存文件
	 * @throws Exception 
	 */
	public static String saveMultipartFile(MultipartFile multifile,FileService mFileService) throws Exception {
		return saveMultipartFile(multifile, mFileService,null);
		}

	
	/*
	 * Java文件操作 获取文件扩展名
	 *
	 *  Created on: 2011-8-2
	 *      Author: blueeagle
	 */
	    public static String getExtensionName(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length() - 1))) { 
	                return filename.substring(dot + 1); 
	            } 
	        } 
	        return filename; 
	    } 

	/**
	 * 保存文件
	 * @throws Exception 
	 */
	public static String saveMultipartFile(MultipartFile multifile,FileService mFileService,String path ) throws Exception {
		byte[] uploadBytes = multifile.getBytes();
		String extension=getExtensionName(multifile.getOriginalFilename());
		String hashString;
		if ("jpg".equals(extension) || "png".equals(extension) || "jpeg".equals(extension)
				|| "bm".equals(extension) || "gif".equals(extension))
			hashString=mFileService.getFileMd5(uploadBytes);
		else
			hashString=mFileService.getMd5(uploadBytes);
		 Map<String, Object> map =mFileService.getByparameter("md5", hashString);
		    if(map!=null&&map.get("PATH")!=null)
		    	return map.get("PATH").toString();//如果文件已经存在，则不保存文件
		  
		StringBuffer sbUrlPath = new StringBuffer();
		
		
			// 文件名
			String fileName = multifile.getOriginalFilename();
			// 检查文件大小
			if (multifile.getSize() > Integer.valueOf(maxSize) * 1024 * 1024) {
				// 提示错误信息:超过大小限制
				throw new RunException("文件超出最大限制");
			}
			// 检查文件后缀格式
			String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

			if ("jpg".equals(fileExtName) || "png".equals(fileExtName) || "jpeg".equals(fileExtName)
					|| "bm".equals(fileExtName) || "gif".equals(fileExtName))
				realPathRoot = MyParameter.TomcatFileImage;// 图片
			else {
				// 其他文件
				if (fileType != null && !"".equals(fileType.trim())) {
					boolean isRealType = false;
					String[] arrType = fileType.split(",");
					for (String str : arrType) {
						if (fileExtName.equals(str.toLowerCase())) {
							isRealType = true;
							break;
						}
					}
					if (!isRealType) {
						// 提示错误信息:文件格式不正确
						throw new RunException("上传失败:不支持的文件格式");
					}
				}
				realPathRoot = MyParameter.TomcatFile;
			}
			String date = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
			File file = new File(realPathRoot, date);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}

			// 创建文件唯一名称
			String newfileName = System.currentTimeMillis() + "";
			String filepath = realPathRoot + date + "/" + newfileName + "." + fileExtName + ";";
			sbUrlPath.append(filepath.substring(2, filepath.length()));
			// 写入文件
			if(Stringutil.isBlank(path))
				file = new File(filepath.substring(0, filepath.length() - 1));
			else
				file = new File(path);
				
			multifile.transferTo(file);
			 //Thumbnails.of(file.toString()).scale(0.25f).toFile(file.toString());
			//md5不为空,记录
			if(!Stringutil.isBlank(hashString)){
				MyFile mMyFile = new MyFile();
				mMyFile.setPath(sbUrlPath.substring(0, sbUrlPath.length() - 1));
				mMyFile.setMd5(hashString);
				mMyFile.setFiledate(date);
				mMyFile.setSuffix(fileExtName);
				mFileService.add(mMyFile);
			}
		
		
		return sbUrlPath.substring(0, sbUrlPath.length() - 1);
	}

	/**
	 * 上传单个文件，根据文件名字获取，如果文件文字为空抛出异常
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	public static String doFileUploadByName(StandardMultipartHttpServletRequest request, String filename,FileService mFileService)
			throws Exception {
		if (Stringutil.isBlank(filename))
			throw new RunException("文件名为空");
		return saveMultipartFile(request.getFile(filename),mFileService);

	}
	public static class FileBen {
		private String key;
		private String path;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}

}
