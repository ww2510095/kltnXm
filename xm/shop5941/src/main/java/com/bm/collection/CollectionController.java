package com.bm.collection;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bm.base.BaseController;
import com.bm.base.Sql;
import com.bm.base.interceptor.Auth;
import com.bm.base.request.RequestType;



@RestController
@Api(tags = "收藏")
public class CollectionController extends BaseController{
	@Autowired
	private CollectionService mCollectionService;
	
	
	/**
	 * 查询所有收藏
	 * 管理员查看所有
	 * */
	@RequestMapping(value ="/Collection/selectAll", method = RequestMethod.POST) 
	@Auth
	public RequestType selectAll(String uname,Integer page ,Integer rows) throws Exception{
		Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		msql.setSql("select DISTINCT Commodity.commoditykeyid,Collection.id,name,mainimage,introduction,price from Collection left join "
				+ "Commodity on Commodity.commoditykeyid=commodityid where memberid="+getLogin(uname).getUserid());
		List<Map<String, Object>> listmap = mCollectionService.exeSelectSql(msql);
		for (Map<String, Object> map : listmap) {
			try {
				map.put("MAINIMAGE",map.get("MAINIMAGE").toString().split(";")[0]);
			} catch (Exception e) {}
			
		}
		
		return sendTrueData(listmap);
	}
	/**
	 * 删除收藏
	 * */
	@Auth
	@RequestMapping(value ="/Collection/delete", method = RequestMethod.POST) 
	public RequestType delete(Long id,String uname,Integer type) throws Exception{
		Collection mCollection =new Collection();
		mCollection.setMemberid(getLogin(uname).getUserid());
		if(type==null)
			mCollection.setId(id);
		else
			mCollection.setCommodityid(id);
		mCollectionService.deleteBySelect(mCollection);
		return sendTrueMsg("删除成功");
	}

	
	/**
	 * 清空收藏
	 * */
	@Auth
	@RequestMapping(value ="/Collection/deleteall", method = RequestMethod.POST) 
	public RequestType deleteall(String uname) throws Exception{
		Collection mCollection =new Collection();
		mCollection.setMemberid(getLogin(uname).getUserid());
		mCollectionService.deleteBySelect(mCollection);
		return sendTrueMsg("清空成功");
	}
	/**
	 * 添加收藏
	 * */
	@Auth
	@RequestMapping(value ="/Collection/add", method = RequestMethod.POST) 
	public RequestType add(Collection mCollection,String uname) throws Exception{
		if(mCollection.getCommodityid()==null)return sendFalse("商品id错误");
		mCollection.setMemberid(getLogin(uname).getUserid());
		try {
			mCollectionService.add(mCollection);
		} catch (Exception e) {
			return sendFalse("已经收藏过了");
		}
		
		return sendTrueMsg("收藏成功");
	}
	

}

