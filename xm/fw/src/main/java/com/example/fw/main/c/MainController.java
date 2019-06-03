package com.example.fw.main.c;

import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.fw.base.BaseController;
import com.example.fw.base.MyDate;
import com.example.fw.base.MyParameter;
import com.example.fw.base.RequestType;
import com.example.fw.base.Sql;
import com.example.fw.base.Util.ExportExcel;
import com.example.fw.base.Util.FileUtil;
import com.example.fw.base.Util.FileUtil.FileBen;
import com.example.fw.base.Util.ReadTxt;
import com.example.fw.base.Util.Zd;
import com.example.fw.main.b.Qiandai_a.Qiandai_a_tab_name;
import com.example.fw.main.b.Qiandai_key;
import com.example.fw.main.b.User;
import com.example.fw.main.s.JiabichacuoService;
import com.example.fw.main.s.Qiandai_keyService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = "其他模块")
public class MainController extends BaseController {

	@Autowired
	protected Qiandai_keyService mQiandai_keyService;
	@Autowired
    protected JiabichacuoService mJiabichacuoService;

	
	 @ApiOperation(value = "登录", response = RequestType.class)
    @RequestMapping(value ="/login", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uname", value = "用户名", dataType = "String", required = true),
            @ApiImplicitParam(name = "pwd", value = "密码", dataType = "String", required = true),
    })

    public RequestType login(String uname,String pwd, @ApiIgnore() HttpSession httpSession) throws Exception {

        if(Stringutil.isBlank(uname))
            return  sendFalse("账号不可为空");
        if(Stringutil.isBlank(pwd))
            return  sendFalse("密码不可为空");
        User mUser1 = mJiabiService.getUser(uname);
        if(mUser1==null)
            return  sendFalse("账号不存在");
        if(!mUser1.getPwd().equals(pwd))
            return  sendFalse("密码错误");

        mUser1.setPwd(null);
        try {
        	if(mUser1.getYou_xq()<System.currentTimeMillis())throw new RunException("账号已到期");
		} catch (Exception e) {
		}
        
        
//        Sql msql = new Sql();
//		msql.setSql("select * from ZHUZHIJIAGOU where ukey in(select ZHUZHIJIAGOU_NAME where uphone='"+mUser1.getUname()+"')");
//		Zhuzhijiagou mZhuzhijiagou =IBeanUtil.Map2JavaBean(mJiabiService.exeSelectSql(msql).get(0), Zhuzhijiagou.class);
//		mUser1.setKey(mZhuzhijiagou.getQuan_x().split(";"));
//		msql.setSql("select deng_j from wangdian where wang_ddz ='"+mUser1.getSuo_swd()+"'");
//		mUser1.setDeng_j(Integer.parseInt(mJiabiService.exeSelectSql(msql).get(0).get("DENG_J").toString()));
        // 将用户加入到Session中
		 if(mUser1.getZhuang_t()==0){
        	return  sendFalse("账号被封号");
		 }
        httpSession.setAttribute("user", mUser1);
        Map<String, Object> map = new HashMap<>();
        map.put("user", mUser1);
        map.put("sessionid", httpSession.getId());
        return sendTrueData(map);
    }
	 @ApiOperation(value = "查询账号到期时间", response = RequestType.class)
	 @RequestMapping(value ="/initUname", method = RequestMethod.POST)
	 public String initUname(String _uname) throws Exception {
		 Long sj1 =getUser(_uname).getYou_xq();
		String sj ;
		if(Stringutil.isBlank(sj1)){
			sj="9999-12-01 00:00:00";
		}else{
			sj=MyDate.stampToDate(sj1);
			
		}
		return sj; 
		
	 }
    @RequestMapping(value ="/qiandai_a_select", method = RequestMethod.POST)
    @ApiOperation(value = "单据明细", response = RequestType.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "keyid", value = "数据的id", dataType = "String", required = true),
        @ApiImplicitParam(name = "type", value = "单据的表单</br>"
        		+ "qdcg:钱袋采购</br>"
        		+ "lyd:领用单</br>"
        		+ "jdy:借用单</br>"
        		+ "ghd:归还单</br>"
        		+ "yjd:移交单", dataType = "String", required = true),
    })
    
    public RequestType qiandai_a_select(String keyid,String type) throws Exception {
    	for (Qiandai_a_tab_name e : Qiandai_a_tab_name.values()) {
				if(e.toString().equals(type)){
					return sendTrueData(mQiandai_key_listService.getTab_name_data(keyid));
				}
		}
    	return sendFalse("类型不存在");
    }
    @RequestMapping(value ="/qiandai_a_list_list", method = RequestMethod.POST)
    @ApiOperation(value = "单据状态,轨迹", response = RequestType.class)
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "keyid", value = "数据的id", dataType = "String", required = true),
    	@ApiImplicitParam(name = "type", value = "单据的表单</br>qdcg:钱袋采购", dataType = "String", required = true),
    })
    public RequestType qiandai_a_list_list(String keyid,String type) throws Exception {
    	for (Qiandai_a_tab_name e : Qiandai_a_tab_name.values()) {
    		if(e.toString().equals(type)){
    			return sendTrueData(mQiandai_listService.getTab_name_data(keyid));
    		}
    	}
    	return sendFalse("类型不存在");
    }
    @ApiOperation(value = "选择明细", response = Qiandai_key.class)
    @RequestMapping(value ="/list", method = RequestMethod.POST)
    public RequestType list(Qiandai_key mQiandai_key,Integer page,Integer rows,@ApiIgnore() HttpSession mHttpSession) throws Exception {
	 	Sql msql = new Sql();
	 	msql.setSql("select id from wangdian where wang_ddz='"+getUser(mHttpSession).getSuo_swd()+"' and suo_sqy='成都市'");
	 	if(mQiandai_keyService.exeSelectSql(msql).size()!=0)
			mQiandai_key.setShi_ylx("成都");
	 	else
			mQiandai_key.setShi_ylx("非成都");

        return sendTrueData(mQiandai_keyService.getALL(mQiandai_key,page,rows));
    }
    @ApiOperation(value = "是否有未读提醒(每5分钟调用一次，true代表有，false代表没有)", response = Qiandai_key.class)
    @RequestMapping(value ="/ti_xing", method = RequestMethod.POST)
    public RequestType ti_xing(@ApiIgnore() HttpSession httpSession) throws Exception {
    	Sql msql  = new Sql();
    	msql.setSql("select * from Jiabichacuo where Shi_fty='未处理' and wang_d='"+getUser(httpSession).getSuo_swd()+"'");
    	return sendTrueData(mQiandai_keyService.exeSelectSql(msql).size()!=0);
    }
    @ApiOperation(value = "报表", response = Qiandai_key.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "qiandai_name", value = "钱袋名称", dataType = "String", required = false),
        @ApiImplicitParam(name = "wangdian_name", value = "网点名称", dataType = "String", required = false),
        @ApiImplicitParam(name = "star", value = "开始时间", dataType = "Long", required = false),
        @ApiImplicitParam(name = "end", value = "结束时间", dataType = "Long", required = false),
    })
    @RequestMapping(value ="/bao_b", method = RequestMethod.POST)
    public RequestType bao_b(String qiandai_name,String wangdian_name,Long star,Long end,@ApiIgnore() HttpSession mHttpSession) throws Exception {
    	
    	return sendTrueData(getBao_b(getUser(mHttpSession),qiandai_name, wangdian_name, star, end));
    }
    
    private String subje(String mname,Qiandai_a_tab_name mQiandai_a_tab_name) throws Exception{
    	Sql msql  = new Sql();
    	String akey=mQiandai_a_tab_name.toString();
    	String bsql="select nvl(sum(shi_rsl),0)nkey from qiandai_key_list where wangdian_sp_b(tab_name)='"+mname+"' and tab_name like '"+akey+"%'";
    	msql.setSql(bsql);
		return mQiandai_keyService.exeSelectSql(msql).get(0).get("NKEY").toString();
    	
    }
    private String getSql(String key,String nkey,String wd){
    	String bsql="select nvl(sum(shi_rsl),0)"+nkey+",wangdian_sp(qiandai_key_list.tab_name)key,shi_ylx,quan_blx,ming_c from qiandai_key_list  where "
    			+ " qiandai_key_list.tab_name like '"+key+"%'  "
    			+wd+" group by wangdian_sp(qiandai_key_list.tab_name),shi_ylx,quan_blx,ming_c";
    	return bsql;
    } 
    private  List<Map<String,Object>> getBao_b(User mu,String qiandai_name,String wangdian_name,Long star,Long end) throws Exception{
    	Sql msql  = new Sql();
    	if(Stringutil.isBlank(wangdian_name)){
    		if(mu.getDeng_j()!=1)throw new RunException("权限不足，请选择具体的网点，只有顶级用户才可以查询所有的网点");
    		wangdian_name="";
        }
    	if(!Stringutil.isBlank(qiandai_name))qiandai_name=" and ming_c='"+qiandai_name+"'";
    	else qiandai_name="";
    	
    	 if(star==null)star=0L;
         if(end==null)end=System.currentTimeMillis();
         
         String a = " and id>"+star+" and id<"+end;
         
         String wd=qiandai_name+a;
    	
    	List<Map<String, Object>> ma;
    	List<Map<String, Object>> ma1;
    	String key = Qiandai_a_tab_name.yjd+wangdian_name;
    	String nkey = "YJSL";
    	//移交数量
    	msql.setSql(getSql(key, nkey, wd));
    	ma=mQiandai_keyService.exeSelectSql(msql);
    	
    	for (Map<String, Object> map : ma) {
    		map.put(nkey, Integer.parseInt(map.get(nkey).toString())-Integer.parseInt(subje(map.get("KEY").toString(), Qiandai_a_tab_name.yjd)));
		}
    	
    	//借用数量
    	 key = Qiandai_a_tab_name.jyd+wangdian_name;
    	 nkey = "JYD";
    	msql.setSql(getSql(key, nkey, wd));
    	ma1=mQiandai_keyService.exeSelectSql(msql);
    	boolean b;
    	for (Map<String, Object> map : ma1) {
    		b=false;
			for (Map<String, Object> map1 : ma) {
				if(map.get("KEY").toString().equals(map1.get("KEY").toString())){
					b=true;
					map.put("JYSL",map1.get("JYD"));
				}
			if(!b){
				//移交没有这个网点
				Map<String, Object> mmap = new HashMap<String, Object>();
				mmap.put("KEY", map.get("KEY"));
				ma.add(mmap);
			}
		}
	}
    	try {
    		for (Map<String, Object> map : ma) {
        		map.put(nkey, Integer.parseInt(map.get(nkey).toString())-Integer.parseInt(subje(map.get("KEY").toString(), Qiandai_a_tab_name.jyd)));
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	//领用数量
    	 key = Qiandai_a_tab_name.lyd+wangdian_name;
    	 nkey = "LYD";
    	msql.setSql(getSql(key, nkey, wd));
      	ma1=mQiandai_keyService.exeSelectSql(msql);
    	for (Map<String, Object> map : ma1) {
    		b=false;
			for (Map<String, Object> map1 : ma) {
				if(map.get("KEY").toString().equals(map1.get("KEY").toString())){
					b=true;
					map.put("LYSL",map1.get("LYD"));
				}
			if(!b){
				//移交没有这个网点
				Map<String, Object> mmap = new HashMap<String, Object>();
				mmap.put("KEY", map.get("KEY"));
				ma.add(mmap);
			}
		}
	}
    	try {
    		for (Map<String, Object> map : ma) {
        		map.put(nkey, Integer.parseInt(map.get(nkey).toString())-Integer.parseInt(subje(map.get("KEY").toString(), Qiandai_a_tab_name.lyd)));
    		}
		} catch (Exception e) {
		}
    	
    	//归还数量
    	
    	 key = Qiandai_a_tab_name.ghd+wangdian_name;
    	 nkey = "GHD";
    	msql.setSql(getSql(key, nkey, wd));
    	ma1=mQiandai_keyService.exeSelectSql(msql);
    	for (Map<String, Object> map : ma1) {
    		b=false;
			for (Map<String, Object> map1 : ma) {
				if(map.get("KEY").toString().equals(map1.get("KEY").toString())){
					b=true;
					map.put("GHSL",map1.get("GHD"));
				}
			if(!b){
				//移交没有这个网点
				Map<String, Object> mmap = new HashMap<String, Object>();
				mmap.put("KEY", map.get("KEY"));
				ma.add(mmap);
			}
		}
	}
    	
    	try {
    		for (Map<String, Object> map : ma) {
        		map.put(nkey, Integer.parseInt(map.get(nkey).toString())-Integer.parseInt(subje(map.get("KEY").toString(), Qiandai_a_tab_name.ghd)));
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	for (Map<String, Object> map : ma) {
			if(map.get("YJSL")==null){
				map.put("YJSL",0);
			}
			if(map.get("LYSL")==null){
				map.put("LYSL",0);
			}
			if(map.get("JYSL")==null){
				map.put("JYSL",0);
			}
			if(map.get("GHSL")==null){
				map.put("GHSL",0);
			}
			map.put("KC",  Integer.parseInt(map.get("LYSL").toString())-
							Integer.parseInt(map.get("YJSL").toString())-
							Integer.parseInt(map.get("GHSL").toString())+
							Integer.parseInt(map.get("JYSL").toString())
					);
		}
    	return ma;
    }
    @RequestMapping(value ="/Export_bao_b", method = RequestMethod.GET)
    @ApiOperation(value = "下载报表", response = Qiandai_key.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "qiandai_name", value = "钱袋名称", dataType = "String", required = false),
        @ApiImplicitParam(name = "wangdian_name", value = "网点名称", dataType = "String", required = false),
        @ApiImplicitParam(name = "star", value = "开始时间", dataType = "Long", required = false),
        @ApiImplicitParam(name = "end", value = "结束时间", dataType = "Long", required = false),
    })
    public RequestType ExportExcel(String qiandai_name,String wangdian_name,Long star,Long end, @ApiIgnore() HttpServletResponse response,@ApiIgnore() HttpSession mHttpSession) throws Exception {
        List<Map<String,Object>> listmap = getBao_b(getUser(mHttpSession),qiandai_name, wangdian_name, star, end);
        List<String> title = new ArrayList<String>();
        List<String> key = new ArrayList<String>();
        title.add("移交数量");        key.add("YJSL");
        title.add("领用数量");        key.add("LYSL");
        title.add("借用数量");        key.add("JYSL");
        title.add("归还数量");  		key.add("GHSL");
        title.add("网点名称");      key.add("KEY");
        title.add("使用类型");    key.add("SHI_YLX");
        title.add("钱袋类型");    key.add("QUAN_BLX");
        title.add("钱袋名称");        key.add("MING_C");
        title.add("库存");        key.add("KC");
        ExportExcel.setEX( MyParameter.TomcatFile+getUser(mHttpSession).getUname()+".xls");
        ExportExcel.Export("报表",title,key,listmap,null);
        return sendTrueData("报错生成完毕");
    }

    /**
     * 字典查询
     */
    @RequestMapping(value = "/zidian", method = RequestMethod.POST)
    @ApiOperation(value = "字典查询", response = RequestType.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pack", value = "接口名字", dataType = "String", required = true),
        @ApiImplicitParam(name = "field", value = "要查询的字段", dataType = "String", required = false),
        @ApiImplicitParam(name = "key", value = "字段的值", dataType = "String", required = false),
    })
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
                    if(str.equals("user"))str="jiabiuser";
                    Sql msql = new Sql();
                    pack="select COLUMN_NAME 字段名,COMMENTS 注释 from user_col_comments where Table_Name ='"+str.toUpperCase()+"'";
                    if(!Stringutil.isBlank(field)){
                        pack=pack+" and column_name='"+field.toUpperCase()+"'";
                    }
                    msql.setSql(pack);
                    return	sendTrueData(mJiabiService.exeSelectSql(msql));
                }
            }
        }


        return sendTrueMsg("未找到对应值");
    }
/*    *//**
     * 字典查询
     *//*
    @RequestMapping(value = "/filea")
    public  RequestType filea(String a) throws Exception {

    	String a1 =Base64.ImageToBase64ByLocal("C:/Users/Administrator/Desktop/weba/images/4.jpg");
    	System.out.println(a.length());
    	System.out.println(a1.length());
    	return sendTrueData(Base64.Base64ToImage(a));
    
    }
    *//**
     * 上传base64字符串到文件
     */
    @ApiOperation(value = "将字符串缓存到服务器", response = RequestType.class)
    @RequestMapping(value = "/ufile")
    public  RequestType aaa(String a) throws Exception {
    	ReadTxt.writeFile(a);
    	return sendTrueData("");
    }
    /**
    * 上传base64字符串到文件
    */
    @RequestMapping(value = "/newImage")
    @ApiOperation(value = "获取缓存的字符串", response = RequestType.class)
    public  RequestType newImage() throws Exception {
    	return sendTrueData(ReadTxt.readFile());
    }
    	
    /**
     * 上传一个文件
     */
    @RequestMapping(value = "/UpLoadFile")
    @ApiOperation(value = "上传一个文件", response = RequestType.class)
    public  RequestType UpLoadFile(HttpServletRequest req) throws Exception {
    	 List<FileBen> listFileBen = FileUtil.doFileUpload(req);
    	 for (FileBen fileBen : listFileBen) {
    		 return sendTrueData(fileBen.getPath());
		}
    	return sendTrueData(ReadTxt.readFile());
    }



}
