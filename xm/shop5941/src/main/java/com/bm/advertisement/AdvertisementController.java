package com.bm.advertisement;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.excle.ReadExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.myjar.Stringutil;

@RestController
@Api(tags = "图片模块")
public class AdvertisementController extends BaseController{
	@Autowired
	private AdvertisementService mAdvertisementService;
	
	

	/**
	 * 添加广告图片
	 * */
	@RequestMapping(value ="/Advertisement/add", method = RequestMethod.POST) 
	@Auth(admin=true)
	public RequestType add(Advertisement mAdvertisement, HttpServletRequest req) throws Exception{
		if (Stringutil.isBlank(mAdvertisement.getPath())) {
			if (req instanceof StandardMultipartHttpServletRequest)
				mAdvertisement.setPath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
		}
		if (Stringutil.isBlank(mAdvertisement.getPath())) return sendFalse("图片不可为空");
		if (Stringutil.isBlank(mAdvertisement.getKey())) return sendFalse("请求标识不可为空");
		mAdvertisementService.add(mAdvertisement);
		return sendTrueMsg("添加成功");
	}
		
		/**
		 * 修改广告图片
		 * */
		@RequestMapping(value ="/Advertisement/update", method = RequestMethod.POST) 
		@Auth(admin=true)
		public RequestType update(Advertisement mAdvertisement, HttpServletRequest req) throws Exception{
			try {
				if (Stringutil.isBlank(mAdvertisement.getPath())) {
					if (req instanceof StandardMultipartHttpServletRequest)
						mAdvertisement.setPath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
				}
			} catch (Exception e) {
			}
			
			mAdvertisementService.updateBySelect(mAdvertisement);
			return sendTrueMsg("修改成功");
			
	}
		/**
		 * 修改大类广告图
		 * */
		@RequestMapping(value ="/Advertisement/largeclassupdate", method = RequestMethod.POST) 
		@Auth(admin=true)
		public RequestType largeclassupdate(Advertisement mAdvertisement, HttpServletRequest req) throws Exception{
			if(Stringutil.isBlank(mAdvertisement.getDescribe()))return sendFalse("大类类名不可为空");
			if (Stringutil.isBlank(mAdvertisement.getPath())) {
				if (req instanceof StandardMultipartHttpServletRequest)
					mAdvertisement.setPath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
			}
			mAdvertisement.setKey("-1");
			mAdvertisementService.updateBySelect(mAdvertisement);
			return sendTrueMsg("修改成功");
			
		}
		/**
		 * 修改细类广告图
		 * */
		@RequestMapping(value ="/Advertisement/fineclassupdate", method = RequestMethod.POST) 
		@Auth(admin=true)
		public RequestType fineclassupdate(Advertisement mAdvertisement, HttpServletRequest req) throws Exception{
			if(Stringutil.isBlank(mAdvertisement.getDescribe()))return sendFalse("大类类名不可为空");
			if(Stringutil.isBlank(mAdvertisement.getKey()))return sendFalse("细类类名不可为空");
			if (Stringutil.isBlank(mAdvertisement.getPath())) {
				try {
					if (req instanceof StandardMultipartHttpServletRequest)
						mAdvertisement.setPath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
				} catch (Exception e) {
				}
				
			}
			mAdvertisementService.updateBySelect(mAdvertisement);
			return sendTrueMsg("修改成功");
			
		}
		/**
		 * 删除广告图片
		 * */
		@RequestMapping(value ="/Advertisement/delete", method = RequestMethod.POST) 
		@Auth(admin=true)
		public RequestType delete(Advertisement mAdvertisement, HttpServletRequest req) throws Exception{
			mAdvertisementService.deleteByid(mAdvertisement);
			return sendTrueMsg("删除成功");
			
		}
		/**
		 * 查看广告图片
		 * */
		@RequestMapping(value ="/Advertisement/select", method = RequestMethod.POST) 
		public RequestType select(Advertisement mAdvertisement, HttpServletRequest req,Integer page,Integer rows) throws Exception{
			return sendTrueData(mAdvertisementService.getALL(mAdvertisement,page,rows,1));
		}
	
		/**
		 * 导入大类广告图
		 */
		@Auth(admin=true)
		@RequestMapping(value = "/Advertisement/maxExcel", method = RequestMethod.POST)
		public RequestType maxExcel(HttpServletRequest req, String uname) throws Exception {
			Long time = System.currentTimeMillis();
			if (req instanceof StandardMultipartHttpServletRequest) {
				String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
				File file = new File(MyParameter.TomcatSD + s);
				List<List<String>> lls = ReadExcel.RxcelImage(file, mFileService);// 解读excel
				int size = lls.size();
				Sql msql = new Sql();
				int add=0;
				int update=0;
				Long ida = System.currentTimeMillis();
				for (int i = 0; i < size; i++) {
					ida++;
					//大类图片
					msql.setSql("select id,describe from Advertisement where describe='"+lls.get(i).get(0)+"'");
					List<Map<String, Object>> listmap = mAdvertisementService.exeSelectSql(msql);
					if(listmap.size()==0){
						msql.setSql("INSERT INTO Advertisement (id,describe,key,bys,path) values("+ida+",'"+lls.get(i).get(0)+"',-1,0,+'"+lls.get(i).get(1)+"')");
						mAdvertisementService.execSQL(msql,-1,"");
						add++;
					}else{
//						msql.setSql("update Advertisement set path='"+lls.get(1)+"' where id = "+listmap.get(0).get("ID").toString());
//						mAdvertisementService.execSQL(msql,0,listmap.get(0).get("ID").toString());
						msql.setSql("update Advertisement set path='"+lls.get(i).get(1)+"' where id="+listmap.get(0).get("ID").toString());
						mAdvertisementService.execSQL(msql, -1, "");
						update++;
					}
				}

				return sendTrueMsg("导入成功，此次一共导入" +size + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒,其中增加:"+add+"条,修改:"+update+"条");
			}

			return sendFalse("未发现文件");
		}
		/**
		 * 导入细类图片
		 */
		@Auth(admin=true)
		@RequestMapping(value = "/Advertisement/minExcel", method = RequestMethod.POST)
		public RequestType minExcel(HttpServletRequest req, String uname) throws Exception {
			Long time = System.currentTimeMillis();
			if (req instanceof StandardMultipartHttpServletRequest) {
				String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
				File file = new File(MyParameter.TomcatSD + s);
 				List<List<String>> lls = ReadExcel.RxcelImage(file, mFileService);// 解读excel
				int size = lls.size();
				Sql msql = new Sql();
				int add=0;
				int update=0;
				Long ida =System.currentTimeMillis();
				for (int i = 0; i < size; i++) {
					ida++;
					msql.setSql("select id,describe from Advertisement where describe='"+lls.get(i).get(1)+"' and bys='"+lls.get(i).get(0)+"'");
					List<Map<String, Object>> listmap = mAdvertisementService.exeSelectSql(msql);
					if(listmap.size()==0){
						//细类小图片
						msql.setSql("INSERT INTO Advertisement (id,describe,bys,key,path) values("+ida+",'"+lls.get(i).get(1)+"','"+lls.get(i).get(0)+"','-3','"+lls.get(i).get(2)+"')");
						mAdvertisementService.execSQL(msql,-1,"");
//						//细类轮播图
						
//						msql.setSql("INSERT INTO Advertisement (id,describe,bys,key,path) values("+System.currentTimeMillis()+",'"+lls.get(i).get(1)+"','"+lls.get(i).get(0)+"','-2','"+lls.get(i).get(3)+"')");
//						mAdvertisementService.execSQL(msql,-1,"");
						add++;
					}else{
						msql.setSql("update Advertisement set path='"+lls.get(i).get(2)+"'  where id="+listmap.get(0).get("ID").toString());
						mAdvertisementService.execSQL(msql, -1, "");
						update++;
					}
				}
				
				return sendTrueMsg("导入成功，此次一共导入" +size + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒,其中增加:"+add+"条,"+update+"条已存在");
			}
			
			return sendFalse("未发现文件");
		}
	
}
