package com.bm.webapp;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.Aenum.Zd;
import com.bm.auths.AuthException;
import com.bm.auths.MemberAuths;
import com.bm.auths.groupby.OrganizationService;
import com.bm.auths.organization.Organization;
import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.excle.ExportExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.IBeanUtil;
import com.bm.base.util.MatrixUtil;
import com.bm.base.util.MyDate;
import com.bm.clerk.Clerk;
import com.bm.clerk.ClerkService;
import com.bm.clerk.commission.Commission;
import com.bm.consumption.Refundresponse;
import com.bm.consumption.RefundresponseService;
import com.bm.orders.OrdersController;
import com.bm.ordersRule.OrdersRule;
import com.bm.ordersRule.OrdersRuleService;
import com.bm.ordersRule.Programme;
import com.bm.ordersRule.ProgrammeService;
import com.bm.ordersRule.gd.Gd;
import com.bm.ordersRule.gd.GdService;
import com.bm.ordersRule.gd.Gd_Commodity_key;
import com.bm.ordersRule.gd.Gd_Commodity_keyService;
import com.bm.ordersRule.sharingdetails.SharingdetailsService;
import com.bm.shop.Shop;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

/**
 * web需求整理
 */
@RestController
@Api(tags = "webapp")
public class Web extends BaseController {

	@Autowired
	private OrdersRuleService mOrdersRuleService;
	@Autowired
	private SharingdetailsService mSharingdetailsService;
	@Autowired
	private ClerkService mClerkService;
	@Autowired
	private OrganizationService mOrganizationService;
	@Autowired
	private ProgrammeService mProgrammeService;

	
	@Autowired
    private RefundresponseService mRefundresponseService;
	@Autowired
	private GdService mGdService;
	@Autowired
	private Gd_Commodity_keyService mGd_Commodity_keyService;
	
	@RequestMapping(value = "/web/dcode1", method = RequestMethod.GET)
	public  void senimage(String str,HttpServletResponse response) throws Exception {
		if(Stringutil.isBlank(str))throw new RunException("内容不可为空");
		BufferedImage image = MatrixUtil.toBufferedImage(MatrixUtil.toBarCodeMatrix(str, null, null));
		sendImage(response, image);
	}
	
   
	/**
	 *添加默认的固定分成
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/Gd/adddefault", method = RequestMethod.POST)
	public  RequestType Gdadddefault(Gd mGd) throws Exception {
		if(mGd.getShoponeid()==null)return sendFalse("供应商不可为空");
		MemberAuths mm=getMember(mGd.getShoponeid().toString());
		if(mm.getmShop()==null||!mm.getmShop().getSuperid().toString().equals("0"))
			return sendFalse(mGd.getShoponeid()+"不是供应商");
		mGd.setShoponeid(mm.getId());
		if(mGd.getClerk()==null)mGd.setClerk(MyParameter.mBigDecimal_0);
		if(mGd.getShopone()==null)mGd.setShopone(MyParameter.mBigDecimal_0);
		if(mGd.getShopto()==null)mGd.setShopto(MyParameter.mBigDecimal_0);
		if(mGd.getShop()==null)mGd.setShop(MyParameter.mBigDecimal_0);
		if(mGd.getSystemone()==null)mGd.setSystemone(MyParameter.mBigDecimal_0);
		if(mGd.getJing_xiao_shang()==null)mGd.setJing_xiao_shang(MyParameter.mBigDecimal_0);
		if(mGd.getXian_shang_dian_zhu()==null)mGd.setXian_shang_dian_zhu(MyParameter.mBigDecimal_0);
		
		mGdService.add(mGd);
		return sendTrueMsg("添加成功");
	}
	
	
	/**
	 *为商品固定分成规则
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/Gd/addGd_Commodity_key", method = RequestMethod.POST)
	public  RequestType addGd_Commodity_key(Gd_Commodity_key mGd_Commodity_key) throws Exception {
		if(Stringutil.isBlank(mGd_Commodity_key.getYoucode()))return sendFalse("条码不可为空");
		Sql msql = new Sql();
		msql.setSql("select id from stock where code='"+mGd_Commodity_key.getYoucode()+"'");
		if(mGd_Commodity_keyService.exeSelectSql(msql).size()==0)return sendFalse("错误:条码"+mGd_Commodity_key.getYoucode()+"不存在或没有导入库存");
		if(mGd_Commodity_key.getClerk()==null)mGd_Commodity_key.setClerk(MyParameter.mBigDecimal_0);
		if(mGd_Commodity_key.getShopone()==null)mGd_Commodity_key.setShopone(MyParameter.mBigDecimal_0);
		if(mGd_Commodity_key.getShopto()==null)mGd_Commodity_key.setShopto(MyParameter.mBigDecimal_0);
		if(mGd_Commodity_key.getShop()==null)mGd_Commodity_key.setShop(MyParameter.mBigDecimal_0);
		if(mGd_Commodity_key.getSystemone()==null)mGd_Commodity_key.setSystemone(MyParameter.mBigDecimal_0);
		if(mGd_Commodity_key.getJing_xiao_shang()==null)mGd_Commodity_key.setJing_xiao_shang(MyParameter.mBigDecimal_0);
		if(mGd_Commodity_key.getXian_shang_dian_zhu()==null)mGd_Commodity_key.setXian_shang_dian_zhu(MyParameter.mBigDecimal_0);
		
		
		mGd_Commodity_keyService.add(mGd_Commodity_key);
		return sendTrueMsg("添加成功");
	}
	/**
	 *查看默认的固定分成
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/Gd/default_select", method = RequestMethod.POST)
	public  RequestType Gddefaultselect(Gd mGd,Integer rows,Integer page) throws Exception {
		if(mGd.getShoponeid()!=null)
			mGd.setShoponeid(getLogin(mGd.getShoponeid().toString()).getUserid());
		@SuppressWarnings("unchecked")
		List<Gd> list = (List<Gd>) mGdService.getALL(mGd, page, rows);
		for (Gd gd : list) {
			gd.setShoponeid(Long.valueOf(getMember(gd.getShoponeid()).getUname()));
		}
		return sendTrueData(list);
	}
	
	
	/**
	 *查询商品固定分成规则
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/Gd/Gd_Commodity_key_select", method = RequestMethod.POST)
	public  RequestType addGd_Commodity_key(Gd_Commodity_key mGd_Commodity_key,Integer rows,Integer page) throws Exception {
		return sendTrueData(mGd_Commodity_keyService.getALL(mGd_Commodity_key, page, rows));
	}
	/**
	 * 字典查询
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/zd/zd", method = RequestMethod.POST)
	public  RequestType zdcx(String pack,String field,String key) throws Exception {
		try {
			pack=pack.toLowerCase();
			field=field.toLowerCase();
			pack = pack.replaceFirst(pack.substring(0, 1), pack.substring(0, 1).toUpperCase());
			Class<?> cl = Class.forName("com.bm.Aenum."+pack+"_"+field);
			Method m = cl.getMethod("values");
			Object[] obj = (Object[]) m.invoke(cl);
			List<Zd> listZd = new ArrayList<>();
			if(Stringutil.isBlank(key)){
				for (Object object : obj) {
					Zd mzd = new Zd();
					mzd.setKey(Integer.parseInt(object.getClass().getMethod("getKey").invoke(object).toString()));
					mzd.setValue(object.getClass().getMethod("getValue").invoke(object).toString());
					listZd.add(mzd);
				}
				return sendTrueData(listZd);
			}else{
				for (Object object : obj) {
					if(object.getClass().getMethod("getKey").invoke(object).toString().equals(key)){
						Zd mzd= new Zd();
						mzd.setKey(Integer.parseInt(key));
						mzd.setValue(object.getClass().getMethod("getValue").invoke(object).toString());
						return sendTrueData(mzd);
					}
					
				}
			}
		} catch (Exception e) {
			
			if(pack.contains("/")){
				String[] strs = pack.split("/");
				String str=null;
				for (String string : strs) {
					if(!Stringutil.isBlank(string)){
						if("sp".equals(string)){
							string="xsp";
						}
						str=string;
						break;
					}
				}
				if(!Stringutil.isBlank(str)){
					Sql msql = new Sql();
					pack="select COLUMN_NAME 字段名,COMMENTS 注释 from user_col_comments where Table_Name ='"+str.toUpperCase()+"'";
					if(!Stringutil.isBlank(field)){
						pack=pack+" and column_name='"+field.toUpperCase()+"'";
					}
					msql.setSql(pack);
				return	sendTrueData(mMemberService.exeSelectSql(msql));
				}
			}
		}
		
		
		return sendTrueMsg("未找到对应值");
	}
	/**
	 * 调用户等级
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/AA/AA", method = RequestMethod.POST)
	public RequestType aa(String phone,int a1) throws Exception {
		Sql msql = new Sql();
		
		if(a1==0){
			msql.setSql("delete Identity where memberid= "+getLogin(phone).getUserid());
		}
		if(a1==1){
			msql.setSql("update Identity set type=1 where memberid= "+getLogin(phone).getUserid());
		}
		if(a1==2){
			msql.setSql("update Identity set type=2 where memberid= "+getLogin(phone).getUserid());
		}
		mProgrammeService.execSQL(msql);
		return sendTrueMsg("添加成功");
	}
	
	/**
	 * 添加一条结算规则
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/OrdersRule/add", method = RequestMethod.POST)
	public RequestType OrdersRuleadd(OrdersRule mOrdersRule) throws Exception {
		if (mOrdersRule.getShoponeid() == null)
			return sendFalse("代理商账号不可为空"); // 代理商id
		if (mOrdersRule.getShopone() == null)
			return sendFalse("代理商分成不可为空"); // 代理商分成
		if (mOrdersRule.getShopto() == null)
			return sendFalse("分公司分成不可为空"); // 分公司分成
		if (mOrdersRule.getShop() == null)
			return sendFalse("店铺分成不可为空"); // 店铺分成
		if (mOrdersRule.getSystemone() == null)
			return sendFalse("系统分成不可为空"); // 系统分成
		if (mOrdersRule.getClerk() == null)
			return sendFalse("推荐人分成不可为空"); // 推荐人分成（店员？二维码商家？）b2c
		if (mOrdersRule.getType() == null)
			mOrdersRule.setType(1);
		
		MemberAuths ma = getMember(getLogin(mOrdersRule.getShoponeid() + ""));
		if (ma.getmShop() == null || ma.getmShop().getId() == null || ma.getmShop().getSuperid() != 0)
			return sendFalse("用户:" + mOrdersRule.getShoponeid() + "不是代理商");
		
		mOrdersRule.setShoponeid(ma.getmShop().getId());
		
		if (mOrdersRule.getShopone().add(mOrdersRule.getShopto()).add(mOrdersRule.getShop())
				.add(mOrdersRule.getSystemone()).add(mOrdersRule.getClerk()).doubleValue() != 100)
			return sendFalse("错误，分成累加结果不等于100");
		
		mOrdersRuleService.add(mOrdersRule);
		return sendTrueMsg("添加成功");
	}
	/**
	 * 添加一条结算规则(按照固定值分成)
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/gd/add", method = RequestMethod.POST)
	public RequestType gdadd(OrdersRule mOrdersRule) throws Exception {
//		if (mOrdersRule.getShoponeid() == null)
//			return sendFalse("代理商账号不可为空"); // 代理商id
//		if (mOrdersRule.getShopone() == null)
//			return sendFalse("代理商分成不可为空"); // 代理商分成
//		if (mOrdersRule.getShopto() == null)
//			return sendFalse("分公司分成不可为空"); // 分公司分成
//		if (mOrdersRule.getShop() == null)
//			return sendFalse("店铺分成不可为空"); // 店铺分成
//		if (mOrdersRule.getSystemone() == null)
//			return sendFalse("系统分成不可为空"); // 系统分成
//		if (mOrdersRule.getClerk() == null)
//			return sendFalse("推荐人分成不可为空"); // 推荐人分成（店员？二维码商家？）b2c
//		if (mOrdersRule.getType() == null)
//			mOrdersRule.setType(1);
//		
//		MemberAuths ma = getMember(getLogin(mOrdersRule.getShoponeid() + ""));
//		if (ma.getmShop() == null || ma.getmShop().getId() == null || ma.getmShop().getSuperid() != 0)
//			return sendFalse("用户:" + mOrdersRule.getShoponeid() + "不是代理商");
//		
//		mOrdersRule.setShoponeid(ma.getmShop().getId());
//		
//		if (mOrdersRule.getShopone().add(mOrdersRule.getShopto()).add(mOrdersRule.getShop())
//				.add(mOrdersRule.getSystemone()).add(mOrdersRule.getClerk()).doubleValue() != 100)
//			return sendFalse("错误，分成累加结果不等于100");
//		
//		mOrdersRuleService.add(mOrdersRule);
		return sendTrueMsg("添加成功");
	}
	/**
	 * 添加一条结算规则
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/Programme/add", method = RequestMethod.POST)
	public RequestType Programmeadd(Programme mProgramme) throws Exception {
		if (mProgramme.getShopone() == null)
			return sendFalse("代理商分成不可为空"); // 代理商分成
		if (mProgramme.getShopto() == null)
			return sendFalse("分公司分成不可为空"); // 分公司分成
		if (mProgramme.getShop() == null)
			return sendFalse("店铺分成不可为空"); // 店铺分成
		if (mProgramme.getSystemone() == null)
			return sendFalse("系统分成不可为空"); // 系统分成
		if (mProgramme.getSales() == null)
			return sendFalse("经销售分成不可为空"); // 
		if (mProgramme.getOnlineshopkeeper() == null)
			return sendFalse("线上店主分成不可为空"); //
		
		
		if (mProgramme.getShopone().add(mProgramme.getShopto()).add(mProgramme.getShop())
				.add(mProgramme.getSystemone())
				.add(mProgramme.getSales())
				.add(mProgramme.getOnlineshopkeeper())
				.doubleValue() != 100)
			return sendFalse("错误，分成累加结果不等于100");
		
		mProgrammeService.add(mProgramme);
		return sendTrueMsg("添加成功");
	}
	/**
	 * 查询结算规则
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/Programme/select", method = RequestMethod.POST)
	public RequestType Programmeselect(Integer page,Integer rows) throws Exception {
		
		return sendTrueData(mProgrammeService.getALL(new Programme(), page, rows));
	}

	/**
	 * 系统剩余任务数量
	 */
	@Auth(admin = true)
	@RequestMapping(value = "/task/tasksize", method = RequestMethod.POST)
	public RequestType tasksize(Integer number) throws Exception {
		if (number == null)
			number = MyParameter.system;
		Sql msql = new Sql();
		msql.setSql("select nvl(count(*),0) tasksize from task where system=" + number);
		return sendTrueData(mOrdersRuleService.exeSelectSql(msql).get(0));
	}
	/**
	 * 为指定店铺添加指定店员
	 */
	@Auth(admin = true)
	@Transactional
	@RequestMapping(value = "/web/shopAddClerk", method = RequestMethod.POST)
	public RequestType shopAddClerk(String code,String phone) throws Exception {
		if(Stringutil.isBlank(code))return sendFalse("店铺编码不可为空");
		if(Stringutil.isBlank(phone))return sendFalse("店员账号不可为空");
		Long userid = getLogin(phone).getUserid();
		if(getMember(userid).getmShop()!=null)
			return sendFalse(phone+"是店长，无法更改");
		Sql msql = new Sql();
		msql.setSql("select id  from shop where code='"+code+"'" );
		List<Map<String, Object>> listmap = mOrdersRuleService.exeSelectSql(msql);
		if(listmap.size()==0)
			return sendFalse("店铺"+code+"不存在");
		
		msql.setSql("delete clerk where memberid="+userid );
		mOrdersRuleService.execSQL(msql, -1, "");
		Clerk mclerk = new Clerk();
		mclerk.setMemberid(userid);
		mclerk.setShopid(Long.valueOf(listmap.get(0).get("ID").toString()));
		mclerk.setState(1);
		mClerkService.add(mclerk);
		msql.setSql("update Friends set memberida=memberidb where memberidb="+userid );
		mClerkService.execSQL(msql, -1, "");
		
		//权限
		Organization mOrganization = new Organization();
		mOrganization.setGroupbyname("店员");
		mOrganization.setMemberid(userid);
		mOrganizationService.add(mOrganization);
		return sendTrueData("修改成功");
	}

	/**
	 * 查询用户层级关系
	 */
	@Auth
//	@RequestMapping(value = "/web/selectMember", method = RequestMethod.POST)
	public RequestType selectMember(String phone, String uname) throws Exception {
		List<String> lmo = new ArrayList<>();
		MemberAuths ma = getMember(getLogin(phone));
		Long idb = ma.getId();
		String sql = "select memberida,uname from Friends left join member on member.id=memberida where memberidb="
				+ idb;
		Sql msql = new Sql();
		List<Map<String, Object>> lmo1;
		while (true) {
			msql.setSql(sql);
			lmo1 = mOrdersRuleService.exeSelectSql(msql);
			if (lmo1.size() == 0)
				break;

			if(lmo1.get(0).get("UNAME").toString().equals(phone)){
				msql.setSql("select shopname from shop where id=(select shopid from clerk where memberid="+lmo1.get(0).get("MEMBERIDA")+")");
				lmo.add(mOrdersRuleService.exeSelectSql(msql).get(0).get("SHOPNAME").toString());
				break;
			}
				
			if ((lmo1.get(0).get("MEMBERIDA") + "").equals("1")) {
				lmo.add("SYSTEM");
				break;
			} else {
				lmo.add(lmo1.get(0).get("UNAME") + "");
				sql = "select memberida,uname from Friends left join member on member.id=memberida where memberidb="
						+ lmo1.get(0).get("MEMBERIDA").toString();
			}

		}
		if (getMember(getLogin(uname)).getSuperadmin() != 1) {
			boolean b = false;
			for (String str : lmo) {
				if (str.equals(uname)) {
					b = true;
					break;
				}
			}
			if (!b)
				throw new RunException("抱歉，权限不足，您不能查询该用户信息");
		}
		if (lmo.size() > 1)
			Collections.reverse(lmo);// 倒叙集合
		return sendTrueData(lmo);
	}

	/**
	 * 查询用店铺下一级
	 */
	@Auth
	@RequestMapping(value = "/web/selectShop", method = RequestMethod.POST)
	public RequestType selectShop(String phone, String uname, Integer type, Integer page, Integer rows)
			throws Exception {
		MemberAuths ma = getMember(getLogin(phone));
		if (ma.getmShop() == null || ma.getmShop().getId() == null)
			return sendFalse("抱歉" + phone + "不是店铺账号");

		selectShopAuth(phone, uname);// 查权限
		Sql msql = new Sql();
		msql.setPage(page == null ? 1 : page);
		msql.setRows(rows);

		if (type == 1) {
			msql.setSql(
					"select uname,nickname,portrait,phone from member where id in(select memberid from clerk where shopid="
							+ ma.getmShop().getId() + ")");

		} else {
			msql.setSql(
					"select shopname, decode(shopstate,0,'提交',1,'正常',2,'关闭',-1,'未通过',-2,'冻结')decode ,code,shopphone from shop where superid="
							+ ma.getmShop().getId());
		}
		return sendTrueData(mOrdersRuleService.exeSelectSql(msql));
	}

	private void selectShopAuth(String phone, String uname) throws Exception {
		if (getMember(getLogin(uname)).getSuperadmin() == 1)
			return;// 超级管理员
		if (phone.equals(uname))
			return;// 查询自己

		if (getMember(getLogin(uname)).getmShop() == null || getMember(getLogin(uname)).getmShop().getId() == null)
			throw new RunException("抱歉，你的权限不足");

		Set<String> ls = new HashSet<>();
		String sid = getMember(getLogin(phone)).getmShop().getId() + "";
		Sql msql = new Sql();
		List<Map<String, Object>> lmap;
		while (sid != null) {
			msql.setSql("select superid from shop where id=" + sid);
			lmap = mOrdersRuleService.exeSelectSql(msql);
			if (lmap.size() == 0)
				sid = null;
			else {
				sid = lmap.get(0).get("SUPERID").toString();
				ls.add(lmap.get(0).get("SUPERID").toString());
			}

		}

		boolean b = false;
		for (String str : ls) {
			if (str.equals(getMember(getLogin(uname)).getmShop().getId() + "")) {
				b = true;
				break;
			}
		}
		if (!b)
			throw new RunException("抱歉，权限不足，您不能查询该用户信息");

	}

	/**
	 * 查询模块
	 */
	@Auth
	@RequestMapping(value = "/web/mk", method = RequestMethod.POST)
	public RequestType mk() throws Exception {
		Sql msql = new Sql();
		msql.setSql("select DISTINCT indexs from COMMODITYkey where indexs is not null");
		return sendTrueData(mOrdersRuleService.exeSelectSql(msql));
	}

	/**
	 * 查询结算规则
	 */
	@Auth(ORDERSRULE = true)
	@RequestMapping(value = "/OrdersRule/select", method = RequestMethod.POST)
	public RequestType OrdersRuleselect(String uname, Long phone, Integer rows, Integer page, Integer type)
			throws Exception {
		OrdersRule mOrdersRule = new OrdersRule();
		if (getMember(getLogin(uname)).getSuperadmin() != 1) {
			if (getMember(getLogin(uname)).getmShop() == null || getMember(getLogin(uname)).getmShop().getId() == null)
				return sendFalse("权限不足");
			mOrdersRule.setShoponeid(getMember(getLogin(uname)).getmShop().getOneid());
		}

		else if (phone != null) {
			MemberAuths ma = getMember(getLogin(phone + ""));
			if (ma.getmShop() == null || ma.getmShop().getId() == null)
				return sendFalse("用户:" + mOrdersRule.getShoponeid() + "所有节点不存在代理商");
			mOrdersRule.setShoponeid(ma.getmShop().getOneid());
		}

		if (type != null && type == 1)
			return sendTrueData(mSharingdetailsService.getALL(mOrdersRule, page, rows));

		return sendTrueData(mOrdersRuleService.getALL(mOrdersRule, page, rows));
	}

	// /**
	// * 分利详情
	// * */
	// @Auth
	// @RequestMapping(value = "/SharingdetailsService/select", method =
	// RequestMethod.POST)
	// public RequestType SharingdetailsServiceselect(String uname,Long
	// phone,Integer rows,Integer page) throws Exception {
	// return OrdersRuleselect(uname, phone, rows, page, 1);
	// }
	/**
	 * 退货查询
	 * 
	 * @param star
	 *            开始时间，
	 * @param end
	 *            结束时间
	 * 
	 */
	@Auth
	@RequestMapping(value = "/web/Returngoods/select", method = RequestMethod.POST)
	public RequestType Returngoodsselect(Long id, String code, HttpServletRequest req, String uname, Long star,
			Long end, Integer page, Integer rows) throws Exception {
		return sendTrueData(selectReturngoodsData(id, code, req, uname, star, end, page == null ? 1 : page, rows));
	}

	/** 导出退货报表 */
	@Auth
	@RequestMapping(value = "/web/Returngoods/expor", method = RequestMethod.GET)
	public void Returngoodsexpor(Long id, String code, HttpServletRequest req, String uname, Long star, Long end,
			Integer rows, HttpServletResponse response) throws Exception {
		List<Map<String, Object>> listmap = selectReturngoodsData(id, code, req, uname, star, end, 1, rows);
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("退货编号");
		key.add("id");
		title.add("申请退货时间");
		key.add("cdate");
		title.add("退货人账号");
		key.add("uname1");
		title.add("退货原因");
		key.add("reason");
		title.add("是否同意退货");
		key.add("istrue");
		title.add("销售价");
		key.add("price");
		title.add("退价");
		key.add("refund");
		title.add("拒绝退货原因");
		key.add("refuse");
		title.add("店铺名字");
		key.add("shopname");
		title.add("店铺编号");
		key.add("shopcode");
		title.add("商品名字");
		key.add("title");
		title.add("数量");
		key.add("num");
		title.add("商品条码");
		key.add("itemid");//
		title.add("审核人");
		key.add("uname");//
		title.add("售价汇总");
		key.add("priceall");//
		title.add("推荐汇总");
		key.add("refundall");//
		ExportExcel.Export("退货报表", title, key, listmap, response);

	}

	private List<Map<String, Object>> selectReturngoodsData(Long id, String code, HttpServletRequest req, String uname,
			Long star, Long end, Integer page, Integer rows) throws Exception {
		Sql msql = new Sql();
		star = star == null ? 0 : star;
		end = end == null ? System.currentTimeMillis() : end;
		msql.setPage(page);
		msql.setRows(rows);
		// 基础sql1
		String sql = "select a.*,member.uname uname1 from (select itemid,Returngoods.price * num priceall,refund*num refundall,Returngoods.id,TO_CHAR(Returngoods.id / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),"
				+ " 'YYYY-MM-DD HH24:MI:SS') AS CDATE ,shop.memberid,uname,decode(istrue,-1,'不同意',0,'未处理',1,'已同意')istrue,reason,Returngoods.price ,"
				+ "refund,refuse,shop.shopname,shop.code shopcode,title,shop.code,num  from Returngoods "
				+ "left join orderrelevance on orderrelevanceid=orderrelevance.id left join orders "
				+ "on orders.ordernumber=orderrelevance.ordernum  left join member on member.id="
				+ "Returngoods.Memberid left join shop on shop.id =Returngoods.shopid)a"
				+ "  left join member on member.id=memberid where a.id<" + end + " and a.id > " + star;

		if (id != null)
			sql = sql + " and Returngoods.id=" + id;

		if (getMember(getLogin(uname)).getSuperadmin() == 1) {
			if (!Stringutil.isBlank(code))
				sql = sql + " and shopcode='" + code + "'";
			msql.setSql(sql);
		} else {
			try {
				code = getMember(getLogin(uname)).getmShop().getCode();
				sql = sql + " and a.code='" + code + "'";
			} catch (Exception e) {
				throw new AuthException("权限不足");
			}
		}

		msql.setSql(sql);
		return mMemberService.exeSelectSql(msql);
	}

	@Auth(admin=true)
	@RequestMapping(value = "/web/Commission/select2", method = RequestMethod.POST)
	public RequestType Sharingdetailsselect2(String uname, Long star, Long end,
			Integer page, Integer rows) throws Exception {
		return sendTrueData(getCommissionData(uname, star, end, page, rows));
	}
	private List<Commission> getCommissionData(String uname, Long star, Long end,
			Integer page, Integer rows) throws Exception {
		Sql msql = new Sql();
		
		page=page==null?1:page;
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		msql.setPage(page);
		msql.setRows(rows);
		msql.setOrderbykey("id");
		msql.setOrderbytype(1);
		msql.setSql("select * from Commission where time1>"+star+" and time1<"+end);
		List<Commission> listCommission = IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql), Commission.class);
		for (Commission commission : listCommission) {
			if(commission.getMemberid1()!=null){
				commission.setMemberid1(Long.valueOf(getMember(commission.getMemberid1()).getUname()));
			}
			if(commission.getMemberid2()!=null){
				commission.setMemberid2(Long.valueOf(getMember(commission.getMemberid2()).getUname()));
			}
			if(commission.getMemberid3()!=null){
				commission.setMemberid3(Long.valueOf(getMember(commission.getMemberid3()).getUname()));
			}
			try {
				commission.setMemberid4(Long.valueOf(getMember(commission.getMemberid4()).getUname()));
			} catch (Exception e) {
			}
			
			commission.setMemberone(getMember(Long.valueOf(commission.getMemberone())).getUname());
			commission.setMemberid(Long.valueOf(getMember(commission.getMemberid()).getUname()));
			
			
		}
		return listCommission;
	}
	@Auth(admin=true)
	@RequestMapping(value = "/web/Commission/expor", method = RequestMethod.GET)
	public void Sharingdetailsexpor(HttpServletResponse response, String uname, Long star, Long end,
			Integer page, Integer rows) throws Exception {
		page=1;
		 List<Commission> listCommission =	getCommissionData( uname, star, end, page, rows);
		 List<Map<String, Object>> listmap = IBeanUtil.ListJavaBean2ListMap(listCommission);
		 

			List<String> title = new ArrayList<String>();
			List<String> key = new ArrayList<String>();
			title.add("订单号"); key.add("ordersid");
			title.add("导购"); key.add("memberid1");
			title.add("导购金额"); key.add("num1");
			title.add("线上店主"); key.add("memberid2");
			title.add("线上店主金额"); key.add("num2");
			title.add("经销商"); key.add("memberid3");
			title.add("经销商金额"); key.add("num3");
			title.add("实体店主"); key.add("memberid4");
			title.add("店铺金额"); key.add("num4");
			title.add("订单金额"); key.add("ordersnum");
			
			
			ExportExcel.Export("结算报表", title, key, listmap, response);
	}
	/**
	 * 结算查询
	 * 
	 * @param star
	 *            开始时间，
	 * @param end
	 *            结束时间
	 * 
	 */
	@Auth(export = true)
	@RequestMapping(value = "/web/Sharingdetails/select", method = RequestMethod.POST)
	public RequestType Sharingdetailsselect(String code, HttpServletRequest req, String uname, Long star, Long end,
			Integer page, Integer rows) throws Exception {
		List<Map<String, Object>> list = selectSharingdetailsData(code, req, uname, star, end, page == null ? 1 : page,
				rows);
		MemberAuths ma = getMember(getLogin(uname));
		// int b =1;
		if (ma.getSuperadmin() != 1) {
			// if(ma.getmShop()==null||ma.getmShop().getSuperid()!=0)
			// b=3;
			// else
			// b=2;

			for (Map<String, Object> map : list) {
				// if(b==2){
				// map.remove("SHOPONE");
				// map.remove("SYSTEMONE");
				// }else{
				// map.remove("SYSTEMONE");
				// }
				try {
					map.put("PHONE", getMember(Long.valueOf(map.get("MEMBERID") + "")).getUname());
					map.put("PHONE1", getMember(Long.valueOf(map.get("MEMBERID1") + "")).getUname());
				} catch (Exception e) {
				}

				if (getMember(getLogin(uname)).getmShop().getSuperid() != 0) {
					map.remove("SHOPONE");
					map.remove("SHOPTO");
				}
				map.remove("SYSTEMONE");

			}
		} else {
			for (Map<String, Object> map : list) {
				try {
					map.put("PHONE", getMember(Long.valueOf(map.get("MEMBERID") + "")).getUname());
					map.put("PHONE1", getMember(Long.valueOf(map.get("MEMBERID1") + "")).getUname());
				} catch (Exception e) {
				}

			}
		}

		return sendTrueData(list);
	}

	/**
	 * 
	 * 
	 * @param star
	 *            开始时间，
	 * @param end
	 *            结束时间
	 * 
	 *            处理时间段内的数据
	 * 
	 */
	@Auth(admin = true)
	@Transactional
	@RequestMapping(value = "/web/Sharingdetails/update", method = RequestMethod.POST)
	public RequestType Sharingdetailsupdate(HttpServletRequest req, Long star, Long end) throws Exception {
		if (star == null)
			return sendFalse("开始时间不可为空");
		if (end == null)
			return sendFalse("结束时间不可为空");
		Sql msql = new Sql();
		msql.setSql("update Commission set state=3, time2="+System.currentTimeMillis()+" where ordersid >" + star + " and time2 is null and ordersid <" + end);
		mOrdersRuleService.execSQL(msql);
		
//		msql.setSql("update Sharingdetails set state=1 where orders >" + star + " and id <" + end);
//		mOrdersRuleService.execSQL(msql, -1, "");
//		msql.setSql("update Sharingdetails2 set state=1 where id >" + star + " and id <" + end);
//		mOrdersRuleService.execSQL(msql, -1, "");
		return sendTrueMsg("处理成功");
	}

	@Auth(admin = true)
	@RequestMapping(value = "/web/Sharingdetails/updatebyid", method = RequestMethod.POST)
	public RequestType Sharingdetailsupdate(HttpServletRequest req, Long id) throws Exception {
		Sql msql = new Sql();
		msql.setSql("update Sharingdetails set state=1 where id =>" + id);
		mOrdersRuleService.execSQL(msql, -1, "");
		return sendTrueMsg("处理成功,用户金额将会在次日到账");
	}

	/** 导出结算报表 */
	@Auth(export = true)
	@RequestMapping(value = "/web/Sharingdetails/expor", method = RequestMethod.GET)
	public void Sharingdetailsexpor(String code, HttpServletRequest req, String uname, Long star, Long end,
			Integer rows, HttpServletResponse response) throws Exception {
		// public void Sharingdetailsexpor(Long supplier,HttpServletRequest
		// req,String uname,Long star,Long end,Integer rows,HttpServletResponse
		// response) throws Exception {
		// Integer[] ys=null;
		// //所有数据
		// List<Map<String, Object>> listmap = new ArrayList<Map<String,
		// Object>>();
		//// if(getMember(getLogin(uname)).getSuperadmin()!=1)
		// if(supplier==null){
		// Sql msql = new Sql();
		// msql.setRows(1000);
		// msql.setSql("select DISTINCT supplier from Commodity");
		// List<Map<String, Object>> suppliermap =
		// mOrdersRuleService.exeSelectSql(msql);
		// int size = suppliermap.size();
		// //每个供应商第一行特殊颜色
		// ys= new Integer[size];
		// for (int i = 0;i<size; i++) {
		// ys[i] = listmap.size();
		// listmap.addAll(selectSharingdetailsData(
		// suppliermap.get(i).get("SUPPLIER").toString(), req, uname, star, end,
		// 1, rows));
		// }
		// }else{
		// listmap = selectSharingdetailsData(supplier+"", req, uname, star,
		// end, 1, rows);
		// }
		// MemberAuths ma = getMember(getLogin(uname));
		// int b =1;
		// if(ma.getSuperadmin()!=1){
		// if(ma.getmShop()==null||ma.getmShop().getSuperid()!=0)
		// b=3;
		// else
		// b=2;
		// }
		// List<String> title = new ArrayList<String>();
		// List<String> key = new ArrayList<String>();
		// title.add("结算编号"); key.add("id");
		// title.add("结算时间"); key.add("cdate");
		// title.add("订单id"); key.add("Ordersid");
		// title.add("供应商"); key.add("suppliername");
		// if(b==1||b==2){
		// if(getMember(getLogin(uname)).getSuperadmin()==1||getMember(getLogin(uname)).getmShop().getSuperid()==0)
		// title.add("供应商分成金额"); key.add("shopone");
		// }
		// if(getMember(getLogin(uname)).getSuperadmin()==1||getMember(getLogin(uname)).getmShop().getSuperid()==0)
		// title.add("供应商分成金额"); key.add("shopone");
		// title.add("分公司分成金额"); key.add("shopto");
		// title.add("店铺分成金额"); key.add("shop");
		// if(b==1){
		// if(getMember(getLogin(uname)).getSuperadmin()==1)
		// title.add("系统分成金额"); key.add("systemone");
		// }
		// title.add("推荐人分成金额"); key.add("clerk");
		// title.add("管理员是否处理"); key.add("state");
		// ExportExcel.Export("结算报表,注意，这里的单位是（分），而不是（元）",title,key,listmap,ys,response);

		List<Map<String, Object>> list = selectSharingdetailsData(code, req, uname, star, end, 1, rows);
		MemberAuths ma = getMember(getLogin(uname));
		// int b =1;
		if (ma.getSuperadmin() != 1) {
			// if(ma.getmShop()==null||ma.getmShop().getSuperid()!=0)
			// b=3;
			// else
			// b=2;

			for (Map<String, Object> map : list) {
				// if(b==2){
				// map.remove("SHOPONE");
				// map.remove("SYSTEMONE");
				// }else{
				// map.remove("SYSTEMONE");
				// }
				try {
					map.put("PHONE", getMember(Long.valueOf(map.get("MEMBERID") + "")).getUname());
					map.put("PHONE1", getMember(Long.valueOf(map.get("MEMBERID1") + "")).getUname());
				} catch (Exception e) {
				}

				if (getMember(getLogin(uname)).getmShop().getSuperid() != 0) {
					map.remove("SHOPONE");
					map.remove("SHOPTO");
				}
				map.remove("SYSTEMONE");

			}
		} else {
			for (Map<String, Object> map : list) {
				try {
					map.put("PHONE", getMember(Long.valueOf(map.get("MEMBERID") + "")).getUname());
					map.put("PHONE1", getMember(Long.valueOf(map.get("MEMBERID1") + "")).getUname());
				} catch (Exception e) {
				}

			}
		}

		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("结算编号");
		key.add("id");
		title.add("结算时间");
		key.add("cdate");
		title.add("订单id");
		key.add("Ordersid");
		title.add("供应商");
		key.add("suppliername");
		// if(b==1||b==2){
		// if(getMember(getLogin(uname)).getSuperadmin()==1||getMember(getLogin(uname)).getmShop().getSuperid()==0)
		title.add("供应商分成金额");
		key.add("shopone");
		// }
		// if(getMember(getLogin(uname)).getSuperadmin()==1||getMember(getLogin(uname)).getmShop().getSuperid()==0)
		// title.add("供应商分成金额"); key.add("shopone");
		// if(getMember(getLogin(uname)).getSuperadmin()==1)
		title.add("分公司分成金额");
		key.add("shopto");
		// if(getMember(getLogin(uname)).getSuperadmin()==1||getMember(getLogin(uname)).getmShop().getSuperid()!=0)
		title.add("店铺分成金额");
		key.add("shop");
		// if(b==1){
		if (getMember(getLogin(uname)).getSuperadmin() == 1)
			title.add("系统分成金额");
		key.add("systemone");
		// }
		title.add("推荐人分成金额");
		key.add("clerk");
		title.add("管理员是否处理");
		key.add("state");
		ExportExcel.Export("结算报表,注意，这里的单位是（分），而不是（元）", title, key, list, response);
		// return sendTrueData(list);

	}

	private List<Map<String, Object>> selectSharingdetailsData(String supplier, HttpServletRequest req, String uname,
			Long star, Long end, Integer page, Integer rows) throws Exception {
		Sql msql = new Sql();
		star = star == null ? 0 : star;
		end = end == null ? System.currentTimeMillis() : end;
		msql.setPage(page);
		msql.setRows(rows);
		String where = "where Sharingdetails.id<" + end + " and Sharingdetails.id > " + star;
		if (!Stringutil.isBlank(supplier)) {
			where = where + " and suppliername='" + supplier + "' ";
		}
		// 基础sql1
		// String sql = "select
		// '时间'cdate,'汇总'id,'订单id'Ordersid,'供应商'suppliername,nvl(sum(shopone),0)
		// shopone,12345678910 memberid,12345678910 memberid1, "
		// + "nvl(sum(shopto),0) shopto, nvl(sum(shop),0) shop,
		// nvl(sum(systemone),0) systemone, nvl(sum(clerk),0) clerk,"
		// + "'处理结果'state from Sharingdetails "
		// + "left join orders on orders.id = Sharingdetails.Ordersid left join
		// "
		// + "Orderrelevance on Orderrelevance.ID = orders.ID left join
		// Commodity "
		// + "on Commodity.id = Orderrelevance.Itemid "
		// + where
		// + " union all select TO_CHAR(Sharingdetails.id / (1000 * 60 * 60 *
		// 24)"
		// + " + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),
		// 'YYYY-MM-DD HH24:MI:SS') AS CDATE ,"
		// +
		// "to_char(Sharingdetails.id),to_char(Ordersid),suppliername,shopone,memberid,memberid1,shopto,shop,systemone,clerk,"
		// + "decode(state,0,'未处理',1,'已处理') state from Sharingdetails left join
		// orders on "
		// + "orders.id=Sharingdetails.Ordersid left join Orderrelevance on
		// Orderrelevance.Ordernum "
		// + "= orders.ordernumber left join Commodity on
		// Commodity.id=Orderrelevance.Itemid "
		// + where;
		String sql = " select DISTINCT to_char(systemone+shopone+shopto+shop+clerk) allnumber,cdate,id,Ordersid,suppliername,shopone,memberid,memberid1,shopto,systemone,clerk,shop,state from (select "
				+ MyDate.orcaleCDATE("Sharingdetails.id") + "  ,"
				+ "to_char(Sharingdetails.id) id,to_char(Ordersid)Ordersid,suppliername,shopone,Sharingdetails.memberid,Sharingdetails.memberid1,shopto,shop,systemone,clerk,"
				+ "decode(state,0,'未处理',1,'已处理') state from Sharingdetails left join orders on "
				+ "orders.id=Sharingdetails.Ordersid left join Orderrelevance on Orderrelevance.orderid "
				+ "= orders.id left join Commodity on Commodity.id=Orderrelevance.Itemid " + where + ")";

		if (getMember(getLogin(uname)).getSuperadmin() != 1
				&& getMember(getLogin(uname)).getmShop().getSuperid() != 0) {
			sql = "select * from (" + sql + ") where ordersid in (select id from orders where shopid="
					+ getMember(getLogin(uname)).getmShop().getId() + ")";
		}
		if (getMember(getLogin(uname)).getSuperadmin() != 1
				&& getMember(getLogin(uname)).getmShop().getSuperid() == 0) {
			sql = "select * from (" + sql + ") where ordersid in (select id from orders where onephone=" + uname + ")";
		}

		String sql1 = "select '金额' allnumber,'时间'cdate,'汇总'id,'订单id'Ordersid,'供应商'suppliername,nvl(sum(shopone),0) shopone,12345678910 memberid,12345678910 memberid1, "
				+ "nvl(sum(shopto),0) shopto,nvl(sum(systemone),0) systemone, nvl(sum(clerk),0) clerk, nvl(sum(shop),0) shop, "
				+ "'处理结果'state from (" + sql + ") union all " + sql;

		msql.setSql(sql1);
		return mMemberService.exeSelectSql(msql);
	}

	/** 导出商品编号 */
	@Auth(admin = true)
	@RequestMapping(value = "/web/Commodity/expor", method = RequestMethod.GET)
	public void Commodityexpor(HttpServletRequest req, HttpServletResponse response, Long star, Long end)
			throws Exception {
		star = star == null ? 0 : star;
		end = end == null ? System.currentTimeMillis() : end;
		Sql msql = new Sql();
		msql.setRows(100000);
		msql.setSql("select LARGECLASS,INCLASS,SMALLCLASS,FINECLASS,NAME,ID  from Commoditykey where id > " + star
				+ " and id<" + end + " order by id");
		List<Map<String, Object>> listmap = mOrdersRuleService.exeSelectSql(msql);

		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("编号");
		key.add("id");
		title.add("大类");
		key.add("largeclass");
		title.add("中类");
		key.add("inclass");
		title.add("小类");
		key.add("smallclass");
		title.add("细类");
		key.add("fineclass");
		title.add("名字");
		key.add("name");
		ExportExcel.Export("商品编号", title, key, listmap, response);

	}

	// private List<Map<String, Object>> selectReturngoodsData(Long id,String
	// code,HttpServletRequest req,String uname,Long star,Long end,Integer
	// page,Integer rows) throws Exception {
	// Sql msql = new Sql();
	// star = star ==null?0:star;
	// end = end ==null?System.currentTimeMillis():end;
	// msql.setPage(page);
	// msql.setRows(rows);
	// //基础sql1
	// String sql = "select a.*,member.uname uname1 from (select
	// itemid,Returngoods.price * num priceall,refund*num
	// refundall,Returngoods.id,TO_CHAR(Returngoods.id / (1000 * 60 * 60 * 24) +
	// TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),"
	// + " 'YYYY-MM-DD HH24:MI:SS') AS CDATE
	// ,shop.memberid,uname,decode(istrue,-1,'不同意',0,'未处理',1,'已同意')istrue,reason,Returngoods.price
	// ,"
	// + "refund,refuse,shop.shopname,shop.code shopcode,title,num from
	// Returngoods "
	// + "left join orderrelevance on orderrelevanceid=orderrelevance.id left
	// join orders "
	// + "on orders.ordernumber=orderrelevance.ordernum left join member on
	// member.id="
	// + "Returngoods.Memberid left join shop on shop.id =Returngoods.shopid)a"
	// + " left join member on member.id=memberid where a.id<"+end+" and a.id >
	// "+star;
	//
	// if(id!=null)
	// sql=sql+ " and Returngoods.id="+id;
	//
	// if(getMember(getLogin(uname)).getSuperadmin()==1){
	// if(!Stringutil.isBlank(code))sql=sql+ " and shopcode='"+code+"'";
	// msql.setSql(sql);
	// }else{
	// try {
	// code = getMember(getLogin(uname)).getmShop().getCode();
	// sql=sql+ " and shop.code='"+code+"'";
	// } catch (Exception e) {
	// throw new AuthException("权限不足");
	// }
	// }
	//
	// msql.setSql(sql);
	// return mMemberService.exeSelectSql(msql);
	// }
	//
	//

	// 门店邮费
	private List<Map<String, Object>> getShopPostfee(int type, String shopcode, String uname, Long star, Long end,
			Integer page, Integer rows) throws Exception {
		if (getMember(getLogin(uname)).getSuperadmin() != 1)
			shopcode = getMember(getLogin(uname)).getmShop().getCode();

		Sql msql = new Sql();
		star = star == null ? 0 : star;
		end = end == null ? System.currentTimeMillis() : end;
		msql.setPage(page);
		msql.setRows(rows);

		String sql = "select * from shop ";
		if (!Stringutil.isBlank(shopcode))
			sql = sql + "where code = '" + shopcode + "'";

		msql.setSql(sql);
		List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
		if (listmap.size() == 0)
			throw new RunException("暂无数据");

		List<Shop> listshop = IBeanUtil.ListMap2ListJavaBean(listmap, Shop.class);

		List<Map<String, Object>> listmap1 = new ArrayList<Map<String, Object>>();// 返回数据
		List<Map<String, Object>> listmap2;// 存放订单
		for (Shop mShop : listshop) {
			msql.setPage(null);
			msql.setRows(null);

			sql = "select " + MyDate.orcaleCDATE("orders.ID", "TIME")
					+ ",orders.* ,uname from orders left join Friends on memberidb=memberid "
					+ "left join member on memberida=member.id where shopid=" + mShop.getId() + " and orders.id>" + star
					+ " and orders.id <" + end;
			msql.setSql(sql);
			listmap2 = mMemberService.exeSelectSql(msql);
			if (listmap2.size() == 0)
				continue;// 没有订单

			sql = "select shop+clerk sc,OrdersRule.* from OrdersRule where shoponeid=" + mShop.getOneid();
			msql.setPage(1);
			msql.setRows(1);
			msql.setSql(sql);
			listmap = mMemberService.exeSelectSql(msql);

			for (Map<String, Object> map1 : listmap2) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("SHOPCODE", mShop.getCode());
				map.put("SHOPNAME", mShop.getShopname());
				map.put("ORDERSID", map1.get("ID"));
				map.put("TIME", map1.get("TIME"));
				if (listmap.size() == 0)
					map.put("POSTFEE", "店铺供应商还未与系统协商规则");
				else {
					if (type == 1) {
						map.put("POSTFEE",
								OrdersController.getpostfee(new BigDecimal(map1.get("PAYMENT").toString()), 3)
										.multiply(new BigDecimal(listmap.get(0).get("SHOP").toString()))
										.divide(new BigDecimal(listmap.get(0).get("SC").toString())));
					} else {
						map.put("POSTFEE",
								OrdersController.getpostfee(new BigDecimal(map1.get("PAYMENT").toString()), 3)
										.multiply(new BigDecimal(listmap.get(0).get("CLERK").toString()))
										.divide(new BigDecimal(listmap.get(0).get("SC").toString())));
						map.put("CLERK", map1.get("UNAME"));
					}

				}

				listmap1.add(map);
			}

		}

		return listmap1;
	}

	private List<Map<String, Object>> getshopone(Integer type, String code, String uname, Long star, Long end,
			Integer page, Integer rows) throws Exception {
		if (getMember(getLogin(uname)).getSuperadmin() != 1)
			if (getMember(getLogin(uname)).getmShop() == null)
				throw new RunException("权限不足");
		Sql msql = new Sql();
		star = star == null ? 0 : star;
		end = end == null ? System.currentTimeMillis() : end;
		msql.setPage(page);
		msql.setRows(rows);

		String sql1 = "select " + MyDate.orcaleCDATE("orders.id", "time")
				+ ",orders.postfee,shopcode,shop.shopname,orders.id ordersid,orderrelevance.id, orders.PAYMENT,'售货' type from Orderrelevance left join orders on orders.id=orderid  left  "
				+ "join stock on stock.code=youcode left join shop on shop.code=shopcode left join Returngoods "
				+ "on orderrelevanceid =orderrelevance.id where orders.status!=1 and orders.status<10  and orders.id>"
				+ star + " and orders.id <" + end;
		String sql2 = "union all " + "select " + MyDate.orcaleCDATE("orders.id", "time")
				+ ",orders.postfee,shopcode,shop.shopname,orders.id ordersid,Returngoods.id, Returngoods.refund,'退货' from Orderrelevance left join orders on orders.id=orderid  left  "
				+ "join stock on stock.code=youcode left join shop on shop.code=shopcode left join Returngoods "
				+ "on orderrelevanceid =orderrelevance.id where orders.status!=1 and orders.status<10 and Returngoods.istrue>0 and orders.id>"
				+ star + " and orders.id <" + end;
		if (!Stringutil.isBlank(code)) {
			sql1 = sql1 + " and shopcode = '" + code + "'";
			sql2 = sql2 + " and shopcode = '" + code + "'";
		}

		msql.setSql(sql1 + sql2);

		List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
		for (Map<String, Object> map : listmap) {
			if (map.get("TYPE").equals("退货"))
				map.put("POSTFEE", OrdersController.getpostfee(new BigDecimal(map.get("PAYMENT").toString()), 1));
			else
				map.put("POSTFEE", OrdersController.getpostfee(new BigDecimal(map.get("PAYMENT").toString()), 2));
		}

		return listmap;
	}

	/**
	 * 店铺邮费或店员邮费
	 * 
	 * @param star
	 *            开始时间，
	 * @param end
	 *            结束时间
	 * 
	 */
	@Auth
	@RequestMapping(value = "/web/ShopPostfee", method = RequestMethod.POST)
	public RequestType select(Integer type, String code, String uname, Long star, Long end, Integer page, Integer rows,
			HttpServletResponse response) throws Exception {
		if (type == null)
			type = 1;
		if (type == 3)
			return sendTrueData(getshopone(type, code, uname, star, end, page, rows));
		return sendTrueData(getShopPostfee(type, code, uname, star, end, page, rows));
	}

	@Auth
	@RequestMapping(value = "/web/exportShopPostfee", method = RequestMethod.GET)
	public void exportOrderrelevance(Integer type, String code, String uname, Long star, Long end, Integer rows,
			HttpServletResponse response) throws Exception {
		if (type == null)
			type = 1;
		List<Map<String, Object>> listmap;
		if (type == 3)
			listmap = getshopone(type, code, uname, star, end, 1, rows);
		else
			listmap = getShopPostfee(type, code, uname, star, end, 1, rows);
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		if (type == 1 || type == 2) {
			title.add("店铺编号");
			key.add("SHOPCODE");
			title.add("店铺名字");
			key.add("SHOPNAME");
		}
		if (type == 3) {
			title.add("供应商编号");
			key.add("SHOPCODE");
			title.add("供应商名字");
			key.add("SHOPNAME");
			title.add("销售编号/退货编号");
			key.add("ID");
			title.add("订单金额");
			key.add("PAYMENT");
			title.add("费用类型");
			key.add("TYPE");
		}
		title.add("订单时间");
		key.add("TIME");
		title.add("订单编号");
		key.add("ORDERSID");
		title.add("邮费");
		key.add("POSTFEE");
		if (type == 2) {
			title.add("导购账号");
			key.add("UNAME");
		}

		ExportExcel.Export("邮费报表", title, key, listmap, response);

	}
	
	public  Map<String, Object> getRecommend2(String uname) throws Exception{
		Map<String, Object> map =new HashMap<String, Object>();
		Sql msql = new Sql();
		//结算规则
		  List<OrdersRule> lOrdersRule;//结算规则
		  msql.setSql("select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
		   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id = (select shopid from clerk where memberid="+getLogin(uname).getUserid()+") ) and nvl(type,1)=1 order by id desc)"
		   				+ "where rownum=1"
		   				+ " union all select id,shoponeid,shopone,shopto,shop,systemone,clerk,other,type from ("
		   		+ "select * from OrdersRule where shoponeid=(select oneid from shop where id =  (select shopid from clerk where memberid="+getLogin(uname).getUserid()+") ) and nvl(type,1)=2 order by id desc)"
		   				+ "where rownum=1");
		   lOrdersRule =  IBeanUtil.ListMap2ListJavaBean(mMemberService.exeSelectSql(msql),OrdersRule.class);
		   if(lOrdersRule.size()!=2)throw new RunException("您的供应商还没有跟系统协商规则");
			   OrdersRule or ;
		
		Long time=1538323200000L;//2018年10月1日0点0分0秒
		//未入账金额	   
		//百分之2的分成金额
		msql.setSql("select nvl(sum(payment),0) payment from orders where status in(2,3,4,6,7,8) and id not in(select ordersid from Sharingdetails) and shippingtype=3 and "
				+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+") and id>"+time);
		BigDecimal b =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
		or=lOrdersRule.get(1);
		//实得金额
		b=b.multiply(or.getClerk());
		
		//百分之10分成
		msql.setSql("select nvl(sum(payment),0) payment  from orders where status in(2,3,4,6,7,8) and id not in(select ordersid from Sharingdetails) and shippingtype!=3 and "
				+ " memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and id>"+time);
		BigDecimal b1 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
		or=lOrdersRule.get(0);
		
		msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid  where status in(2,3,4,6,7,8) and orders.id not in(select ordersid from Sharingdetails)  and "
				+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time);
		b1=b1.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
		//导购拼单+4
		msql.setSql("select nvl((sum(a)-count(*))*400,0)count from(select sum(num)a,Commoditykeyid,orderid from Orderrelevance left join Commodity on itemid=Commodity.id "
				+ "left join orders on orders.id=orderid "
				+ "where status in(2,3,4,6,7,8) and orders.id not in(select ordersid from Sharingdetails)  and "
				+ " orders.memberid in(select memberidb from Friends where memberida="+getLogin(uname).getUserid()+")and  orders.shippingtype!=3 and Orderrelevance.id>"+time
				+" group by Commoditykeyid,orderid)");
		
		//分成金额
		b1=b1.multiply(or.getClerk());
		//实得金额
		b1=b1.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
			   
		//已入账金额
		//百分之2的分成金额
		msql.setSql("select nvl(sum(clerk),0)"
				+ " payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype=3 and Sharingdetails.memberid="+
				getLogin(uname).getUserid()
				+" and orders.id>"+time);
		BigDecimal b2 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
		//百分之10分成
		msql.setSql("select nvl(sum(clerk),0) payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype!=3 and Sharingdetails.memberid="+
		getLogin(uname).getUserid()+" and orders.id>"+time);
		BigDecimal b3 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
		
		if(getMember(uname).getmShop()!=null){
			//未入账金额	   
			//百分之2的分成金额
			msql.setSql("select nvl(sum(payment),0) payment from orders where status in(2,3,4,6,7,8) and id not in(select ordersid from "
					+ "Sharingdetails) and shippingtype=3 and shopid="+getMember(uname).getmShop().getId()+" and id>"+time);
			BigDecimal b4 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
			or=lOrdersRule.get(1);
			b4=b4.multiply(or.getShop());
			//百分之10分成
			msql.setSql("select nvl(sum(payment) ,0)payment  from orders where status in(2,3,4,6,7,8) and id not in(select ordersid from "
					+ "Sharingdetails) and shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and id>"+time);
			BigDecimal b5 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
			or=lOrdersRule.get(0);
			
			msql.setSql("select nvl(sum(num)*9,0) count  from Orderrelevance left join orders on orders.id=orderid "
					+ " where status in(2,3,4,6,7,8) and orders.id not in(select ordersid from "
					+ "Sharingdetails) and orders.shippingtype!=3 and shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time);
			
			b5=b5.subtract(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("COUNT").toString()));
			b5=b5.multiply(or.getShop());
			
			//已入账金额
			//百分之2的分成金额
			msql.setSql("select nvl(sum(shop),0) payment from Sharingdetails left join orders on orders.id=ordersid where "
					+ "shippingtype=3  and shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time);
			BigDecimal b6 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
			//百分之10分成
			msql.setSql("select  nvl(sum(shop),0) payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype!=3 and "
					+ "  shopid="+getMember(uname).getmShop().getId()+" and orders.id>"+time);
			BigDecimal b7 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
			
			if(getMember(uname).getmShop().getSuperid()!=0){
				map.put("suma", b2.add(b3).add(b6).add(b7).divide(new BigDecimal(100)));
				map.put("sumb", b.add(b1).add(b4).add(b5).divide(new BigDecimal(100)));
//				//总价
//				msql.setSql("select (nvl(sum(shop)-sum(getpostfee(shopone+shopto+shop+systemone+clerk,1)),0)+nvl(sum(shop),0))/100 sum from "
//						+ "Sharingdetails left join orders on ordersid=orders.id where shopid="+
//						getMember(uname).getmShop().getId()+" and state=1 and Sharingdetails.id>"+time);
//				map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
				
				//作为导购的情况下金额
				msql.setSql("select (nvl(sum(clerk),0))/100 sum from "
						+ "Sharingdetails where memberid="+
						getLogin(uname).getUserid()+" and state=1 and Sharingdetails.id>"+time);
				map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
				
				//作为店铺的情况下金额
				msql.setSql("select (nvl(sum(shop),0))/100 sum from "
						+ "Sharingdetails left join orders on ordersid=orders.id where shopid="+
						getMember(uname).getmShop().getId()+" and state=1 and Sharingdetails.id>"+time);
				map.put("sumc", new BigDecimal(map.get("sumc").toString()).add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("SUM").toString())) );
				
			}else{
				//未入账金额	   
				//百分之2的分成金额
				msql.setSql("select nvl(sum(payment),0) payment from orders where status in(2,3,4,6,7,8) and "
						+ "id not in(select ordersid from Sharingdetails) and shippingtype=3 and onephone="+uname+ " and id>"+time);
				BigDecimal b8 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
				or=lOrdersRule.get(1);
				b8=b8.multiply(or.getShopone());
				//百分之10分成
				msql.setSql("select nvl(sum(payment) ,0)payment  from orders where status in(2,3,4,6,7,8) and id not "
						+ "in(select ordersid from Sharingdetails) and shippingtype!=3 and onephone="+uname+"  and id>"+time);
				BigDecimal b9 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
				or=lOrdersRule.get(0);
				b9=b9.multiply(or.getShopone());
				
				//每单+9快
				msql.setSql("select count(*)*900 PAYMENT from orders where status in(2,3,4,6,7,8) and id not "
						+ "in(select ordersid from Sharingdetails) and shippingtype!=3 and onephone="+uname+"  and id>"+time);
				
				b9 =b9.add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString()));
				
				//已入账金额
				//百分之2的分成金额
				msql.setSql("select nvl(sum(shop),0) payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype=3 and Sharingdetails.memberid in"
						+ "(select memberid from clerk where shopid in(select id from shop where onephone="+uname+")) and Sharingdetails.id>"+time);
				BigDecimal b10 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
				//百分之10分成
				msql.setSql("select  nvl(sum(shop),0) payment from Sharingdetails left join orders on orders.id=ordersid where shippingtype!=3 and Sharingdetails.memberid in"
						+ "(select memberid from clerk where shopid in(select id from shop where onephone="+uname+")) and Sharingdetails.id>"+time);
				BigDecimal b11 =new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("PAYMENT").toString());
			
				
				map.put("suma", b2.add(b3).add(b6).add(b7).add(b10).add(b11).divide(new BigDecimal(100)));
				map.put("sumb", b.add(b1).add(b4).add(b5).add(b8).add(b9).divide(new BigDecimal(100)));
				
				//作为导购的情况下金额
				msql.setSql("select (nvl(sum(clerk),0))/100 sum from "
						+ "Sharingdetails where memberid="+
						getLogin(uname).getUserid()+" and state=1 and Sharingdetails.id>"+time);
				map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
				
				//作为店铺的情况下金额
				msql.setSql("select (nvl(sum(shop),0))/100 sum from "
						+ "Sharingdetails left join orders on ordersid=orders.id where shopid="+
						getMember(uname).getmShop().getId()+" and state=1 and Sharingdetails.id>"+time);
				map.put("sumc", new BigDecimal(map.get("sumc").toString()).add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("SUM").toString())) );
				//作为供应商的情况下金额
				msql.setSql("select (nvl(sum(shopone),0))/100 sum from Sharingdetails "
						+ "left join orders on ordersid=orders.id where onphone="+
						uname+" and state=1 and Sharingdetails.id>"+time);
				map.put("sumc", new BigDecimal(map.get("sumc").toString()).add(new BigDecimal(mMemberService.exeSelectSql(msql).get(0).get("SUM").toString())) );
				
				
			}
			
		}else{
			map.put("suma", b2.add(b3).divide(new BigDecimal(100)));
			map.put("sumb", b.add(b1).divide(new BigDecimal(100)));
			
			msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk,1)),0)/100 sum from Sharingdetails where memberid="+
					getLogin(uname).getUserid()+" and state=1 and Sharingdetails.id>"+time);
			map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
		}
		

	return map;
		
		
	}

	/**
	 * 我的推荐收入2.0 type，身份，1，店员，2店长，3，供应商
	 */
	@Auth
	@RequestMapping(value = "/web/Recommend2", method = RequestMethod.POST)
	public RequestType Recommend2(Integer state,String uname, Integer rows, Integer page, Integer type, String phone,Long star,Long end)
			throws Exception {
		if(rows==null)rows=10;
		if(type==null)type=1;
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		Map< String, Object> map = new HashMap<String, Object>();
		Sql msql = new Sql();
		msql.setSql("select Sharingdetails.*,decode(shippingtype,1,'自提',2,'送货',3,'门店直销')shippingtype,ORDERS.SHOPNAME,payment from Sharingdetails left join orders on ordersid=orders.id where Sharingdetails.id<"+
		end+" and Sharingdetails.id>"+star);
		if(!Stringutil.isBlank(phone)){
			Map< String, Object> map1=getRecommend2(phone);
			map.put("suma", map1.get("suma"));
			map.put("sumb", map1.get("sumb"));
			map.put("sumc", map1.get("sumc"));
			if(type==1){
				msql.setSql(msql.getSql()+" and Sharingdetails.memberid="+getLogin(phone).getUserid());
			}else if(type==2){
				msql.setSql(msql.getSql()+" and Sharingdetails.memberid in(select memberid from clerk where shopid="+getMember(phone).getmShop().getId()+")");
			}else{
				msql.setSql(msql.getSql()+" and ordersid in(select id from shop where onephone="+phone+")");
			}
		}
		if(state!=null)
			msql.setSql(msql.getSql()+" state="+state);
		msql.setPage(page);
		msql.setRows(rows);
		List<Map<String, Object>> list = mMemberService.exeSelectSql(msql);
		for (Map<String, Object> map2 : list) {
			map2.put("PHONE",getMember(Long.valueOf(map2.get("MEMBERID").toString())).getUname());
			map2.put("PHONE1",getMember(Long.valueOf(map2.get("MEMBERID1").toString())).getUname());
		}
		map.put("list",list);
		
		return sendTrueData(map);
		
//		try {
//
//			if(type==null)type=1;
//			if (type != 1 && type != 2 && type != 3)
//				throw new RunException("类型错误");
//			
//
//			Map<String, Object> map = new HashMap<String, Object>();
//			Sql msql = new Sql();
//			if (getMember(uname).getSuperadmin() == 1) {
////				if (type == null)
////					type = 1;
//				if (Stringutil.isBlank(phone)) {
//
//					if (type == 1) {
//						// 总价
//						msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")),0)/100 sum from Sharingdetails where " + " Sharingdetails.id <"
//								+ (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//						
//						map.put("suma", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//						msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")) ,0)/100 sum from Sharingdetails where" + " Sharingdetails.id>"
//								+ (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//						map.put("sumb", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//						msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")),0)/100 sum from Sharingdetails where   state=1");
//						map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//						msql.setPage(page);
//						msql.setRows(rows);
//						
//						if(state==null){
//							msql.setSql(
//									"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.clerk/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//											+ "left join orders on Sharingdetails.ordersid=orders.id "
//											+ "left join Orderrelevance on Orderrelevance.orderid="
//											+ "orders.id where  orders.id is not null  group by orders.id,orders.payment,"
//											+ "Sharingdetails.state,clerk,"
//											+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//						}else{
//							msql.setSql(
//									"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.clerk/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//											+ "left join orders on Sharingdetails.ordersid=orders.id "
//											+ "left join Orderrelevance on Orderrelevance.orderid="
//											+ "orders.id where  orders.id is not null and Sharingdetails.state="+state
//											+" group by orders.id,orders.payment,Sharingdetails.state,clerk,"
//											+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//						}
//						
//					} else if (type == 2) {
//						// 总价
//						msql.setSql("select nvl(sum(shop)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where "
//								+ " Sharingdetails.id <" + (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//						map.put("suma", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//						msql.setSql("select nvl(sum(shop)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")) ,0)/100 sum from Sharingdetails " + "left join orders on ordersid=orders.id where "
//								+ " Sharingdetails.id>" + (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//						map.put("sumb", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//						msql.setSql("select nvl(sum(shop)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where "
//								+ " state=1");
//						map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//						msql.setPage(page);
//						msql.setRows(rows);
//						if(state==null){
//							msql.setSql(
//									"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.shop/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//											+ "left join orders on Sharingdetails.ordersid=orders.id "
//											+ "left join Orderrelevance on Orderrelevance.orderid="
//											+ "orders.id where  orders.id is not null   group by orders.id,orders.payment,"
//											+ "Sharingdetails.state,Sharingdetails.shop,"
//											+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//						}else{
//							msql.setSql(
//									"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.shop/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//											+ "left join orders on Sharingdetails.ordersid=orders.id "
//											+ "left join Orderrelevance on Orderrelevance.orderid="
//											+ "orders.id where  orders.id is not null  and Sharingdetails.state="+state
//											+ " group by orders.id,orders.payment,Sharingdetails.state,Sharingdetails.shop,"
//											+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//						}
//					
//					} else {
//
//						// 总价
//						msql.setSql("select nvl(sum(shopone)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where "
//								+ " Sharingdetails.id <" + (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//						map.put("suma", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//						msql.setSql("select nvl(sum(shopone)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")) ,0)/100 sum from Sharingdetails " + "left join orders on ordersid=orders.id where "
//								+ " Sharingdetails.id>" + (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//						map.put("sumb", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//						msql.setSql("select nvl(sum(shopone)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//								+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where "
//								+ " state=1");
//						map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//						msql.setPage(page);
//						msql.setRows(rows);
//						if(state==null){
//							msql.setSql(
//									"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.shopone/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//											+ "left join orders on Sharingdetails.ordersid=orders.id "
//											+ "left join Orderrelevance on Orderrelevance.orderid="
//											+ "orders.id where  orders.id is not null "
//											+ " group by orders.id,orders.payment,Sharingdetails.state,Sharingdetails.shopone,"
//											+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//						
//						}else{
//							msql.setSql(
//									"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.shopone/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//											+ "left join orders on Sharingdetails.ordersid=orders.id "
//											+ "left join Orderrelevance on Orderrelevance.orderid="
//											+ "orders.id where  orders.id is not null and Sharingdetails.state="+state
//											+ " group by orders.id,orders.payment,Sharingdetails.state,Sharingdetails.shopone,"
//											+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//							
//						}
//					
//					}
//					msql.setSql("select * from ("+msql.getSql()+") where id>"+star+" and id<"+end);
//					List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
//
//					// 最终金额
//					for (Map<String, Object> map2 : listmap) {
//						map2.put("FINAL", new BigDecimal(map2.get("CLERK").toString())
//								.subtract(new BigDecimal(map2.get("POSTFEE").toString())));
//						map2.put("phone1", getMember(Long.valueOf(map2.get("MEMBERID1").toString())).getUname());
//
//					}
//
//					map.put("data", listmap);
//					return sendTrueData(map);
//
//				} else {
//
//					uname = phone;
//				}
//			} else {
//				if (getMember(uname).getmShop() == null) {
//					type = 1;
//				} else if (getMember(uname).getmShop().getSuperid().toString().equals("0")) {
//					type = 3;
//				}
//
//				else
//					type = 2;
//
//			}
//
//			if (type == 1) {
//				// 总价
//				msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")),0)/100 sum from Sharingdetails where memberid=" + getLogin(uname).getUserid() + " and Sharingdetails.id <"
//						+ (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//				map.put("suma", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//				msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")) ,0)/100 sum from Sharingdetails where memberid=" + getLogin(uname).getUserid() + " and Sharingdetails.id>"
//						+ (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//				map.put("sumb", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//				msql.setSql("select nvl(sum(clerk)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")),0)/100 sum from Sharingdetails where memberid=" + getLogin(uname).getUserid()
//						+ " and state=1");
//				map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//				msql.setPage(page);
//				msql.setRows(rows);
//				if(state==null){
//					msql.setSql(
//							"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.clerk/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//									+ "left join orders on Sharingdetails.ordersid=orders.id "
//									+ "left join Orderrelevance on Orderrelevance.orderid="
//									+ "orders.id where  orders.id is not null and Sharingdetails.memberid="
//									+ getLogin(uname).getUserid() + " group by orders.id,orders.payment,Sharingdetails.state,clerk,"
//									+ "getpostfee(shopone + shopto + shop + systemone + clerk,"+type+"),memberid1 ");
//				}else{
//					msql.setSql(
//							"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.clerk/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//									+ "left join orders on Sharingdetails.ordersid=orders.id "
//									+ "left join Orderrelevance on Orderrelevance.orderid="
//									+ "orders.id where  orders.id is not null and Sharingdetails.state="+state
//									+ " and Sharingdetails.memberid="
//									+ getLogin(uname).getUserid() + " group by orders.id,orders.payment,Sharingdetails.state,clerk,"
//									+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//				}
//			
//			} else if (type == 2) {
//				// 总价
//				msql.setSql("select nvl(sum(shop)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where shopid="
//						+ getMember(uname).getmShop().getId() + " and Sharingdetails.id <"
//						+ (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//				map.put("suma", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//				msql.setSql("select nvl(sum(shop)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")) ,0)/100 sum from Sharingdetails " + "left join orders on ordersid=orders.id where shopid="
//						+ getMember(uname).getmShop().getId() + " and Sharingdetails.id>"
//						+ (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//				map.put("sumb", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//				msql.setSql("select nvl(sum(shop)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where shopid="
//						+ getMember(uname).getmShop().getId() + " and state=1");
//				map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//				msql.setPage(page);
//				msql.setRows(rows);
//				if(state==null){
//				msql.setSql(
//						"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.shop/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//								+ "left join orders on Sharingdetails.ordersid=orders.id "
//								+ "left join Orderrelevance on Orderrelevance.orderid="
//								+ "orders.id where  orders.id is not null and orders.shopid="
//								+ getMember(uname).getmShop().getId()
//								+ " group by orders.id,orders.payment,Sharingdetails.state,Sharingdetails.shop,"
//								+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//				}else{
//					msql.setSql(
//							"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.shop/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//									+ "left join orders on Sharingdetails.ordersid=orders.id "
//									+ "left join Orderrelevance on Orderrelevance.orderid="
//									+ "orders.id where  orders.id is not null and orders.shopid="
//									+ getMember(uname).getmShop().getId()
//									+ " and Sharingdetails.state="+state 
//									+ " group by orders.id,orders.payment,Sharingdetails.state,Sharingdetails.shop,"
//									+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//				}
//				
//			} else {
//
//				// 总价
//				msql.setSql("select nvl(sum(shopone)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where onephone="
//						+ uname + " and Sharingdetails.id <" + (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//				map.put("suma", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//				msql.setSql("select nvl(sum(shopone)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")) ,0)/100 sum from Sharingdetails " + "left join orders on ordersid=orders.id where onephone="
//						+ uname + " and Sharingdetails.id>" + (System.currentTimeMillis() - MyParameter.ReturnOrdersTime));
//				map.put("sumb", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//
//				msql.setSql("select nvl(sum(shopone)-sum(getpostfee(shopone+shopto+shop+systemone+clerk," + type
//						+ ")),0)/100 sum from Sharingdetails" + " left join orders on ordersid=orders.id where onephone="
//						+ uname + " and state=1");
//				map.put("sumc", mMemberService.exeSelectSql(msql).get(0).get("SUM"));
//				msql.setPage(page);
//				msql.setRows(rows);
//				if(state==null){
//				msql.setSql(
//						"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.shopone/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//								+ "left join orders on Sharingdetails.ordersid=orders.id "
//								+ "left join Orderrelevance on Orderrelevance.orderid="
//								+ "orders.id where  orders.id is not null and orders.onephone=" + uname
//								+ " group by orders.id,orders.payment,Sharingdetails.state,Sharingdetails.shopone,"
//								+ "getpostfee(shopone + shopto + shop + systemone + clerk,"+type+"),memberid1 ");
//				}else{
//					msql.setSql(
//							"select sum(num) num,orders.id,orders.payment ,Sharingdetails.state,Sharingdetails.shopone/100 clerk,getpostfee(shopone+shopto+shop+systemone+clerk,"+type+") postfee,memberid1 from Sharingdetails "
//									+ "left join orders on Sharingdetails.ordersid=orders.id "
//									+ "left join Orderrelevance on Orderrelevance.orderid="
//									+ "orders.id where  orders.id is not null and orders.onephone=" + uname
//									+ " and Sharingdetails.state="+state 
//									+ " group by orders.id,orders.payment,Sharingdetails.state,Sharingdetails.shopone,"
//									+ "getpostfee(shopone + shopto + shop + systemone + clerk, "+type+"),memberid1 ");
//				}
//			}
//			
//			msql.setSql("select * from ("+msql.getSql()+") where id>"+star+" and id<"+end);
//			List<Map<String, Object>> listmap = mMemberService.exeSelectSql(msql);
//
//			// 最终金额
//			for (Map<String, Object> map2 : listmap) {
//				map2.put("FINAL", new BigDecimal(map2.get("CLERK").toString())
//						.subtract(new BigDecimal(map2.get("POSTFEE").toString())));
//				map2.put("phone1", getMember(Long.valueOf(map2.get("MEMBERID1").toString())).getUname());
//
//			}
//
//			map.put("data", listmap);
//			map.put("istabletrue", listmap.size()==rows);
//			return sendTrueData(map);
//
//		
//		} catch (Exception e) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("data", new ArrayList<>());
//			map.put("suma", 0);
//			map.put("sumb", 0);
//			map.put("sumc", 0);
//			return sendTrueData(map);
//		}
	}
	/**
	 * 导出报表2.0 type，身份，1，店员，2店长，3，供应商
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/web/EXRecommend2", method = RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public void EXRecommend2(Integer state,String uname, Integer rows, Integer page, Integer type,
			String phone,Long star,Long end, HttpServletResponse response)
			throws Exception {
		RequestType ret = Recommend2(state, uname, rows, page, type, phone, star, end);
		
		Map<String, Object> map = (Map<String, Object>) ret.getData();
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		title.add("订单金额");    key.add("PAYMENT");
		title.add("导购分成"); key.add("CLERK");
		title.add("供应商分成"); key.add("SHOPONE");
		title.add("店铺分成"); key.add("SHOP");
		title.add("订单编号"); key.add("ORDERSID");
		title.add("绑定账户"); key.add("PHONE");
		title.add("购买用户"); key.add("PHONE1");
		title.add("送货方式"); key.add("SHIPPINGTYPE");
		title.add("店铺名字"); key.add("SHOPNAME");
		title.add("状态：0未清算1，已清算"); key.add("STATE");
		
		List<Map<String, Object>> listmap = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> listmap1 = (List<Map<String, Object>>) map.get("list");
		for (Map<String, Object> map2 : listmap1) {
			try {
				map2.put("CLERK" ,new BigDecimal(map2.get("CLERK").toString()).divide(new BigDecimal(100)));
				map2.put("SHOP" ,new BigDecimal(map2.get("SHOP").toString()).divide(new BigDecimal(100)));
				map2.put("SHOPONE" ,new BigDecimal(map2.get("SHOPONE").toString()).divide(new BigDecimal(100)));
				map2.put("SYSTEM" ,new BigDecimal(map2.get("SYSTEM").toString()).divide(new BigDecimal(100)));
				
			} catch (Exception e) {
			}
		
		}
		
		Map<String, Object> map1 = new HashMap<String, Object>();
		try {
			map1.put("PAYMENT","已入账金额合计："+map.get("suma").toString());
			map1.put("CLERK","未入账金额合计："+map.get("sumb").toString());
			map1.put("SHOPONE","已结算金额合计："+map.get("sumc").toString());
			listmap.add(map1);
		} catch (Exception e) {
		}
		
		
		listmap.addAll(listmap1);
		ExportExcel.Export("佣金", title, key, listmap, response);
		
	}
	
	
	/**
	 * 导购和用户查询
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/web/clerk2", method = RequestMethod.POST)
	public RequestType clerk2(String uname, Integer rows, Integer page, Integer type, String str)
			throws Exception {
		if(type==null)type=1;
		String sql="";
		if(type==1){
			sql="select member.*,shopname from member left join Friends on memberidb=member.id "
					+ "left join clerk on memberid=memberida left join shop on shop.id=shopid where 1=1 ";
		}else{
			sql="select member.*,shopname from clerk left join member on member.id=memberid left join shop on shop.id=shopid where 1=1 ";
			
		}
		if(!Stringutil.isBlank(str)){
			sql= sql+" and (member.uname like '%"+str+"%' or shopname like '%"+str+"%' )";
		}
		if(rows==null)rows=10;
		Sql msql = new Sql();
		msql.setSql(sql);
		msql.setRows(rows);
		msql.setPage(page);		
		return sendTrueData(mMemberService.exeSelectSql(msql));
		
	}

    /**
     * 退款查询
     * */
    @Auth(admin=true)
    @RequestMapping(value="/Refundresponse/select",method=RequestMethod.POST)
    public RequestType select(Integer rows,Integer page,Refundresponse mRefundresponse) throws Exception{
    	return sendTrueData(mRefundresponseService.getALL(mRefundresponse,rows,page));
    }
}
