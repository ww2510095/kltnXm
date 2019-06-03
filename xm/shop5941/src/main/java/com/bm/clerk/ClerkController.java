package com.bm.clerk;

import java.io.File;
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

import com.bm.base.BaseController;
import com.bm.base.MyParameter;
import com.bm.base.Sql;
import com.bm.base.excle.ReadExcel;
import com.bm.base.interceptor.Auth;
import com.bm.base.interceptor.Auth.Administration;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.user.Member;
import com.myjar.desutil.RunException;

@RestController
@Api(tags = "店员身份")
public class ClerkController extends BaseController{
	@Autowired
	private ClerkService mClerkService;
	

	/**
	 *添加店员
	 * */
	@Auth(clerk=true)
	@RequestMapping(value ="/Clerk/add", method = RequestMethod.POST) 
	@Transactional
	public RequestType add(String phone,String uname) throws Exception{
		Long userid = getLogin(phone).getUserid();
//		if(userid==null) return sendFalse("用户:"+phone+"不存在或从未登陆过"); 
		Clerk mClerk =new Clerk();
		mClerk.setMemberid(userid);
		if(mClerkService.getALL(mClerk).size()!=0) return sendFalse("用户"+phone+"已经是店员了");
		if(getMember(getLogin(uname)).getmShop()==null||getMember(getLogin(uname)).getmShop().getId()==null)
			return sendFalse("抱歉，您不能添加店员"); 
		mClerk.setShopid(getMember(getLogin(uname)).getmShop().getId());
		mClerk.setState(1);
		mClerkService.add(mClerk);
		Sql msql = new Sql();
		msql.setSql("update Friends set memberida="+userid+" where  memberidb="+userid);
		mClerkService.execSQL(msql, -1, "");
		
		return sendTrueMsg("添加成功");

	}
	/**
	 * 批量导入店员
	 */
	@Auth(admin=true)
	@Transactional
	@RequestMapping(value = "/Clerk/readClerkExcel", method = RequestMethod.POST)
	public RequestType readShopExcel(HttpServletRequest req, String uname) throws Exception {
		long time = System.currentTimeMillis();
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			File file = new File(MyParameter.TomcatSD + s);
			List<List<String>> lls = ReadExcel.readExcel(file);// 解读excel
			Long id = System.currentTimeMillis();
			for (List<String> list : lls) {
				Member m = new Member();
				id=id+1;
//				try {
					m.setUname(list.get(0));
					m.setId(id);
					memberRegister(m,true);
//				} catch (Exception e) {
//					id=getLogin(uname).getUserid();
//				}
				
				
				
				
				Sql msql = new Sql();
				msql.setSql("select id from shop where code='"+lls.get(1)+"'");
				List<Map<String, Object>> listmap = mClerkService.exeSelectSql(msql);
				if(listmap.size()!=1) throw new RunException("错误，店铺："+lls.get(1)+" 不存在");
				
				Clerk mClerk = new Clerk();
				mClerk.setMemberid(id);
				mClerk.setShopid(Long.valueOf(listmap.get(0).get("ID").toString()));
				mClerk.setState(1);
				mClerkService.add(mClerk);
				
				msql.setSql("update Friends set memberida="+id+" where  memberidb="+id);
				mClerkService.execSQL(msql, -1, "");
				
			}

			
			return sendTrueMsg("导入成功，此次耗时" + (System.currentTimeMillis() - time) + "毫秒");
		}

		return sendFalse("未发现文件");
	}
	

	
	
}
