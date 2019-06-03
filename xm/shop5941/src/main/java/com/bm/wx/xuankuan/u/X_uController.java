package com.bm.wx.xuankuan.u;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.Sql;
import com.bm.base.excle.ExportExcel;
import com.bm.base.excle.ReadExcel;
import com.bm.base.interceptor.Auth_wx;
import com.bm.base.interceptor.Auth_wx.x_key_ke;
import com.bm.base.redis.RedisUtils;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.bm.wx.xuankuan.sp.XSp;
import com.bm.wx.xuankuan.sp.XSpService;
import com.bm.wx.xuankuan.stock.XStock;
import com.bm.wx.xuankuan.stock.XstockService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;


@RestController
@Api(tags = "微信选款")
public class X_uController extends BaseController {
	
	@Autowired
	private X_UService mX_UService;
	@Autowired
	private XSpService mSpService;
	@Autowired
	private XstockService mXstockService;
	
	
	/**
	 * 注册
	 * */
	@Auth_wx(x_key=x_key_ke.admin)
	@RequestMapping(value ="/x_u/add", method = RequestMethod.POST) 
	public RequestType add(X_u mX_u) throws Exception{
		if (Stringutil.isBlank(mX_u.getX_name()))
			return sendFalse("用户名不可为空！");
		if (Stringutil.isBlank(mX_u.getX_pwd()))
			return sendFalse("密码不可为空！");
		if (Stringutil.isBlank(mX_u.getX_m_name()))
			return sendFalse("所属门店名字不可为空！");
		if (Stringutil.isBlank(mX_u.getX_m_code()))
			return sendFalse("所属门店变化不可为空！");
		if (mX_u.getEx()==null)
			return sendFalse("身份错误");
		
		if(mX_UService.getByparameter("x_name", mX_u.getX_name())!=null)return sendFalse("账号已存在");
		
		boolean b= false;
		for (x_key_ke e : x_key_ke.values()) {
				if(e.getKey()==mX_u.getEx()){
					b=true;
					break;
				}
			
		}
		if(!b)return sendFalse("身份错误");
		mX_u.setX_t(0);
		mX_UService.add(mX_u);
		return sendTrueMsg("添加成功");
	}
	/**
	 * 批量导入用户
	 */
	@Auth_wx(x_key=x_key_ke.admin)
	@RequestMapping(value = "/x_u/r_Excel", method = RequestMethod.POST)
	@Transactional
	public RequestType r_Excel(HttpServletRequest req) throws Exception {
		long time = System.currentTimeMillis();
		long ida = time;
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			int i;
			for (i=0;i<lls.size();i++) {
				try {
					X_u mX_u = new X_u();
					int j=0;
					mX_u.setId(ida);
					ida=ida+1;
					mX_u.setX_name(lls.get(i).get(j));j=j+1;//账号
					mX_u.setX_pwd(lls.get(i).get(j));j=j+1;//密码
					mX_u.setEx(Integer.parseInt(lls.get(i).get(j)));j=j+1;//等级
					mX_u.setX_m_name(lls.get(i).get(j));j=j+1;//所属门店名字
					mX_u.setX_m_code(lls.get(i).get(j));j=j+1;//所属门店编号
					mX_u.setX_b(lls.get(i).get(j));j=j+1;//经理账号
					mX_u.setX_t(0);//状态，0：正常，-1，已冻结
					mX_UService.add(mX_u);
				} catch (Exception e) {
					throw new RunException("第"+(i+2)+"行错误，数据不足，请检查");
				}
				
			}
			
			
			
			return sendTrueMsg("导入成功，此次一共导入" + i + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	/**
	 * 模糊解锁门店
	 * */
	@Auth_wx(x_key=x_key_ke.jing_l)
	@RequestMapping(value ="/x_u/r_s_l", method = RequestMethod.POST) 
	public RequestType r_s_l(String str) throws Exception{
		if (Stringutil.isBlank(str))str="";
				//return sendFalse("内容不可为空！");
		Sql msql = new Sql();
		msql.setSql("select x_m_name,x_m_code from xu where x_m_name like '%"+str+"%' or x_m_code like '"+str+"' group by x_m_name,x_m_code");
		List<Map<String, Object>> listmap = mX_UService.exeSelectSql(msql);
		if(listmap.size()==0){
			msql.setSql("select x_m_name,x_m_code from xu where x_name = '"+str+"' ");
			 listmap = mX_UService.exeSelectSql(msql);
		}
		
		return sendTrueData(listmap);
		
	}
	/**
	 * 重置用户信息
	 * */
	@Auth_wx(x_key=x_key_ke.admin)
	@RequestMapping(value ="/x_u/r_pwd", method = RequestMethod.POST) 
	public RequestType r_pwd(X_u mX_u) throws Exception{
		if (Stringutil.isBlank(mX_u.getX_name()))
			return sendFalse("用户名不可为空！");
		X_u mX_u1= mX_UService.getByparameter("x_name", mX_u.getX_name(),X_u.class);
		if(mX_u1==null) return sendFalse("用户不存在");
		mX_u.setId(mX_u1.getId());
		mX_UService.updateBySelect(mX_u);
		
		
		return sendTrueMsg("修改成功");
	}
	/**
	 * 冻结或解冻用户
	 * */
	@Auth_wx(x_key=x_key_ke.admin)
	@RequestMapping(value ="/x_u/r_t", method = RequestMethod.POST) 
	public RequestType r_t(String x_name,Integer x_t) throws Exception{
		if (Stringutil.isBlank(x_name))
			return sendFalse("用户名不可为空！");
		if(x_t==null)return sendFalse("操作错误");
		X_u mX_u= mX_UService.getByparameter("x_name", x_name,X_u.class);
		if(mX_u==null) return sendFalse("用户不存在");
		if(x_t!=0&&x_t!=-1)return sendFalse("操作错误");
		mX_u.setX_t(x_t);
		mX_UService.updateBySelect(mX_u);
		return sendTrueMsg((x_t==0?"解冻":"冻结")+"成功");
	}
	
	/**
	 * 查询用户
	 * */
	@RequestMapping(value ="/x_u/r_s", method = RequestMethod.POST) 
	@Auth_wx(x_key=x_key_ke.admin)
	public  RequestType r_s(X_u mX_u,Integer page,Integer rows) throws Exception{
		return sendTrueData(mX_UService.getALL(mX_u, page, rows));
	}
	/*
	 * 登录
	 * */
	@RequestMapping(value ="/x_u/x_l", method = RequestMethod.POST) 
	public  RequestType x_l(String pwd, String x_name) throws Exception{
		if (Stringutil.isBlank(x_name))
			return sendFalse("用户名不可为空！");
		if (Stringutil.isBlank(pwd))
			return sendFalse("密码不可为空！");
		X_u mX_u= mX_UService.getByparameter("x_name", x_name,X_u.class);
		if(mX_u==null)return sendFalse("账号不存在");
		if(mX_u.getX_t()==-1)return sendFalse("账号被冻结");
		if(!mX_u.getX_pwd().equals(pwd))return sendFalse("密码错误");
		String uid= UUID.randomUUID().toString().replace("-", "");
		mX_u.setX_z_time(System.currentTimeMillis());
		
		RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_user, uid, mX_u);
		mX_u.setX_pwd(null);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("user", mX_u);
		return sendTrueData(map);
	}

	
	/*
	 * 上传商品资料
	 * */
	@RequestMapping(value ="/sp/s_c", method = RequestMethod.POST) 
	@Auth_wx(x_key=x_key_ke.gong_ys)
	public  RequestType sp_s_c(XSp msp,HttpServletRequest req,String UID) throws Exception{
		X_u mX_u= getX_u(UID);
		if(msp==null)return sendFalse("最少选填一项");
			if (req instanceof StandardMultipartHttpServletRequest){
				msp.setA1(getfile(req, "b1"));
				msp.setA2(getfile(req, "b2"));
				msp.setA3(getfile(req, "b3"));
				msp.setA4(getfile(req, "b4"));
			}
			msp.setFa_b(0);
			if(mX_u.getEx()!=x_key_ke.admin.getKey()){
				msp.setGong_ysbh(mX_u.getX_m_code());
			}
//			msp.setGong_ysbh(mX_u.getX_m_code());
			if(Stringutil.isBlank(msp.getYan_s()))
					msp.setYan_s(XSpService.zwys+"*");
			else
				if(msp.getYan_s().substring(msp.getYan_s().length()-1).equals("\\*")||msp.getYan_s().substring(msp.getYan_s().length()-1).equals("*"))
					msp.setYan_s(msp.getYan_s().substring(0,msp.getYan_s().length()-1));
			/*msp.setZong_kc("0");
			msp.setSheng_ykc("0");
			msp.setDong_jkc("0");*/
			mSpService.add(msp);
			return sendTrueMsg("成功");
		
	}
	/*
	 * 上传商品资料
	 * */
	@RequestMapping(value ="/sp/aaaaaa", method = RequestMethod.POST) 
	public  RequestType aaaaa(XSp msp,HttpServletRequest req,String ukey) throws Exception{
		X_u mX_u= mX_UService.getById(1551680137597L,X_u.class);
		if(msp==null)return sendFalse("最少选填一项");
		if (req instanceof StandardMultipartHttpServletRequest){
			msp.setA1(getfile(req, "b1"));
			msp.setA2(getfile(req, "b2"));
			msp.setA3(getfile(req, "b3"));
			msp.setA4(getfile(req, "b4"));
		}
		msp.setFa_b(0);
		if(mX_u.getEx()!=x_key_ke.admin.getKey()){
			msp.setGong_ysbh(mX_u.getX_m_code());
		}
//			msp.setGong_ysbh(mX_u.getX_m_code());
		if(Stringutil.isBlank(msp.getYan_s()))
			msp.setYan_s(XSpService.zwys+"*");
		else
			if(msp.getYan_s().substring(msp.getYan_s().length()-1).equals("\\*")||msp.getYan_s().substring(msp.getYan_s().length()-1).equals("*"))
				msp.setYan_s(msp.getYan_s().substring(0,msp.getYan_s().length()-1));
		/*msp.setZong_kc("0");
		msp.setSheng_ykc("0");
		msp.setDong_jkc("0");*/
		mSpService.add(msp);
		return sendTrueMsg("成功");
		
	}
	/*
	 * 修改商品资料
	 * */
	@RequestMapping(value ="/sp/s_update", method = RequestMethod.POST) 
	@Auth_wx(x_key=x_key_ke.admin)
	public  RequestType sp_s_update(XSp msp,HttpServletRequest req) throws Exception{
		if(msp==null)return sendFalse("最少选填一项");
		if(msp.getId()==null)return sendFalse("编号错误");
		if (req instanceof StandardMultipartHttpServletRequest){
			msp.setA1(getfile(req, "b1"));
			msp.setA2(getfile(req, "b2"));
			msp.setA3(getfile(req, "b3"));
			msp.setA4(getfile(req, "b4"));
		}
		msp.setFa_b(0);
		mSpService.updateBySelect(msp);
		return sendTrueMsg("修改成功");
		
	}
	/*
	 * 发布商品资料
	 * */
	@RequestMapping(value ="/sp/s_up", method = RequestMethod.POST) 
	@Auth_wx(x_key=x_key_ke.admin)
	public  RequestType sp_s_up(XSp msp,String UID,Integer fa_bsj) throws Exception{
		if(msp==null)return sendFalse("最少选填一项");
		if(msp.getId()==null)return sendFalse("编号错误");
		if(msp.getFa_b()==null)msp.setFa_b(1);
		if(msp.getFa_b()!=1&&msp.getFa_b()!=0)return sendFalse("参数错误");
		if(msp.getFa_b()==0){
			Sql msql = new Sql();
			msql.setSql("update xsp set fa_b=0 where id="+msp.getId());
			mSpService.execSQL(msql);
			return sendTrueMsg("下架成功");
		}
	//	if(Stringutil.isBlank(msp.getZong_kc()))return sendFalse("库存错误");
		X_u mxu = getX_u(UID);
		if(mxu.getEx()!=x_key_ke.admin.getKey()){
			msp.setGong_ysbh(mxu.getX_m_code());
		}
		if(fa_bsj==null)fa_bsj=3;
		//String sb =mSpService.forString("0*", msp.getZong_kc().split(XSpService.fgf).length);
	//	msp.setDong_jkc(sb.substring(0,sb.length()-1));
	//	msp.setSheng_ykc(msp.getZong_kc());
		XSp mspa = mSpService.getById(msp,XSp.class);
		msp.setYan_s(mspa.getYan_s());
		msp.setJie_ssj(System.currentTimeMillis()+(fa_bsj*86400000L));
		mSpService.updateBySelect(msp);
		return sendTrueMsg("发布成功");
		
	}
	
	/*
	 * 查询商品资料
	 * */
	@RequestMapping(value ="/sp/s_up_s", method = RequestMethod.POST) 
	@Auth_wx(x_key=x_key_ke.gong_ys)
	public  RequestType sp_s_up_s(XSp msp,String UID,Integer page,Integer rows,String orderbykey,Integer orderbytype) throws Exception{
		X_u mX_u= getX_u(UID);
		if(msp.getFa_b()==null||msp.getFa_b()!=1){
			if(mX_u.getEx()!=x_key_ke.admin.getKey()){
				msp.setGong_ysbh(mX_u.getX_m_code());
			}
		}
		
		return sendTrueData(mSpService.getALL(msp, orderbykey, orderbytype, page, rows));
		
	}


	/*
	 * 查询已发布的商品资料
	 * */
	@RequestMapping(value ="/sp/s_up_s_t", method = RequestMethod.POST) 
	@Auth_wx(x_key={x_key_ke.jing_l,x_key_ke.men_d})
	public  RequestType sp_s_up_s_t(XSp msp,String UID,Integer page,Integer rows,String orderbykey,Integer orderbytype) throws Exception{
		msp.setFa_b(1);
		return sp_s_up_s(msp, UID, page, rows, orderbykey, orderbytype);
	}

	/*private String forString(String[] key,String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < key.length; i++) {
			sb.append(key[i]);
			sb.append(str);
		}
		return sb.toString();
		
	}*/
	/**private boolean updatekc(List<XSp> lsitXSp,String[] djkc,String[] sykc,String[] ys) {
		for (XSp xSp : lsitXSp) {
			for (int i = 0; i < ys.length; i++) {
				if(xSp.getYan_s().equals(ys[i])){
					Integer size =Integer.parseInt(djkc[i])+Integer.parseInt(xSp.getSheng_ykc());
					djkc[i]=size.toString();
					size =Integer.parseInt(sykc[i])-Integer.parseInt(xSp.getSheng_ykc());
					sykc[i]=size.toString();
					lsitXSp.remove(xSp);
					return true;
				}
			}
		}
		return false;

	}
	/*
	 *申请下单
	 * */
	@RequestMapping(value ="/xstock/x_d", method = RequestMethod.POST) 
	@Auth_wx()
	public  RequestType xstockx_d(XSp msp,String UID,String bz,String yan_s_kc_array) throws Exception{
		
		
		msp=mSpService.getById(msp,XSp.class);
		if(msp==null)return sendFalse("商品不存在");
		if(msp.getFa_b()!=1)return sendFalse("商品不可下单");
		if(System.currentTimeMillis()>msp.getJie_ssj())
			return sendFalse("商品已过期");
	//	String[] ys = msp.getYan_s().split(XSpService.fgf);
		//String[] djkc = msp.getDong_jkc().split(XSpService.fgf);
		//String[] sykc = msp.getSheng_ykc().split(XSpService.fgf);
		/**List<XSp> lsitXSp = GsonUtil.fromJsonList(yan_s_kc_array, XSp.class);
		for (;;) {
			if(!updatekc(lsitXSp, djkc, sykc, ys))break;
		}
		*/
	/*	String sb =forString(djkc,"*");
		msp.setDong_jkc(sb.substring(0,sb.length()-1));
		sb =forString(sykc,"*");
		msp.setSheng_ykc(sb.substring(0,sb.length()-1));
		mSpService.updateBySelect_orders(msp);*/
		
		X_u mxu = getX_u(UID);
		//if(Stringutil.isBlank(m_code))m_code=mxu.getX_m_code();
		//X_u mxuK = mX_UService.getByparameter("x_m_code", m_code,X_u.class);
		//if(mxuK==null)throw new RunException("错误，店铺编号不存在");
		XStock mXstock= new XStock();
		mXstock.setSuo_smdbh(mxu.getX_m_code());
		mXstock.setSp_id(msp.getId());
		if(mXstockService.getALL(mXstock).size()!=0)throw new RunException("错误，该商品已经下过单了");
		
		mXstock= new XStock();
		
		mXstock.setSuo_smdbh(mxu.getX_m_code());
		mXstock.setSp_id(msp.getId());
		mXstock.setId(System.currentTimeMillis());
		mXstock.setShen_qkc(yan_s_kc_array);
		mXstock.setShen_qr_name(mxu.getX_b());
		mXstock.setSuo_smdmz(mxu.getX_m_name());
		mXstock.setSp_name(msp.getShang_pm());
		mXstock.setShi_fty(0);
		mXstock.setShi_fxg(0);
		if(!Stringutil.isBlank(bz)){
			mXstock.setBei_z(System.currentTimeMillis()+":"+mxu.getX_name()+":"+bz+";");
		}
		mXstockService.add(mXstock);
		
		return sendTrueData(mXstock.getId());
	}
	/*
	 *修改下单
	 * */
	@RequestMapping(value ="/xstock/x_u", method = RequestMethod.POST) 
	@Auth_wx(x_key={x_key_ke.jing_l})
	public  RequestType xstockx_u(XStock mXStock,String UID,String bz,String m_code,String yan_s_kc_array) throws Exception{
		if(Stringutil.isBlank(yan_s_kc_array))
			return sendFalse("订单错误");
		
		mXStock= mXstockService.getById(mXStock,XStock.class);
		 if(mXStock==null) return sendFalse("订单不存在");
		 if(mXStock.getShi_fxg()==1)
			 return sendFalse("订单已选了数量");
		
		 mXStock.setShi_fxg(1);
		 mXStock.setShen_qkc(yan_s_kc_array);
		mXstockService.updateBySelect(mXStock);
		
		return sendTrueMsg("修改成功");
	}
//	public static void main(String[] args) {
//		XSp mxsp = new XSp();
//		mxsp.setSheng_ykc("50");
//		mxsp.setYan_s("红色");
//		XSp mxsp1 = new XSp();
//		mxsp1.setSheng_ykc("60");
//		mxsp1.setYan_s("蓝色");
//		List<XSp> listxsp = new ArrayList<>();
//		listxsp.add(mxsp);
//		listxsp.add(mxsp1);
//		System.out.println(GsonUtil.toJsonString(listxsp));
//	}
	/*
	 *查询订单
	 * */
	@RequestMapping(value ="/xstock/x_s", method = RequestMethod.POST) 
	@Auth_wx(x_key={x_key_ke.jing_l})
	public  RequestType xstockx_s(XStock mXStock,Integer rows,Integer page,String UID) throws Exception{
		X_u mxu = getX_u(UID);
		if(mxu.getEx()!=x_key_ke.admin.getKey()){
			mXStock.setShen_qr_name(mxu.getX_name());
		}
		return sendTrueData(mXstockService.getALL(mXStock,"XStock.id",1, page, rows));
	}
	/*
	 *查询我的门店的订单
	 * */
	@RequestMapping(value ="/xstock/x_shop_s", method = RequestMethod.POST) 
	@Auth_wx()
	public  RequestType x_shop_s(XStock mXStock,Integer rows,Integer page,String UID) throws Exception{
		X_u mxu = getX_u(UID);
		Sql msql = new Sql();
		msql.setSql("select XStock.* from XStock  where SUO_SMDBH='"+mxu.getX_m_code()+"'");
		//msql.setSql("select XStock.*,x_m_name SUO_SMDmz  from  XStock left join xu on SUO_SMDBH=x_m_code where SUO_SMDBH='"+mxu.getX_m_code()+"'");
		msql.setRows(rows);
		msql.setPage(page);
		List<XStock> listXStock =IBeanUtil.ListMap2ListJavaBean(mXstockService.exeSelectSql(msql), XStock.class);
		//mXstockService.update_ys_array(listXStock);
		return sendTrueData(listXStock);
	}
	/*
	 *查询我下了订单的门店
	 * */
	@RequestMapping(value ="/xstock/xshop", method = RequestMethod.POST) 
	@Auth_wx()
	public  RequestType xshop(String UID) throws Exception{
		X_u mxu = getX_u(UID);
		if(mxu==null)return sendFalse("登录超时");
		Sql msql = new Sql();
		msql.setSql("select SUO_SMDBH, SUO_SMDmz  from  XStock  where shen_qr_name='"+mxu.getX_name()+"' group by SUO_SMDBH, SUO_SMDmz ");
		return sendTrueData(mXstockService.exeSelectSql(msql));
	}
	/*
	 *同意订单
	 * */
	@RequestMapping(value ="/xstock/x_d_true", method = RequestMethod.POST) 
	@Auth_wx(x_key={x_key_ke.admin})
	public  RequestType xstockx_d_true(XStock mXStock,String UID,String bz) throws Exception{
		mXStock = mXstockService.getById(mXStock,XStock.class);
		if(mXStock==null)
			return sendFalse("订单不存在");
		if(mXStock.getShi_fty()!=0)
			return sendFalse("订单已处理");
		XSp msp=mSpService.getById(mXStock.getSp_id(),XSp.class);
		if(msp==null)return sendFalse("商品不存在");
		if(msp.getFa_b()!=1)return sendFalse("商品已下架");
		
		mXStock.setGong_ysgykc(mXStock.getShen_qkc());
		
		//String[] ys = msp.getYan_s().split(XSpService.fgf);
		//String[] djkc = msp.getDong_jkc().split(XSpService.fgf);
		/**List<XSp> lsitXSp = GsonUtil.fromJsonList(mXStock.getShen_qkc(), XSp.class);
		for (;;) {
			if(!xstockx_d_trueupdatekc(lsitXSp, djkc,  ys))break;
		}*/
//		for (XSp xSp : lsitXSp) {
//			for (int i = 0; i < ys.length; i++) {
//				if(xSp.getYan_s().equals(ys[i])){
//					Integer size =Integer.parseInt(djkc[i])-Integer.parseInt(xSp.getDong_jkc());
//					djkc[i]=size.toString();
//				}
//			}
//		}
		//String sb =forString(djkc,"*");
		//msp.setDong_jkc(sb.substring(0,sb.length()-1));
		
//		mSpService.updateBySelect(msp);
		//mSpService.updateBySelect_orders(msp);
		mXStock.setShi_fty(1);
		if(!Stringutil.isBlank(bz)){
			mXStock.setBei_z(mXStock.getBei_z()+System.currentTimeMillis()+":"+getX_u(UID).getX_name()+":"+bz+";");
		}
		mXstockService.updateBySelect(mXStock);
		return sendTrueMsg("同意成功!");
	}
	/**
	private boolean xstockx_d_trueupdatekc(List<XSp> lsitXSp,String[] djkc,String[] ys) {
		for (XSp xSp : lsitXSp) {
			for (int i = 0; i < ys.length; i++) {
				if(xSp.getYan_s().equals(ys[i])){
					Integer size =Integer.parseInt(djkc[i])-Integer.parseInt(xSp.getSheng_ykc());
					djkc[i]=size.toString();
					lsitXSp.remove(xSp);
					return true;
				}
			}
		}
		return false;

	}
	**/
//	/*
//	 *同意订单
//	 * */
//	@RequestMapping(value ="/xstock/x_d_true", method = RequestMethod.POST) 
//	@Auth_wx(x_key={x_key_ke.admin})
//	public  RequestType xstockx_d_true(XStock mXStock,Integer num,String UID,String bz) throws Exception{
//		mXStock = mXstockService.getById(mXStock,XStock.class);
//		if(mXStock==null)
//			return sendFalse("订单不存在");
//		if(mXStock.getShi_fty()!=0)
//			return sendFalse("订单已处理");
//		XSp msp=mSpService.getById(mXStock.getSp_id(),XSp.class);
//		if(msp==null)return sendFalse("商品不存在");
//		if(msp.getFa_b()!=1)return sendFalse("商品已下架");
//		
//		num=num==null?mXStock.getShen_qkc():num;
//		mXStock.setGong_ysgykc(num);
//		if(mXStock.getShen_qkc()<num){
//			return sendFalse("库存错误，给与库存不可高于申请库存");
//		}else{
//			msp.setSheng_ykc(msp.getSheng_ykc()+(mXStock.getShen_qkc()-num));
//		}
//		msp.setDong_jkc(msp.getDong_jkc()-mXStock.getShen_qkc());
//		
//		mSpService.updateBySelect(msp);
//		mXStock.setShi_fty(1);
//		if(!Stringutil.isBlank(bz)){
//			mXStock.setBei_z(mXStock.getBei_z()+System.currentTimeMillis()+":"+getX_u(UID).getX_name()+":"+bz+";");
//		}
//		mXstockService.updateBySelect(mXStock);
//		return sendTrueMsg("同意成功!");
//	}
//	/*
//	 *拒绝订单
//	 * */
//	@RequestMapping(value ="/xstock/x_d_false", method = RequestMethod.POST) 
//	@Auth_wx(x_key={x_key_ke.admin})
//	public  RequestType xstockx_d_false(XStock mXStock,String UID,String bz) throws Exception{
//		mXStock = mXstockService.getById(mXStock,XStock.class);
//		if(mXStock==null)
//			return sendFalse("订单不存在");
//		if(mXStock.getShi_fty()!=0)
//			return sendFalse("订单已处理");
//		XSp msp=mSpService.getById(mXStock.getSp_id(),XSp.class);
//		if(msp==null)return sendFalse("商品不存在");
//		
//		
//		mXStock.setGong_ysgykc(0);
//		msp.setSheng_ykc(msp.getSheng_ykc()+mXStock.getShen_qkc());
//		msp.setDong_jkc(msp.getDong_jkc()-mXStock.getShen_qkc());
//		
//		mSpService.updateBySelect(msp);
//		mXStock.setShi_fty(-1);
//		if(!Stringutil.isBlank(bz)){
//			mXStock.setBei_z(mXStock.getBei_z()+System.currentTimeMillis()+":"+getX_u(UID).getX_name()+":"+bz+";");
//		}
//		mXstockService.updateBySelect(mXStock);
//		return sendTrueMsg("拒绝成功!");
//	}

	
	private String getfile(HttpServletRequest req,String name) {
		String a1="";
		try {
			StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest) req;
			a1 =FileUtil.saveMultipartFile(request.getFile(name), mFileService);
		} catch (Exception e) {
		}
		return a1;

	}
	
	
	
	
	
	/**
	 * 导出商品excel
	 */
	@Auth_wx(x_key={x_key_ke.admin})
	@RequestMapping(value = "/sp/excel_out", method = RequestMethod.GET)
	public void spexcel_out(Long star,Long end, Integer rows, XSp msp,HttpServletResponse response)
			throws Exception {
		star=star==null?0:star;
		end=end==null?System.currentTimeMillis():end;
		Sql msql = new Sql();
		msql.setPage(1);
		msql.setRows(rows);
		msql.setSql("select * from (select * from xsp "+mSpService.getWhere(msp)+") where id<"+end+" and id>"+star);
		
		List<Map<String, Object>> listmap = mSpService.exeSelectSql(msql);
		
		List<String> title = new ArrayList<String>();
		List<String> key = new ArrayList<String>();
		key.add("id");			title.add("编号");
		key.add("da_l");		title.add("大类");
		key.add("zhong_l");		title.add("中类");
		key.add("xiao_l");		title.add("小类");
		key.add("xi_l");		title.add("细类");
		key.add("shang_pm");	title.add("商品名称");
		key.add("zhu_jm");		title.add("助记码");
		key.add("gong_ysbh");	title.add("供应商编号");
		key.add("ping_p");		title.add("品牌");
		key.add("nian_f");		title.add("年份");
		key.add("ji_j");		title.add("季节");
		key.add("ji_ldw");		title.add("计量单位");
		key.add("can_pfg");		title.add("产品风格");
		key.add("jia_gsx");		title.add("价格属性");
		key.add("nian_ld");		title.add("年龄段");
		key.add("bao_zfs");		title.add("包装方式");
		key.add("ping_pj");		title.add("吊牌价");
		key.add("cai_gj");		title.add("采购价");
		key.add("ding_jqx");	title.add("定价权限");
		key.add("shi_fyxsgzk");	title.add("是否允许手工折扣");
		key.add("shang_srq");	title.add("上市日期");
		key.add("xiao_szq");	title.add("销售周期");
		key.add("yan_s");		title.add("颜色");
		key.add("chi_m");		title.add("尺码");
		key.add("zhi_xbz");		title.add("执行标准");
		key.add("zhong_bbs");	title.add("中包标识");
		key.add("wai_bfs");		title.add("外包方式");
		key.add("nei_xs");		title.add("内箱数");
		key.add("wai_xs");		title.add("外箱数");
		key.add("cai_z");		title.add("材质");
		key.add("ban_x");		title.add("版型");
		key.add("hua_x");		title.add("花型");
		key.add("ling_x");		title.add("领型");
		key.add("feng_g");		title.add("风格");
		key.add("lei_b");		title.add("类别");
		key.add("bei_x");		title.add("杯型");
		key.add("bei_m");		title.add("杯模");
		key.add("ke_z");		title.add("克重");
		key.add("yang_p");		title.add("样品");
		key.add("zhu_tk");		title.add("主推款");
		key.add("a1");			title.add("图片1");
		key.add("a2");			title.add("图片2");
		key.add("a3");			title.add("图片3");
		key.add("a4");			title.add("图片4");
		key.add("zong_kc");		title.add("总库存");
		key.add("dong_jkc");	title.add("冻结库存");
		key.add("sheng_ykc");	title.add("剩余库存");
		key.add("fa_b");		title.add("是否发布");
		
		for (Map<String, Object> mmap : listmap) {
			java.util.Collection<String>  co = mmap.keySet();
			for (String string : co) {
				if(string.equals("A1")||string.equals("A2")||string.equals("A3")||string.equals("A4")){
					try {
						String str = mmap.get(string).toString();
						if(str.substring(0,1).equals("/"))
							str=str.substring(1,str.length());
						mmap.put(string, MyParameter.TomcatSD+str);
					} catch (Exception e) {
					}
					
				}
				if(string.equals("FA_B"))
					if( mmap.get(string).toString().equals("0"))
						mmap.put(string, "未发布");
					else
						mmap.put(string, "已发布");
			}
		}
		
		ExportExcel.Export("商品选款报表",title,key,listmap,response);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
