package com.bm.help;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.myjar.Stringutil;

@RestController
@Api(tags = "帮助文档")
public class HelpController extends BaseController {

	@Autowired
	private HelpService mHelpService;

	/**
	 * 添加帮助
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/help/add", method = RequestMethod.POST)
	public RequestType shopsave(Help mHelp,HttpServletRequest req) throws Exception {
		if(Stringutil.isBlank(mHelp.getTitle2()))return sendFalse("标题不可为空");
		try {
			Long.valueOf(mHelp.getTitle2());
			return sendFalse("标题不可为纯数字");
		} catch (Exception e) {
			
			if (req instanceof StandardMultipartHttpServletRequest) {
				try {
					String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
					mHelp.setContent(s);
				} catch (Exception e2) {
				}
			
				
			}else{
				mHelp.setContent("1");
			}
			if(mHelp.getType()==null)mHelp.setType(0);
			
			mHelpService.add(mHelp);
			
			return sendTrueMsg("添加成功");
		}
		
	}
	/**
	 * 查询顶级标题
	 * */
	@RequestMapping(value = "/help/title", method = RequestMethod.POST)
	public RequestType title(String uname) throws Exception {
		int a;
		if(uname==null)a=1;
		else{

			if(getMember(uname).isClerk())
				a=2;
			else
				a=1;
		
		}
		Sql msql = new Sql();
		msql.setSql("select  title2, max(px)px from apphelp where title1 is null and type<"+a+" group by title2 order by nvl(px,0) desc ");
		return sendTrueData(mHelpService.exeSelectSql(msql));
		 
	}
	/**
	 * 查询所有内容
	 * */
	@RequestMapping(value = "/help/selectall", method = RequestMethod.POST)
	public RequestType selectall() throws Exception {
		Sql msql = new Sql();
		msql.setSql("select * from apphelp ");
		return sendTrueData(mHelpService.exeSelectSql(msql));
		
	}
	/**
	 * 查询下一级标题
	 * */
	@RequestMapping(value = "/help/nexttitle", method = RequestMethod.POST)
	public RequestType nexttitle(String title,String uname) throws Exception {
		if(Stringutil.isBlank(title))return sendFalse("标题不可为空");
		int a;
		if(uname==null)a=1;
		else{
			if(getMember(uname).isClerk())
				a=2;
			else
				a=1;
		}
			
		Sql msql = new Sql();
		msql.setSql("SELECT * FROM (select distinct title2,1 id,'1' content,PX from apphelp  where content ='1' and title1 ='"+title
					+"' and type<"+a+"  union all select title2,id,content,PX from apphelp where content !='1' and title1 ='"+title+"'"
							+ " and type<"+a+" )order by nvl(px,0) desc ");
		return sendTrueData(mHelpService.exeSelectSql(msql));
		
	}
	/**
	 * 删除帮助
	 * */
	@Auth(admin=true)
	@RequestMapping(value = "/help/delete", method = RequestMethod.POST)
	public RequestType delete(String key) throws Exception {
		try {
			Long id = Long.valueOf(key);
			mHelpService.deleteByid(id);
		} catch (Exception e) {
			Help mhelp = new Help();
			mhelp.setTitle2(key);
			mHelpService.deleteBySelect(mhelp);
		}
		
		return sendTrueMsg("删除成功");
		
	}
	
	/**
	 * 修改帮助
	 */
	@Auth(admin=true)
	@RequestMapping(value = "/help/update", method = RequestMethod.POST)
	public RequestType update(Help mHelp,HttpServletRequest req) throws Exception {

		
		if (req instanceof StandardMultipartHttpServletRequest) {
			String s = FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest)req,mFileService);// 将文件保存到服务器
			mHelp.setContent(s);
			
		}else{
			mHelp.setContent("1");
		}
	
		mHelpService.updateBySelect(mHelp);
		return sendTrueMsg("更新成功");

	}

	
}
