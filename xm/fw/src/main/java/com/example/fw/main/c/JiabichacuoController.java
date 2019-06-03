package com.example.fw.main.c;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fw.base.BaseController;
import com.example.fw.base.BaseService._where;
import com.example.fw.base.IBeanUtil;
import com.example.fw.base.MyParameter;
import com.example.fw.base.RequestType;
import com.example.fw.base.Sql;
import com.example.fw.base.Util.ExportExcel;
import com.example.fw.base.Util.FileUtil;
import com.example.fw.base.Util.FileUtil.FileBen;
import com.example.fw.main.b.Jiabichacuo;
import com.example.fw.main.s.JiabichacuoService;
import com.myjar.Stringutil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/Jiabichacuo")
@Api(tags = "假币差错")
public class JiabichacuoController extends BaseController {
    @Autowired
    protected JiabichacuoService mJiabichacuoService;

    @ApiOperation(value = "添加假币差错和审核，id为空添加，不为空审核", response = RequestType.class)
    @RequestMapping(value ="/save", method = RequestMethod.POST)
    @ApiImplicitParams({
   	 @ApiImplicitParam(name = "aaa", value = "可以上传N个文件</br>文件key值无所谓(不能是qqqq)</br>那个qqqq是我测试的", dataType = "String", required = true),
   })
    public RequestType save(Jiabichacuo mJiabichacuo,@ApiIgnore() HttpServletRequest req,@ApiParam MultipartFile qqqq) throws Exception {
    	HttpSession httpSession=req.getSession();
    	
    	 //List<FileBen> listFileBen = FileUtil.doFileUpload(req);
    	 
    	if(Stringutil.isBlank(mJiabichacuo.getId())){
/*    		if(listFileBen.size()==0)return sendFalse("图片不可为空");
    		String sa = "";
    		for (FileBen fileBen : listFileBen) {
				sa=sa+fileBen.getPath()+";";
			}
*/    		if(Stringutil.isBlank(mJiabichacuo.getImage()))return sendFalse("图片不可为空");
        	mJiabichacuo.setTi_jr(getUser(httpSession).getUname());
        	mJiabichacuo.setShi_fck(0);
        	//mJiabichacuo.setImage(sa);
        	mJiabichacuo.setShi_fty("未处理");
            mJiabichacuoService.add(mJiabichacuo);
            return sendTrueMsg("添加成功");
        }else{
        	
        	Jiabichacuo mJiabichacuo1=mJiabichacuoService.getById(mJiabichacuo,Jiabichacuo.class);
        	if(!mJiabichacuo1.getWang_d().equals(getUser(httpSession).getSuo_swd())){
        		if(!mJiabichacuo1.getTi_jr().equals(getUser(httpSession).getUname())){
        			
        		return sendFalse("只有相关责任单位才能操作");
        		}
        	}
        	if(Stringutil.isBlank(mJiabichacuo.getShi_fty()))return sendFalse("是否同意必选");
        	if(!mJiabichacuo.getShi_fty().equals("不同意")){
        		if(!mJiabichacuo.getShi_fty().equals("同意")){
        			return sendFalse("是否同意只能是同意和不同意");
        		}
        	}
        	if(mJiabichacuo.getShi_fty().equals("不同意")){
        		//if(listFileBen.size()==0)return sendFalse("图片不可为空");
        		if(Stringutil.isBlank(mJiabichacuo.getShen_htp()))return sendFalse("图片不可为空");
        		if(Stringutil.isBlank(mJiabichacuo.getShen_hyj())){
        			return sendFalse("文字不可为空");
        		}
       		 
        	}
        	mJiabichacuo.setShen_hr(getUser(httpSession).getUname());
        	mJiabichacuoService.updateBySelect(mJiabichacuo);
        	 return sendTrueMsg("成功");
           
        }

    }
    @ApiOperation(value = "假币查询列表查询", response = Jiabichacuo.class)
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    public RequestType list(Jiabichacuo mJiabichacuo,Integer page,Integer rows,@ApiIgnore() HttpSession httpSession) throws Exception {
    	Sql msql = new Sql();
		msql.setPage(page);
		msql.setRows(rows);
		switch (getUser(httpSession).getDeng_j()) {
		case 1:
			msql.setSql("select * from Jiabichacuo "+mJiabichacuoService.getWhere(mJiabichacuo, _where.like));
			break;
		case 2:
			msql.setSql("select * from (select * from Jiabichacuo "+mJiabichacuoService.getWhere(mJiabichacuo, _where.like)+") where "+
					" zhi_ddw in(select wang_ddz from wangdian where a2='"+getUser(httpSession).getSuo_swd()+"')");
			break;
		case 3:
			msql.setSql("select * from (select * from Jiabichacuo "+mJiabichacuoService.getWhere(mJiabichacuo, _where.like)+") where "+
					" zhi_ddw in(select wang_ddz from wangdian where a3='"+getUser(httpSession).getSuo_swd()+"')");
			break;
		case 4:
			msql.setSql("select * from (select * from Jiabichacuo "+mJiabichacuoService.getWhere(mJiabichacuo, _where.like)+") where wang_d='"+getUser(httpSession).getSuo_swd()+"' ");
			break;
		}
		List<Jiabichacuo> mlistJiabichacuo =IBeanUtil.ListMap2ListJavaBean( mJiabichacuoService.exeSelectSql(msql), Jiabichacuo.class);
		String ida="";
		for (Jiabichacuo jiabichacuo : mlistJiabichacuo) {
			ida= ida+jiabichacuo.getId()+",";
		}
		ida=ida+"1";
		msql.setSql("update Jiabichacuo set shi_fck=1 where id in("+ida+")");
		mJiabichacuoService.execSQL(msql);
        return sendTrueData(mlistJiabichacuo);
    }

    @RequestMapping(value ="/getByid", method = RequestMethod.POST)
    public RequestType getByid(String id) throws Exception {
        if(Stringutil.isBlank(id)) return sendFalse("编号不可为空");
        return sendTrueData(mJiabichacuoService.getById(id));
    }

    @RequestMapping(value ="/ExportExcel", method = RequestMethod.GET)
    public RequestType ExportExcel(Jiabichacuo mJiabichacuo, Long star, Long end, HttpServletResponse response,@ApiIgnore HttpSession mHttpSession) throws Exception {
       if(star==null)star=0L;
       if(end==null)end=System.currentTimeMillis();
        Sql msql = new Sql();
        msql.setSql("select * from Jiabichacuo "+mJiabichacuoService.getWhere(mJiabichacuo)+" and id<"+end+" and id>"+star);
        List<Map<String,Object>> listmap = mJiabichacuoService.exeSelectSql(msql);
        List<String> title = new ArrayList<String>();
        List<String> key = new ArrayList<String>();
        title.add("编号");      key.add("id");
        title.add("面值");      key.add("mian_z");
        title.add("差错");      key.add("cha_clx");
        title.add("上报人账号");  key.add("ti_jr");
        title.add("审批人  ");    key.add("shen_hr");
        title.add("是否同意 ");   key.add("shi_fty");
//        title.add("是否修改 ");   key.add("shi_fxg");
//        title.add("描述");      key.add("shen_hyj");
//        title.add("图片");      key.add("image");
//		title.add("不同意图片");      key.add("shen_htp");
        //图片处理
       // ExportExcel.ImagePathKey(listmap,"ZHAP_P");
        
        ExportExcel.setEX(  MyParameter.TomcatFile+getUser(mHttpSession).getUname()+".xls");
        ExportExcel.Export("报表",title,key,listmap,null);
        return sendTrueData("报错生成完毕");
       // ExportExcel.Export("报表",title,key,listmap,response);
    }


}
