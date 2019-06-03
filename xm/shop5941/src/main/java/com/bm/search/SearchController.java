package com.bm.search;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;

@RestController
@Api(tags = "热门搜索")
public class SearchController extends BaseController{
	@Autowired
	private SearchService mSearchService;
	

	/**
	 *查询热门搜索
	 * */
	@RequestMapping(value ="/search/select", method = RequestMethod.POST) 
	public RequestType select() throws Exception{
		return sendTrueData(mSearchService.getALL(new Search(),1,8));

	}
	/**
	 *添加热门搜索
	 * */
	@Auth(admin=true)
	@RequestMapping(value ="/search/add", method = RequestMethod.POST) 
	public RequestType add(String text) throws Exception{
		Search mSearch=new Search();
		mSearch.setText(text);
		mSearchService.add(mSearch);
		return sendTrueMsg("添加成功");
		
	}
	/**
	 *删除热门搜索
	 * */
	@RequestMapping(value ="/search/delete", method = RequestMethod.POST) 
	public RequestType delete(Long  id) throws Exception{
		mSearchService.deleteByid(id);
		return sendTrueMsg("删除成功");
		
	}
	

	
	
}
