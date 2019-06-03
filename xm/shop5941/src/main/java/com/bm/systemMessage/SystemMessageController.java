package com.bm.systemMessage;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;
import com.bm.base.util.FileUtil;
import com.bm.base.util.GsonUtil;
import com.bm.base.util.IBeanUtil;
import com.myjar.Stringutil;

@RestController
@Api(tags = "消息模块")
public class SystemMessageController extends BaseController{
	
	/**
	 * 添加消息
	 * */
	@RequestMapping(value ="/SystemMessage/add", method = RequestMethod.POST) 
	@Auth(SystemMessage=true)
	public RequestType add(SystemMessage mSystemMessage, HttpServletRequest req,String uname) throws Exception{
		if(Stringutil.isBlank(mSystemMessage.getTitle()))return sendFalse("标题不可为空");
		if(Stringutil.isBlank(mSystemMessage.getContent()))return sendFalse("内容不可为空");
		if(Stringutil.isBlank(mSystemMessage.getSummary()))return sendFalse("摘要不可为空");
		if(mSystemMessage.getType()==null)return sendFalse("类型不可为空");
		if(mSystemMessage.getMymemberid()==null)mSystemMessage.setMymemberid(0L);
		// 图片
		if (Stringutil.isBlank(mSystemMessage.getImage())) {
			if (req instanceof StandardMultipartHttpServletRequest)
				mSystemMessage.setImage(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
		}
		if (Stringutil.isBlank(mSystemMessage.getImage()))
				return sendFalse("图片不可为空");
	
		mSystemMessage.setMemberid(getLogin(uname).getUserid());
		mSystemMessage.setCode(UUID.randomUUID().toString());
		mSystemMessage.setIstrue(0);
		mSystemMessage.setMymemberid(0L);
		mSystemMessageService.add(mSystemMessage);
		return sendTrueMsg("发布成功");
	}
	/**
	 * 删除消息
	 * */
	@RequestMapping(value ="/SystemMessage/delete", method = RequestMethod.POST) 
	@Auth
	public RequestType delete(SystemMessage mSystemMessage, HttpServletRequest req,String uname) throws Exception{
		mSystemMessage = IBeanUtil.Map2JavaBean(mSystemMessageService.getById(mSystemMessage), SystemMessage.class);
		if(getMember(getLogin(uname)).getSuperadmin()!=1)
			if(mSystemMessage.getMemberid()!=getLogin(uname).getUserid())return sendFalse("该消息不属于你");
		
		mSystemMessageService.deleteByid(mSystemMessage);
		return sendTrueMsg("删除成功");
	}
	/**
	 * 删除一批消息
	 * */
	@RequestMapping(value ="/SystemMessage/deletebycode", method = RequestMethod.POST) 
	@Auth(admin=true)
	public RequestType deletebycode(String code, HttpServletRequest req,String uname) throws Exception{
		SystemMessage mSystemMessage = new SystemMessage();
		mSystemMessage.setCode(code);
		mSystemMessageService.deleteBySelect(mSystemMessage);
		return sendTrueMsg("删除成功");
	}
		
	/**
	 * 查询我的消息列表
	 * */
	@RequestMapping(value ="/SystemMessage/selectallBymemberid", method = RequestMethod.POST) 
	@Auth
	public RequestType selectallBymemberid( HttpServletRequest req,String uname,Integer page,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setSql("select * from SystemMessage where mymemberid="+getLogin(uname).getUserid() +" or mymemberid=0");
		msql.setPage(page);
		msql.setRows(rows);
		msql.setOrderbykey("istrue,id ");
		msql.setOrderbytype(1);
		return sendTrueData(mSystemMessageService.exeSelectSql(msql));
	}
	/**
	 * 查询所有消息列表
	 * */
	@RequestMapping(value ="/SystemMessage/selectallByadmin", method = RequestMethod.POST) 
	@Auth(admin=true)
	public RequestType selectallByadmin( HttpServletRequest req,String uname,Integer page,Integer rows) throws Exception{
		SystemMessage mSystemMessage = new SystemMessage();
		return sendTrueData(mSystemMessageService.getALL(mSystemMessage,page, rows));
	}
	/**
	 * 查询未读消息列表
	 * */
	@RequestMapping(value ="/SystemMessage/selectallBytrue", method = RequestMethod.POST) 
	@Auth
	public RequestType selectallBytrue( HttpServletRequest req,String uname,Integer page,Integer rows) throws Exception{
		SystemMessage mSystemMessage = new SystemMessage();
		mSystemMessage.setMemberid(getLogin(uname).getUserid());
		mSystemMessage.setIstrue(0);
		return sendTrueData(mSystemMessageService.getALL(mSystemMessage,page, rows));
	}
	/**
	 * 查询已读消息列表
	 * */
	@RequestMapping(value ="/SystemMessage/selectallByfalse", method = RequestMethod.POST) 
	@Auth
	public RequestType selectallByfalse( HttpServletRequest req,String uname,Integer page,Integer rows) throws Exception{
		SystemMessage mSystemMessage = new SystemMessage();
		mSystemMessage.setMemberid(getLogin(uname).getUserid());
		mSystemMessage.setIstrue(1);
		return sendTrueData(mSystemMessageService.getALL(mSystemMessage,page, rows));
	}
	/**
	 * 查询单条消息详细
	 * */
	@RequestMapping(value ="/SystemMessage/selectallByid", method = RequestMethod.POST) 
	@Auth
	public RequestType selectallByid( HttpServletRequest req,String uname,Long id) throws Exception{
		SystemMessage mSystemMessage = IBeanUtil.Map2JavaBean(mSystemMessageService.getById(id), SystemMessage.class);
		mSystemMessage.setIstrue(1);
		mSystemMessageService.updateBySelect(mSystemMessage);
		if(mSystemMessage.getTitle().equals("物流助手"))
			mSystemMessage.setContent(mSystemMessage.getSummary());
		return sendTrueData(mSystemMessage);
		
	}
	/**
	 * 根据消息类型查询消息
	 * */
	@RequestMapping(value ="/SystemMessage/selectallBytype", method = RequestMethod.POST) 
	@Auth
	public RequestType selectallBytype( HttpServletRequest req,String uname,Integer type,Integer page,Integer rows) throws Exception{
		SystemMessage mSystemMessage = new SystemMessage();
		mSystemMessage.setMemberid(getLogin(uname).getUserid());
		mSystemMessage.setType(type);
		return sendTrueData(mSystemMessageService.getALL(mSystemMessage,page, rows));
	}
	/**
	 * 批量删除消息
	 * */
	@RequestMapping(value ="/SystemMessage/deleteByJsonArray", method = RequestMethod.POST) 
	@Auth
	@Transactional
	public RequestType selectallBytype(String JsonArray,HttpServletRequest req,String uname) throws Exception{
		List<SystemMessage> mListSystemMessage =GsonUtil.fromJsonList(JsonArray, SystemMessage.class);
		for (SystemMessage systemMessage : mListSystemMessage) {
			mSystemMessageService.deleteByid(systemMessage);
		}
		return sendTrueMsg("删除成功");
	}
	/**
	 * 所有消息已读
	 * */
	@RequestMapping(value ="/SystemMessage/updateistrue", method = RequestMethod.POST) 
	@Auth
	public RequestType updateistrue(HttpServletRequest req,String uname) throws Exception{
		SystemMessage mSystemMessage = new SystemMessage();
		mSystemMessage.setMemberid(getLogin(uname).getUserid());
		mSystemMessage.setIstrue(1);
		mSystemMessageService.updateBySelect(mSystemMessage);
		return sendTrueMsg("操作成功");
	}
	/**
	 * 清空消息
	 * */
	@RequestMapping(value ="/SystemMessage/deleteBymemberidAll", method = RequestMethod.POST) 
	@Auth
	public RequestType deleteBymemberidAll(HttpServletRequest req,String uname) throws Exception{
		SystemMessage mSystemMessage = new SystemMessage();
		mSystemMessage.setMemberid(getLogin(uname).getUserid());
		mSystemMessageService.deleteBySelect(mSystemMessage);
		return sendTrueMsg("删除成功");
	}
	
	
	
	
	
}
