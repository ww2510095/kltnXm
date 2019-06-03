package com.bm.shop;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.bm.clerk.Clerk;
import com.bm.clerk.ClerkService;
import com.bm.friends.Friends;
import com.bm.friends.FriendsService;
import com.bm.shop.back.Shopback;
import com.bm.user.Member;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "店铺相关")
public class ShopController extends BaseController {

	@Autowired
	private ShopService mShopService;
	@Autowired
	private ClerkService mClerkService;
	@Autowired
	private OrganizationService mOrganizationService;
	@Autowired
	private FriendsService mFriendsService;
	
	
	/**
	 * 批量修改店铺所属人
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/shop/updatemember", method = RequestMethod.POST)
	@Transactional
	public RequestType updatemember(HttpServletRequest req) throws Exception {
		long time = System.currentTimeMillis();
		long id = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			int size = lls.size();
			int j=0;
			for (int i = 0; i < size; i++) {
				if(Stringutil.isBlank(lls.get(i).get(0))&&Stringutil.isBlank(lls.get(i).get(1)))continue;
				j=j+1;
				Member mMember  = mMemberService.getByparameter("uname", lls.get(i).get(0),Member.class);
				Shop mshop =new Shop();
				mshop.setShopname(lls.get(i).get(1));
				@SuppressWarnings("unchecked")
				List<Shop> listmap = (List<Shop>) mShopService.getALL(mshop);
				if(listmap.size()!=1)
					throw new RunException("第"+(i+3)+"行出现错误，店铺异常，未注册或有多个相同名字的店铺，请检查");
				if(mMember==null){
					mMember = new Member();
					mMember.setUname(lls.get(i).get(0));
					mMember.setPassword("123456");
					mMember.setId(id);
					memberRegister(mMember,false);
					
					Friends mFriends = new Friends();
					mFriends.setMemberida(id);
					mFriends.setMemberidb(id);
					mFriends.setMemberidatype(2);
					mFriends.setType(4);
					mFriends.setId(id);
					mFriendsService.add(mFriends);
					
					Clerk mClerk = new Clerk();
					mClerk.setMemberid(id);
					mClerk.setShopid(listmap.get(0).getId());
					mClerk.setState(1);
					mClerk.setId(id);
					mClerkService.add(mClerk);
					
				}else{
					Friends mFriends = new Friends();
					mFriends.setMemberidb(mMember.getId());
					mFriendsService.deleteBySelect(mFriends);
					
					mFriends = new Friends();
					mFriends.setMemberida(id);
					mFriends.setMemberidb(id);
					mFriends.setMemberidatype(2);
					mFriends.setType(4);
					mFriends.setId(id);
					mFriendsService.add(mFriends);
					
					Clerk mClerk=mClerkService.getByparameter("memberid", id+"",Clerk.class);
					if(mClerk!=null){
						if(!mClerk.getShopid().toString().equals(listmap.get(0).getId().toString()))
							throw new RunException("第"+(i+3)+"行出现错误，用户"+lls.get(i).get(0)+"已经是店员或者店长了，但却不是当前给定的店铺，请检查");
					}else{
						mClerk = new Clerk();
						mClerk.setMemberid(id);
						mClerk.setShopid(listmap.get(0).getId());
						mClerk.setState(1);
						mClerk.setId(id);
						mClerkService.add(mClerk);
						
					}
					
				}
				
				Long shopid = listmap.get(0).getId();
				Shop mshop1 = new Shop();
				mshop1.setId(shopid);
				mshop1.setMemberid(mMember==null?id:mMember.getId());
				mShopService.updateBySelect(mshop1);
				id=id+1;
				
			}

			
			return sendTrueMsg("导入成功，此次一共导入" + j + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	
	/**
	 * 添加店铺
	 */
	@Transactional
	@Auth(Shop={Administration.ADD})
	@RequestMapping(value = "/shop/add", method = RequestMethod.POST)
	public RequestType shopsave(Shopback en, HttpServletRequest req, String uname,String phone) throws Exception {
		Shop ms = getShop(uname);
		if(ms==null)
			return sendFalse("上级代理不存在");
		en.setSuperid(ms.getId()+"");
		en.setOneid(ms.getOneid()+"");
		
//		boolean b =false;//是否生成用户
//		// 获取用户信息
//		Member mMember = getMember(getLogin(uname));
		//父级代理
//		Map<String, Object> mobj=mMemberService.getByparameter("uname", en.getSuperid());
//		
//		if(mobj==null||Stringutil.isBlank(mobj.get("ID")+"")) return sendFalse("上级代理不存在");
////			mobj.get("ID");
//		Shop ms = getMember(getLogin(mobj.get("UNAME").toString())).getmShop();
//		if(ms==null)return sendFalse("上级代理不存在");
//		en.setSuperid(ms.getId()+"");
		
		
//		mobj=mMemberService.getByparameter("uname", phone);
//		if(mobj==null||Stringutil.isBlank(mobj.get("ID")+"")){
//			//生成用户
//			b=true;
//			Member mMember1 = new Member();
//			mMember1.setUname(phone);
//			memberRegister(mMember1,true);
//		}
		
//		en.setSuperid(getMember(getLogin(mobj.get("UNAME").toString(),true)).getmShop().getId()+"");
		
		uname = GsonUtil.toJsonString(en);
		Shop sp = GsonUtil.fromJsonString(uname, Shop.class);
		sp.setShopuname(phone);
		return addto(sp, req, phone);
//		uname = "添加成功，等待审核。";
//		if(b)uname =uname + "店铺所属用户不存在，已经自动注册，初始密码123456";
//		
//		return sendTrueMsg(uname);

	}
	/**
	 * 根据店铺名字模糊解锁编码和名字
	 */
	@Transactional
	@Auth
	@RequestMapping(value = "/shop/getShopByShopNameLike", method = RequestMethod.POST)
	public RequestType getShopByShopNameLike(String shopname) throws Exception {
		if(Stringutil.isBlank(shopname))
			return sendFalse("最少输入一个数值");
		
//		Sql msql = new Sql();
		if(shopname.length()==11){
			try {
//				msql.setSql("select id from Identity where memberid="+getMember(shopname).getId());
//				if(mMemberService.exeSelectSql(msql).size()!=0){
//					List<Map<String, Object>>  listmap = new ArrayList<Map<String, Object>>();
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("SHOPNAME", "推荐人:"+shopname);
//					map.put("CODE", getMember(shopname).getId());
//					listmap.add(map);
//					return sendTrueData(listmap);
//				}
				List<Map<String, Object>>  listmap = new ArrayList<Map<String, Object>>();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("SHOPNAME", "推荐人:"+shopname);
				map.put("CODE", getMember(shopname).getId());
				listmap.add(map);
				return sendTrueData(listmap);
			} catch (Exception e) {
//				throw new RunException("错误");
//				msql.setSql("select shopname,code from shop where shopname like '%"+shopname+"%' and superid !=0");
//				return sendTrueData(mMemberService.exeSelectSql(msql));
			}
		
		}
		throw new RunException("错误");
//		msql.setSql("select shopname,code from shop where shopname like '%"+shopname+"%' and superid !=0");
//		return sendTrueData(mMemberService.exeSelectSql(msql));
	}
	
	/**
	 * 申请代理商
	 */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/shop/addto", method = RequestMethod.POST)
	public RequestType addto(Shop en, HttpServletRequest req, String uname) throws Exception {
		if(Stringutil.isBlank(en.getShopuname()))
			return sendFalse("错误：店铺账号不可为空");
		try {
			// 获取用户信息
			MemberAuths mMember = getMember(getLogin(en.getShopuname()));
			if(mMember.getmShop()!=null)
				return sendFalse("错误：账号："+en.getShopuname()+"已经有店铺了");
			
			en.setSystemtype(getMember(getLogin(uname)).getUsersystem());
			en.setMemberid(mMember.getId());
		} catch (Exception e) {
			//用户不存在
			Member mMember = mMemberService.getByparameter("uname", en.getShopuname(),Member.class);
			if(mMember==null){
				mMember=new Member();
				Long id = System.currentTimeMillis();
				mMember.setId(id);
				mMember.setUname(en.getShopuname());
				String sa="1";
				if(en.getOneid()!=null){
					if(!(en.getOneid()+"").equals("1")){
						Map<String, Object>  m = mShopService.getById(en.getOneid());
						if(m!=null)
							sa = m.get("MEMBERID").toString();
							
					}
						
				}
					
				
				memberRegister(mMember, true,sa);
			}
			en.setSystemtype("0");
			en.setMemberid(mMember.getId());
					
					
			
		}
		
		try {
			// logo
			if(Stringutil.isBlank(en.getShoplogo())){
				if (req instanceof StandardMultipartHttpServletRequest)
					en.setShoplogo(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req,mFileService));
			}
		} catch (Exception e) {
		}
	

		if (Stringutil.isBlank(en.getShopname()))
			return sendFalse("名字不可为空");
		if (Stringutil.isBlank(en.getShoptype()))
			return sendFalse("类型不可为空");
		if (Stringutil.isBlank(en.getShopresume()))
			return sendFalse("简介不可为空");
		if (Stringutil.isBlank(en.getProvince()))
			return sendFalse("省不可为空");
		if (Stringutil.isBlank(en.getCity()))
			return sendFalse("市不可为空");
		if (Stringutil.isBlank(en.getArea()))
			return sendFalse("区不可为空");
		if (Stringutil.isBlank(en.getStreet()))
			return sendFalse("街道不可为空");
		if (Stringutil.isBlank(en.getDetailed()))
			return sendFalse("详细地址不可为空");
		if (en.getLongitude() == null || en.getLongitude().doubleValue() == 0)
			return sendFalse("经纬度错误");
		if (en.getLatitude() == null || en.getLatitude().doubleValue() == 0)
			return sendFalse("经纬度错误");
		
		Long id =System.currentTimeMillis();
		if(en.getId()==null)
			en.setId(id);

		if (en.getSuperid() == null)
			en.setSuperid(0L);// 0代表顶级节点，系统跟
		if (en.getOneid() == null)//一级代理商
			en.setOneid(en.getId());
		
		en.setShopstate(1);
		en.setAddtime(System.currentTimeMillis());
		en.setBalance(new BigDecimal("0"));
		mShopService.add(en);
		//供应商身份
		Clerk ck = new Clerk();
		ck.setMemberid(en.getMemberid());
		ck.setShopid(en.getId());
		ck.setState(1);
		mClerkService.add(ck);
		Sql msql = new Sql();
		msql.setSql("select id from Friends where  memberidb="+en.getMemberid());
		List<Map<String, Object>> listmap =mClerkService.exeSelectSql(msql);
		if(listmap.size()==0)
			msql.setSql("INSERT INTO Friends (id,memberida,memberidb,memberidatype,type,bz) VALUES('"+System.currentTimeMillis()+"',"+en.getMemberid()+",'"+en.getMemberid()+"','0','3','')");
		else
			msql.setSql("update Friends set memberida="+en.getMemberid()+" where  memberidb="+en.getMemberid());
		mClerkService.execSQL(msql, -1, "");
		
		if(en.getSuperid()==0){
			//权限
			Organization mOrganization = new Organization();
			mOrganization.setGroupbyname("供应商");
			mOrganization.setMemberid(en.getMemberid());
			mOrganizationService.add(mOrganization);
		}
		

		return sendTrueMsg("添加成功");

	}

	/**
	 * 更新店铺信息
	 */
	@Auth(Shop = { Administration.UPDATE })
	@RequestMapping(value = "/shop/update", method = RequestMethod.POST)
	public RequestType shopupdate(Shop en, HttpServletRequest req) throws Exception {
		// logo
		if(Stringutil.isBlank(en.getShoplogo())){
			if (req instanceof StandardMultipartHttpServletRequest)
				en.setShoplogo(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req,mFileService));
		}
	
		mShopService.updateBySelect(en);
		return sendTrueMsg("修改成功");
	}

	/**
	 * 删除店铺信息
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/shop/delete", method = RequestMethod.POST)
	public RequestType shopdelete(Shop en, HttpServletRequest req) throws Exception {
		mShopService.deleteByid(en);
		return sendTrueMsg("删除成功");
	}
	/**
	 * 查询店铺信息
	 */
	@Auth(Shop={Administration.SELECT})
	@RequestMapping(value = "/shop/selectall", method = RequestMethod.POST)
	public RequestType selectall(String adminname,Shop en, String orderbykey,Integer orderbytype,Integer page,Integer rows,HttpServletRequest req) throws Exception {
		if(!Stringutil.isBlank(adminname)){
			Shop m =getMember(getLogin(adminname)).getmShop();
			if(m==null)
				return sendTrueData(new ArrayList<>());
			else{
				List<Shop> listShop= new ArrayList<>();
				m.setShopuname(adminname);
				listShop.add(m);
				return sendTrueData(listShop);
			}
		}
			
		return sendTrueData(mShopService.getALL(en, orderbykey, orderbytype, page, rows));
	}
	/**
	 * 查询店铺信息模糊解锁
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/shop/selectalladmin", method = RequestMethod.POST)
	public RequestType selectalladmin(String str, String orderbykey,Integer orderbytype,Integer page,Integer rows) throws Exception {
		if(Stringutil.isBlank(str))return sendFalse("输入内容不可为空");
		if(rows==null)rows=10;
		Sql msql = new Sql();
		msql.setSql("select shop.*,uname shopuname from shop left join member on memberid=member.id where shopname like '%"+str+"%' or detailed like '%"+str+"%' ");
		msql.setOrderbykey(orderbykey);
		msql.setOrderbytype(orderbytype);
		msql.setPage(page);
		msql.setRows(rows);
		return sendTrueData(mShopService.exeSelectSql(msql));
	}

	/**
	 * 关闭店铺
	 */
	@Auth(Shop = { Administration.UPDATE })
	@RequestMapping(value = "/shop/close", method = RequestMethod.POST)
	public RequestType close(Shop en, HttpServletRequest req) throws Exception {
		Long id = en.getId();
		en = new Shop();
		en.setId(id);
		en.setShopstate(2);
		mShopService.updateBySelect(en);
		return sendTrueMsg("操作成功");
	}

	/**
	 * 开启店铺
	 */
	@Auth(Shop = { Administration.UPDATE })
	@RequestMapping(value = "/shop/open", method = RequestMethod.POST)
	public RequestType open(Shop en, HttpServletRequest req) throws Exception {
		Long id = en.getId();
		en = new Shop();
		en.setId(id);
		en.setShopstate(1);
		mShopService.updateBySelect(en);
		return sendTrueMsg("操作成功");
	}

	/**
	 * 查询店铺详细信息
	 */
	@Auth(Shop = { Administration.SELECT })
	@RequestMapping(value = "/shop/select", method = RequestMethod.POST)
	public RequestType select(Shop en, HttpServletRequest req) throws Exception {
		return sendTrueData(mShopService.getById(en));
	}

	/**
	 * 审核，冻结，解冻店铺
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/shop/examine", method = RequestMethod.POST)
	public RequestType examine(Shop en, HttpServletRequest req,String uname) throws Exception {
		en.setSystemtime(System.currentTimeMillis());
		en.setSystemadmin(uname);
		mShopService.updateBySelect(en);
		return sendTrueMsg("操作成功");
	}

}
