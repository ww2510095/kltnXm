package com.bm.user.goldcoin;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.excle.ReadExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.user.Member;
import com.myjar.Stringutil;



@RestController
@Api(tags = "积分模块")
public class GoldcoinController extends BaseController {
	@Autowired
	private GoldcoinKeyService mKey;
	@Autowired
	private GoldcoinVService mValue;
	@Autowired
	private GoldcoinDefaultService mDefault;


	
	/**
	 * 为用户导入积分
	 */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/GoldcoinV/Excel", method = RequestMethod.POST)
	public RequestType readShopExcel(HttpServletRequest req, String uname) throws Exception {
		long time = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			GoldcoinV mGoldcoinV = new GoldcoinV();
			Long ida = time;
			for (int i=0;i<lls.size();i++) {
				Member mMember = mMemberService.getByparameter("uname", lls.get(i).get(0),Member.class);
				if(mMember==null)
					continue;
				mGoldcoinV.setMemberid(mMember.getId());
				mGoldcoinV.setNum(Integer.valueOf(lls.get(i).get(1)));
				mGoldcoinV.setId(ida);
				mGoldcoinV.setTitle("管理员添加");
				ida=ida+1;
				mValue.add(mGoldcoinV);
			}
			
			return sendTrueMsg("导入成功，此次一共导入" + lls.size() + "条数据！总共耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	

	/**
	 * 添加默认的积分规则
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/GoldcoinDefault/add", method = RequestMethod.POST)
	public RequestType GoldcoinDefaultadd(GoldcoinDefault mGoldcoinDefault) throws Exception {
		if(mGoldcoinDefault.getPercentage()==null)return sendFalse("比例不可为空");
		if(mGoldcoinDefault.getPercentage()>100||mGoldcoinDefault.getPercentage()<0)return sendFalse("比例错误，只能是0-100");
		mDefault.add(mGoldcoinDefault);
		return sendTrueMsg("添加成功");
	}
	
	/**
	 * 查询默认积分规则
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/GoldcoinDefault/select", method = RequestMethod.POST)
	public RequestType GoldcoinDefaultselect(Integer page,Integer rows) throws Exception {
		return sendTrueData(mDefault.getALL(new GoldcoinDefault(), page, rows));
	}
	/**
	 * 为商品积分规则
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/GoldcoinKey/add", method = RequestMethod.POST)
	public RequestType GoldcoinKeyadd(GoldcoinKey mGoldcoinKey) throws Exception {
		if(mGoldcoinKey.getNum()==null)return sendFalse("积分不可为空");
		if(Stringutil.isBlank(mGoldcoinKey.getYoucode()))return sendFalse("条码不可为空");
		mKey.add(mGoldcoinKey);
		return sendTrueMsg("添加成功");
	}
	
	/**
	 * 查询商品积分规则
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/GoldcoinKey/select", method = RequestMethod.POST)
	public RequestType GoldcoinKeyselect(String youcode,Integer page,Integer rows) throws Exception {
		GoldcoinKey mGoldcoinKey = new GoldcoinKey();
		mGoldcoinKey.setYoucode(youcode);
		return sendTrueData(mKey.getALL(mGoldcoinKey, page, rows));
	}
	/**
	 * 查询积分明细
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/GoldcoinV/select", method = RequestMethod.POST)
	public RequestType GoldcoinVselect(GoldcoinV mGoldcoinV,String uname,String phone,Integer page,Integer rows) throws Exception {
//		GoldcoinV mGoldcoinV = new GoldcoinV();
		if(!Stringutil.isBlank(phone))
			uname=phone;
//		if(!Stringutil.isBlank(uname))
			
		if(getMember(uname).getSuperadmin()!=1){
			mGoldcoinV.setIstrue(1);
			mGoldcoinV.setMemberid(getMember(uname).getId());
			}else{
				if(!Stringutil.isBlank(phone))
					mGoldcoinV.setMemberid(getMember(phone).getId());
			}
		@SuppressWarnings("unchecked")
		List<GoldcoinV> listGoldcoinV = (List<GoldcoinV>) mValue.getALL(mGoldcoinV, page, rows);
		for (GoldcoinV goldcoinV : listGoldcoinV) {
			goldcoinV.setMemberid(Long.valueOf(getMember(goldcoinV.getMemberid()).getUname()));
		}
		return sendTrueData(listGoldcoinV);
	}
	/**
	 * 我的积分明细
	 */
	@Auth()
	@RequestMapping(value = "/GoldcoinV/select_my", method = RequestMethod.POST)
	public RequestType GoldcoinVselect_my(String uname,Integer page,Integer rows) throws Exception {
		return GoldcoinVselect(new GoldcoinV(),uname,uname, page, rows);
	}

}
