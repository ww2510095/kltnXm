package com.bm.file;


import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.Shop5941Application;
import com.bm.base.Base64;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.PictureChangeSize;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "文件管理")
public class FileController extends BaseController {

	
	/**
	 * 文件急速秒传技术，对文件md5(大写)查重，已有文件不上传，直接引用文件
	 */
	@RequestMapping(value = "/feile/selectbymd5", method = RequestMethod.POST)
	public RequestType selectbymd5(String md5) throws Exception {
		String s =filerepeat(md5);
		if(s!=null) return sendTrueData(md5);
		return sendFalse("...");
	}
	/**
	 *对指定文件进行压缩，返回压缩文件路径
	 */
	@RequestMapping(value = "/feile/compressImage", method = RequestMethod.POST)
	public RequestType compressImage(String path,int w,int h) throws Exception {
		path = MyParameter.TomcatSD+path;
		path = path.replace("//", "/");
		String[] sa = path.split("/");
		String path1 =  MyParameter.TomcatFileImage+"icon/"+sa[sa.length-1];
		try {
			PictureChangeSize.compressImage(path,path1, w, h);
		} catch (Exception e) {
			File file = new File(MyParameter.TomcatFileImage, "icon");
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			PictureChangeSize.compressImage(path, path1, w, h);
		}
		
		
		return sendTrueData(path1.substring(2,path1.length()));
	}
	/**
	 * 文件队列上传
	 */
	@RequestMapping(value = "/feile/addList", method = RequestMethod.POST)
	public RequestType addList(HttpServletRequest req) throws Exception {
		if (req instanceof StandardMultipartHttpServletRequest)
			return sendTrueData(FileUtil.doFileUpload((StandardMultipartHttpServletRequest) req, mFileService));
		return sendFalse("");
	}
	/**
	 * base64上传
	 */
	@RequestMapping(value = "/feile/base64Image", method = RequestMethod.POST)
	public RequestType base64Image(String image64) throws Exception {
		if(Stringutil.isBlank(image64))return sendFalse("image64不可为空");
		return sendTrueData(Base64.Base64ToImage(mFileService, image64));
	}
	/**
	 * 列出某一日上传的图片,必须是完整的数字年月日，如20180609
	 * 
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/feile/selectbydate", method = RequestMethod.POST)
	public RequestType selectbydate(String filedate,String suffix,Integer page,Integer rows) throws Exception {
		MyFile my = new MyFile();
		my.setFiledate(filedate);
		my.setSuffix(suffix);
		return sendTrueData(mFileService.getALL(my, page, rows));
	}
	
	/**
	 * 删除文件
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/feile/delete", method = RequestMethod.POST)
	public RequestType delete(Long id) throws Exception {
		Map<String, Object> map = mFileService.getById(id);
		if(map==null) return sendFalse("文件不存在");
		
		Runtime.getRuntime().gc();
		
		if(deletea(MyParameter.TomcatSD+map.get("PATH").toString()))
		return sendTrueData("删除成功");
		
		return sendTrueData("删除失败，文件被占用");
	}
	public  boolean deletea(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
			Shop5941Application.out("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
        	return deleteFile(fileName);
                
        }
    }
	public  boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
				Shop5941Application.out("删除单个文件" + fileName + "成功！");
                return true;
            } else {
				Shop5941Application.out("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
			Shop5941Application.out("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
	/**
	 * 修改文件
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/feile/update", method = RequestMethod.POST)
	public RequestType update(Integer id,HttpServletRequest req) throws Exception {
		if(id==null) throw new RunException("编号错误");
		Map<String, Object> map =mFileService.getById(id);
		if(map==null||map.get("PATH")==null)throw new RunException("编号错误");
		
		if(!(req instanceof StandardMultipartHttpServletRequest))throw new RunException("未发现文件");
		MultipartFile multifile = ((StandardMultipartHttpServletRequest) req).getFile("a");
		if(multifile==null)throw new RunException("未发现文件");
		
		FileUtil.saveMultipartFile(multifile, mFileService, map.get("PATH").toString());
		return sendTrueMsg("修改成功");
	}


}
