package com.bm.feedback;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;

@RestController
@Api(tags = "反馈")
public class FeedbackController extends BaseController{
	@Autowired
	private FeedbackService mFeedbackService;
	

	/**
	 *查询反馈
	 * */
	@RequestMapping(value ="/Feedback/select", method = RequestMethod.POST) 
	public RequestType select(Integer page,Integer rows) throws Exception{
		return sendTrueData(mFeedbackService.getALL(new Feedback(),page,rows));

	}
	/**
	 *添加反馈
	 * */
	@Auth
	@RequestMapping(value ="/Feedback/add", method = RequestMethod.POST) 
	public RequestType add(String text,String uname) throws Exception{
		Feedback mSearch=new Feedback();
		mSearch.setText(text);
		mSearch.setPhone(uname);
		mFeedbackService.add(mSearch);
		return sendTrueMsg("反馈成功");
		
	}
	/**
	 *删除反馈
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/Feedback/delete", method = RequestMethod.POST) 
	public RequestType delete(Long  id) throws Exception{
		mFeedbackService.deleteByid(id);
		return sendTrueMsg("删除成功");
		
	}
	

	
	
}
