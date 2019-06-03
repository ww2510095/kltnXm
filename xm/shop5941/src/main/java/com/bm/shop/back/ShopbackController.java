package com.bm.shop.back;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.auths.MemberAuths;
import com.bm.auths.groupby.OrganizationService;
import com.bm.auths.organization.Organization;
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
import com.bm.clerk.Clerk;
import com.bm.clerk.ClerkService;
import com.bm.shop.Shop;
import com.bm.shop.ShopService;
import com.bm.user.Member;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "店铺批量接口")
public class ShopbackController extends BaseController {

	@Autowired
	private ShopbackService mShopbackService;
	@Autowired
	private ShopService mShopService;
	@Autowired
	private ClerkService mClerkService;
	@Autowired
	private OrganizationService mOrganizationService;

	/**
	 * 批量导入店铺
	 */
	@Auth(Shopback = { Administration.ADD })
	@RequestMapping(value = "/shopback/readShopExcel", method = RequestMethod.POST)
	public RequestType readShopExcel(HttpServletRequest req, String uname) throws Exception {
		String systemtype =getMember(getLogin(uname).getUserid()).getUsersystem();
		long time = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			int size = lls.size();
			for (int i = 0; i < size; i++) {
				lls.get(i).add(systemtype);
			}

			ShopBackExcel mShopBackExcel = new ShopBackExcel();
			int i = mShopbackService.addList(mShopBackExcel.readShop(lls, uname));
			
			return sendTrueMsg("导入成功，此次一共导入" + i + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	/**
	 * 批量导入店员
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/shopback/readClerk", method = RequestMethod.POST)
	@Transactional
	public RequestType readClerk(HttpServletRequest req, String uname) throws Exception {
		long time = System.currentTimeMillis();
		long id1 = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			List<Shop> listshop = new ArrayList<>();
			Shop sh;
			Member mMember;
			Sql msql = new Sql();
			
			for (int i=0;i<lls.size();i++) {
				if(lls.get(i).size()!=2) throw new RunException("第"+(i+3)+"行存在错误,数据长度不足或超出，请检查");
				sh=null;
				mMember = null;
				for (Shop mShop : listshop) {
					if(mShop.getCode().equals(lls.get(i).get(1))){
						sh =  mShop;
						break;
					}
				}
				if(sh==null){
					msql.setSql("select * from shop where code='"+lls.get(i).get(1)+"'");
					List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
					if(listmap.size()!=1)
						throw new RunException("第"+(i+3)+"行存在错误,未找到对应店铺,请检查");
					
					sh = IBeanUtil.Map2JavaBean(listmap.get(0), Shop.class);
					listshop.add(sh);
				}
				try {
					mMember = getMember(lls.get(i).get(0));
				} catch (Exception e) {

					Long id = System.currentTimeMillis();
					mMember = new Member();
					mMember.setUname(lls.get(i).get(0));
					mMember.setPassword("123456");
					mMember.setId(id);
					memberRegister(mMember,false);
					//推荐人
		    		msql.setSql("INSERT INTO Friends (id,memberida,memberidb,memberidatype) VALUES('"+id1+"',"+id+",'"+id+"','0')");
		    		mMemberService.execSQL(msql, -1, id+"");
				}
				//查询是否已经是电源
				msql.setSql("select id from clerk where memberid="+mMember.getId());
				if(mMemberService.exeSelectSql(msql).size()!=0)
					throw new RunException("第"+(i+3)+"行存在错误,改账号已经是别的店铺的店员了,请检查");
				
				//更改推荐人为自己
				msql.setSql("update Friends set memberida =memberidb where memberidb="+mMember.getId());
	    		mMemberService.execSQL(msql, -1, "");
	    		
	    		
	    		//店铺
//	    		msql.setSql("INSERT INTO clerk (id,shopid,memberid,state) VALUES('"+System.currentTimeMillis()+"',"+sh.getId()+",'"+mMember.getId()+"','1')");
//	    		mMemberService.execSQL(msql, -1, mMember.getId()+"");
	    		Clerk mClerk= new Clerk();
	    		mClerk.setMemberid(mMember.getId());
	    		mClerk.setShopid(sh.getId());
	    		mClerk.setState(1);
	    		mClerk.setId(id1);
	    		mClerkService.add(mClerk);
	    		//权限
				Organization mOrganization = new Organization();
				mOrganization.setGroupbyname("店员");
				mOrganization.setMemberid(mMember.getId());
				mOrganization.setId(id1);
				mOrganizationService.add(mOrganization);
				
				id1=id1+1;
			}
			
			
			
			return sendTrueMsg("导入成功，此次一共导入" + lls.size() + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}
		
		return sendFalse("未发现文件");
	}

	/**
	 * 批量修改
	 */
	@Auth(Shopback = { Administration.UPDATE })
	@Transactional
	@RequestMapping(value = "/shopback/update", method = RequestMethod.POST)
	public RequestType update(String JsonArray) throws Exception {
		List<Shopback> lsh = GsonUtil.fromJsonList(JsonArray, Shopback.class);
		if (lsh.size() == 0)
			sendFalse("数据错误或为空");
		for (Shopback shopback : lsh) {
			mShopbackService.updateBySelect(shopback);
		}
		return sendTrueMsg("修改成功");
	}

	/**
	 * 批量删除
	 */
	@Auth(Shopback = { Administration.DELETE })
	@Transactional
	@RequestMapping(value = "/shopback/delete", method = RequestMethod.POST)
	public RequestType delete(String JsonArray) throws Exception {
		List<Long> lsh = GsonUtil.fromJsonList(JsonArray, Long.class);
		if (lsh.size() == 0)
			sendFalse("数据错误或为空");
		for (Long shopback : lsh) {
			mShopbackService.deleteByid(shopback);
		}
		return sendTrueMsg("删除成功");
	}

	/**
	 * 查询自己导入数据
	 */
	@Auth(Shopback = { Administration.SELECT })
	@RequestMapping(value = "/shopback/selectall", method = RequestMethod.POST)
	public RequestType selectall(String uname, Integer page, Integer rows) throws Exception {
		Shopback sh = new Shopback();
		if(getMember(getLogin(uname)).getSuperadmin()!=1)
		sh.setAdminphone(uname);
		return sendTrueData(mShopbackService.getALL(sh, page, rows));
	}

	/**
	 * 批量保存数据
	 */
	@Auth(admin=true)
//	@Transactional
	@RequestMapping(value = "/shopback/save", method = RequestMethod.POST)
	public RequestType save(String JsonArray,String uname) throws Exception {
		List<Shop> lsh = GsonUtil.fromJsonList(JsonArray, Shop.class);
		if (lsh.size() == 0)
			sendFalse("数据错误或为空");
		
		int size =lsh.size();
		MemberAuths ma;
		Map<String, Object> map;
		for (int i =0;i<size;i++) {
			//代理商
			 ma = getMember(getLogin(lsh.get(i).getOneid()+""));
			if(ma==null) throw new RunException("第"+(i+1)+"条数据错误，代理商不存在！");
			if(ma.getmShop()==null||ma.getmShop().getId()==null||ma.getmShop().getSuperid()!=0)throw new RunException("第"+(i+1)+"条数据错误，"+lsh.get(i).getOneid()+"不是代理商！");
			lsh.get(i).setOneid(ma.getmShop().getId());
			//上级代理
			 ma = getMember(getLogin( lsh.get(i).getSuperid()+""));
			if(ma==null) throw new RunException("第"+(i+1)+"条数据错误，代理商不存在！");
			if(ma.getmShop()==null||ma.getmShop().getId()==null)throw new RunException("第"+(i+1)+"条数据错误，上级代理不存在！");
			lsh.get(i).setSuperid(ma.getmShop().getId());
			
			  map = mMemberService.getByparameter("uname", lsh.get(i).getMemberid()+"");
			if(map==null||map.get("ID")==null){
				Long id = System.currentTimeMillis();
				Member m = new Member();
				m.setUname(lsh.get(i).getMemberid()+"");
				m.setPassword("123456");
				 lsh.get(i).setMemberid(id);
				 m.setId(id);
				memberRegister(m,false);
				//推荐人
				Sql msql = new Sql();
	    		msql.setSql("INSERT INTO Friends (id,memberida,memberidb,memberidatype) VALUES('"+System.currentTimeMillis()+"',"+id+",'"+id+"','0')");
	    		mMemberService.execSQL(msql, -1, id+"");
	    		
	    		lsh.get(i).setShopuname(m.getUname());
			}else{
				lsh.get(i).setShopuname(map.get("UNAME").toString());
				 lsh.get(i).setMemberid(Long.valueOf(map.get("ID").toString()));
			}
			lsh.get(i).setShopstate(1);
			lsh.get(i).setSystemadmin(uname);
			lsh.get(i).setSystemtime(System.currentTimeMillis());
			lsh.get(i).setBalance(new BigDecimal("0"));
			mShopService.add(lsh.get(i));
			//店长身份
			Clerk ck = new Clerk();
			ck.setMemberid(lsh.get(i).getMemberid());
			ck.setShopid(lsh.get(i).getId());
			ck.setState(1);
			mClerkService.add(ck);
			mShopbackService.deleteByid(lsh.get(i));
			//权限
			Organization mOrganization = new Organization();
			mOrganization.setGroupbyname("门店");
			mOrganization.setMemberid(lsh.get(i).getMemberid());
			mOrganizationService.add(mOrganization);
		
			
		}
		return sendTrueMsg("保存成功，未注册的用户已自动注册，默认密码123456");
	}
}
