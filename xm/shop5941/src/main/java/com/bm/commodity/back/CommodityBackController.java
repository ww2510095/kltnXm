package com.bm.commodity.back;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.Shop5941Application;
import com.bm.base.excle.ZIP;
import com.bm.base.util.MyDate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.excle.ReadExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.interceptor.Auth.Administration;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.commodity.Commodity;
import com.bm.commodity.CommodityService;
import com.bm.commodity.SpecificationsService;
import com.bm.stock.StockService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "批量操作商品")
public class CommodityBackController extends BaseController {

	@Autowired
	private CommodityBackService mCommodityBackService;
	@Autowired
	private CommodityService mCommodityService;
	@Autowired
	private SpecificationsService mSpecificationsService;
	@Autowired
	private StockService mStockService;

	/**
	 * 批量导入商品
	 */
	@Auth(commodityback = { Administration.ADD })
	@RequestMapping(value = "/commodityback/readCommodityExcel", method = RequestMethod.POST)
	@Transactional
	public RequestType readShopExcel(HttpServletRequest req, String uname) throws Exception {
		String systemtype = getMember(getLogin(uname).getUserid()).getUsersystem();
		long time = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.RxcelImage(file, mFileService);// 解读excel
			ReadCommodityExcel mCommodityBackExcel = new ReadCommodityExcel();
			int i = 0;
			Long ida = System.currentTimeMillis();
			Sql msql = new Sql();
			msql.setSql("select nvl(max(nvl(indexsby,-1)),0) indexsby from Commoditykey ");
			int indexby = Integer.parseInt(mCommodityService.exeSelectSql(msql).get(0).get("INDEXSBY").toString());
			if (getMember(getLogin(uname).getUserid()).getSuperadmin() == 1) {
				List<Commodity> l = mCommodityBackExcel.readCommodityadmin(lls);
//				Commodity commodity1;
				for (Commodity commodity : l) {
					String mcode=commodity.getMycode();
					commodity.setMycode(null);
					ida++;
					//管理员直接导入正式表
					i++;
					String image=commodity.getMainimage();
					commodity.setMainimage(null);
					
					if(commodity.getDetailsimage()!=null&&commodity.getDetailsimage().length()<2)
						commodity.setDetailsimage(null);
					
					
//					commodity1 = IBeanUtil.Map2JavaBean(
//							mCommodityService.getByparameter("youcode", commodity.getYoucode()), Commodity.class);
//					if (commodity1 != null) {
//						commodity.setId(commodity1.getId());
//						mCommodityService.updateBySelect(commodity);
//					} else {
					try {
			
						msql.setSql("select * from Commoditykey where id="+commodity.getName());
						Commodity commodity1=IBeanUtil.Map2JavaBean(mCommodityService.exeSelectSql(msql).get(0), Commodity.class);
						if(commodity1!=null){
							commodity1=new Commodity();
							commodity1.setId(Long.valueOf(commodity.getName()));
							commodity1.setDetailsimage(image);
							mCommodityService.updateBySelect(commodity1);
							
						
						}else{
							commodity.setIndexs("热卖推荐");
							commodity.setIndexsby(indexby);
							indexby=indexby+1;
							
							commodity.setMainimage(image);
							commodity.setId(ida);
							commodity.setMycode(mcode);
							mCommodityService.add(commodity,i+2);
							commodity.setSystem(systemtype);// 添加系统
						
						}
					} catch (Exception e) {

						commodity.setIndexs("热卖推荐");
						commodity.setIndexsby(indexby);
						indexby=indexby+1;
						

						commodity.setMainimage(image);
						commodity.setId(ida);
						commodity.setMycode(mcode);
						mCommodityService.add(commodity,i+2);
						commodity.setSystem(systemtype);// 添加系统
					
					
					}
				
//					@SuppressWarnings("unchecked")
//					List<Commodity> list = (List<Commodity>) mCommodityService.getALL(commodity);
//					if(list.size()==0){
//						commodity.setMainimage(image);
//						commodity.setId(ida);
//						commodity.setMycode(mcode);
//						mCommodityService.add(commodity,i+2);
//						commodity.setSystem(systemtype);// 添加系统
//					}
//						
//					else{
//						commodity.setDetailsimage(image);
//						mCommodityService.updateBySelect(commodity);
//					}
//					}
				}
			} else {
				List<Commodityback> l = mCommodityBackExcel.readCommodity(lls, uname);
				Commodityback commodity1;
				for (Commodityback commodity : l) {
					//非管理员导入临时表
					i++;
					commodity1 = IBeanUtil.Map2JavaBean(
							mCommodityBackService.getByparameter("youcode", commodity.getYoucode()), Commodityback.class);
					if (commodity1 != null) {
						commodity.setId(commodity1.getId());
						mCommodityBackService.updateBySelect(commodity);
					} else {
						mCommodityBackService.add(commodity);
					}
				}

			}

			return sendTrueMsg("导入成功，此次一共导入" + i + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	/**
	 * 批量导入商品2
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/commodityback/readCommodityExcelEX", method = RequestMethod.POST)
	@Transactional
	@ApiOperation(value = "新的导入方式，一次上传所有的商品资料，图片除外", response = RequestType.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uname", value = "当前登录账户", dataType = "String", required = true,paramType="query"),
			@ApiImplicitParam(name = "UID", value = "设备号", dataType = "String", required = true,paramType="query"),
			@ApiImplicitParam(name = "file", value = "Excel文件", dataType = "__file", required = true,paramType="form"),
	})
	public RequestType readShopExcelEX(HttpServletRequest req, String uname) throws Exception {
		long time = System.currentTimeMillis();
		Sql msql = new Sql();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			ReadCommodityExcel mCommodityBackExcel = new ReadCommodityExcel();
			int i = 0;
			int i2=0;
			Long ida = System.currentTimeMillis();
			List<Commodity> l = mCommodityBackExcel.readCommodityadmin2(lls);
			List<String> liststr =new ArrayList<>();
			for (Commodity commodity : l) {
				commodity.setId(ida);
				if(liststr.contains(commodity.getCode()))continue;
				liststr.add(commodity.getCode());
				i++;
				ida=ida+1;

				mCommodityService.add(commodity);
			}
			List<CK> l2 = mCommodityBackExcel.readSpecifications2(lls,l);
			for (CK mck : l2) {
				i2++;
				if(mSpecificationsService.getByparameter("youcode", mck.getmCommodity().getYoucode())!=null)
					throw new RunException("错误，条码"+mck.getmCommodity().getYoucode()+"已经存在");
				int a =0;
				while (true) {
					try {
						String su=mck.getmCommodity().getSupplier();
						mck.getmCommodity().setId(ida);
						mck.getmCommodity().setSupplier(null);
						mSpecificationsService.add(mck.getmCommodity());
						
						msql.setSql("select code from shop where memberid="+getMember(su).getId());
						try {
							mck.getmStock().setShopcode(mMemberService.exeSelectSql(msql).get(0).get("CODE").toString());
						} catch (Exception e) {
							throw new RunException("错误，条码"+mck.getmStock().getCode()+"所对应的供应商不存在，请检查");
						}
						
						mck.getmStock().setId(ida);
						ida=ida+1;
						mStockService.add(mck.getmStock());
						break;
					} catch (Exception e) {
						ida=ida+1;
						a++;
						if(a==100)
							throw new RunException("请稍后再试");
					}
					
				}
				
				
			}
		
			
			return sendTrueMsg("导入成功，此次一共导入" + i + "款商品，"+i2+"个条码！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}
		
		return sendFalse("未发现文件");
	}
	
	/**
	 * 导入最新库存
	 * */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/commodityback/imageEx", method = RequestMethod.POST)
	public RequestType imageEx(HttpServletRequest req, String uname) throws Exception {
		long time = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.RxcelImage(file,mFileService);// 解读excel
			Sql msql = new Sql();
			for (List<String> list : lls) {
				msql.setSql("select id,mainimage,detailsimage from Commoditykey where mainimage='"+list.get(0)+"'");
				Commodity listCommodity = null;
				try {
					listCommodity=IBeanUtil.Map2JavaBean(mMemberService.exeSelectSql(msql).get(0), Commodity.class);
				} catch (Exception e) {
					throw new RunException("错误，商城助记码"+list.get(0)+"不存在，请检查");
				}
				if(listCommodity==null)
					throw new RunException("错误，商城助记码"+list.get(0)+"不存在，请检查");
				String[] strs = list.get(2).split(";");
				int smsize=Integer.valueOf(list.get(1));
				if(strs.length<smsize)
					throw new RunException("错误，商城助记码"+list.get(0)+"所对应的列只有"+strs.length+"张图片，但主图却有"+smsize+"张，请检查");
			String sm="";
			String dm="";
			for (int i=0;i<strs.length;i++) {
				if(i<smsize)
					sm=sm+";"+strs[i];
				else
					dm=dm+";"+strs[i];
			}
			if(!Stringutil.isBlank(sm))sm=sm.substring(1,sm.length());
			if(!Stringutil.isBlank(dm))dm=dm.substring(1,sm.length());
			listCommodity.setMainimage(sm);
			listCommodity.setDetailsimage(dm);
			mCommodityService.updateBySelect(listCommodity);
		}
			
			return sendTrueMsg("导入成功，此次一共导入" + lls.size() + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	/**
	 * 批量导入规格
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/commodity/readCommodityExcel", method = RequestMethod.POST)
	@Transactional
	public RequestType commodityreadShopExcel(HttpServletRequest req, String uname) throws Exception {
		long time = System.currentTimeMillis();
		Long ida =  System.currentTimeMillis();;
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			
			ReadCommodityExcel mCommodityBackExcel = new ReadCommodityExcel();
			List<Commodity> l = mCommodityBackExcel.readSpecifications(lls);
			int i=0;
			for (Commodity commodity : l) {
				i++;
				if(mSpecificationsService.getByparameter("youcode", commodity.getYoucode())!=null)
					throw new RunException("错误，条码"+commodity.getYoucode()+"已经存在");
				int a =0;
				while (true) {
					try {
						commodity.setId(ida);
						mSpecificationsService.add(commodity);
						break;
					} catch (Exception e) {
						ida=ida+1;
						a++;
						if(a==100)
							throw new RunException("请稍后再试");
					}
					
				}
				
				
			}
			
			return sendTrueMsg("导入成功，此次一共导入" + i + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}
		
		return
				
				sendFalse("未发现文件");
	}

	/**
	 * 批量修改
	 */
	@Auth(commodityback = { Administration.UPDATE })
	@Transactional
	@RequestMapping(value = "/commodityback/update", method = RequestMethod.POST)
	public RequestType update(String JsonArray) throws Exception {
		List<Commodityback> lsh = GsonUtil.fromJsonList(JsonArray, Commodityback.class);
		if (lsh.size() == 0)
			sendFalse("数据错误或为空");
		for (Commodityback mCommodityback : lsh) {
			mCommodityBackService.updateBySelect(mCommodityback);
		}
		return sendTrueMsg("修改成功");
	}

	/**
	 * 批量删除
	 */
	@Auth(Shopback = { Administration.DELETE })
	@Transactional
	@RequestMapping(value = "/commodityback/delete", method = RequestMethod.POST)
	public RequestType delete(String JsonArray) throws Exception {
		List<Commodityback> lsh = GsonUtil.fromJsonList(JsonArray, Commodityback.class);
		if (lsh.size() == 0)
			sendFalse("数据错误或为空");
		for (Commodityback mCommodityback : lsh) {
			mCommodityBackService.deleteByid(mCommodityback);
		}
		return sendTrueMsg("删除成功");
	}

	/**
	 * 查询自己导入数据
	 */
	@Auth(Shopback = { Administration.SELECT })
	@RequestMapping(value = "/commodityback/selectall", method = RequestMethod.POST)
	public RequestType selectall(String uname, Integer page, Integer rows) throws Exception {
		Commodityback sh = new Commodityback();
		sh.setAdminphone(uname);
		// return
		// sendTrueData(IBeanUtil.ListMap2ListJavaBean(mCommodityBackService.getALL(sh,
		// page, rows), Commodityback.class));
		return sendTrueData(mCommodityBackService.getALL(sh, page, rows));
	}

	/**
	 * 批量保存数据
	 */
	@Auth(admin = true)
	@Transactional
	@RequestMapping(value = "/commodityback/save", method = RequestMethod.POST)
	public RequestType save(String JsonArray) throws Exception {
		List<Commodity> lsh = GsonUtil.fromJsonList(JsonArray, Commodity.class);
		if (lsh.size() == 0)
			sendFalse("数据错误或为空");

		for (Commodity mCommodity : lsh) {
			mCommodityBackService.deleteByid(mCommodity);
		}
		mCommodityService.addList(lsh);
		return sendTrueMsg("保存成功");
	}


	private  List<ZipKey> updateZipKey(List<ZipKey> mlistZipKey,String path){
		String[] a = path.split(".");
		if(a.length!=0){
			if(a[a.length-1].equals("db"))
				return mlistZipKey;
		}
		List<String> strs = Arrays.asList(path.split("/"));
		if(strs.size()<5)return mlistZipKey;
		for (ZipKey mZipKey :mlistZipKey){
			if(strs.contains(mZipKey.getKey())){
				if(strs.contains("detailsimage"))
					mZipKey.setDetailsimage(mZipKey.getDetailsimage()+path.substring(2, path.length())+";");
				else
					mZipKey.setMainimage(mZipKey.getMainimage()+path.substring(2, path.length())+";");

				return mlistZipKey;
			}
		}
		ZipKey mZipKey = new ZipKey();
		mZipKey.setKey(strs.get(4));
		if(strs.contains("detailsimage"))
			mZipKey.setDetailsimage(mZipKey.getDetailsimage()+path.substring(2, path.length())+";");
		else
			mZipKey.setMainimage(mZipKey.getMainimage()+path.substring(2, path.length())+";");
		mlistZipKey.add(mZipKey);
		return mlistZipKey;
	}


	/**
	 * 批量导入图片压缩包
	 */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/commodityback/readImagesZip", method = RequestMethod.POST)
	@ApiOperation(value = "批量导入图片，只能是zip格式，详情图叫detailsimage", response = RequestType.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uname", value = "当前登录账户", dataType = "String", required = true,paramType="query"),
			@ApiImplicitParam(name = "UID", value = "设备号", dataType = "String", required = true,paramType="query"),
			@ApiImplicitParam(name = "file", value = "ZIP压缩包", dataType = "__file", required = true,paramType="form"),
	})
	public RequestType readImagesZip(HttpServletRequest req) throws Exception {
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器

			File[] li = ZIP.unzip(MyParameter.TomcatSD + s, MyParameter.TomcatFileImage,null);
			List<ZipKey> mlist = new ArrayList<>();
			for (File file : li) {
				String st = file.toString().replace("\\","/");
				mlist=updateZipKey(mlist,st);

			}
			Long time = MyDate.Dtae_star()-86400000L;
			for(ZipKey mZipKey :mlist){
				Sql msql = new Sql();
				msql.setSql("update commoditykey set   ");
				if(!Stringutil.isBlank(mZipKey.getMainimage()))
					msql.setSql(msql.getSql()+" mainimage='"+mZipKey.getMainimage()+"',");
				if(!Stringutil.isBlank(mZipKey.getDetailsimage()))
					msql.setSql(msql.getSql()+" detailsimage='"+mZipKey.getDetailsimage()+"'");

				msql.setSql(msql.getSql()+"  where code='"+mZipKey.getKey()+"' and id>"+time);

				
				mCommodityService.execSQL(msql);
			}


			return sendTrueMsg("修改成功");
		}

		return sendFalse("未发现文件");
	}


}
