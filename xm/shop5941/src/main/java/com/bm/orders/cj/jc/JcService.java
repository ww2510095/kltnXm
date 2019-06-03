package com.bm.orders.cj.jc;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.bm.base.util.FileUtil;
import com.bm.file.FileService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@Service
public class JcService extends BaseService{

	@Override
	protected String getTabName() {
		return "jc";
	}
	
	public int add(Jc mjc,HttpServletRequest req,FileService mFileService) throws Exception {
		if(mjc==null)throw new RunException("奖池添加错误！");
		if(Stringutil.isBlank(mjc.getBh()))throw new RunException("编号不可为空！");
		if(mjc.getNum()==null)throw new RunException("中奖几率不可为空！");
		if(mjc.getNum().doubleValue()<0||mjc.getNum().doubleValue()>10000)throw new RunException("几率错误！"); 
		if(Stringutil.isBlank(mjc.getTitle()))throw new RunException("描述不可为空！");
	
		if(mjc.getXn_id()!=null){
			Sql msql = new Sql();
			msql.setSql("select id from Coupon where id="+mjc.getXn_id());
			if(exeSelectSql(msql).size()==0){
				throw new RunException("优惠券id不存在");
			}
			
		}
		
		if(Stringutil.isBlank(mjc.getImagepath())){
			if (req instanceof StandardMultipartHttpServletRequest){
				mjc.setImagepath(FileUtil.doFileUploadOne((StandardMultipartHttpServletRequest) req, mFileService));
			}	 
				
		}
		if(Stringutil.isBlank(mjc.getImagepath()))throw new RunException("奖品图片不可为空！");
		Jc mjc1 = new Jc();
		mjc1.setBh(mjc.getBh());
		@SuppressWarnings("unchecked")
		List<Jc> listjc = (List<Jc>) getALL(mjc1);
		if(listjc.size()!=0){
			BigDecimal a = mjc.getNum();
			for (Jc jc : listjc) {
				a=a.add(jc.getNum());
			}
			if(a.doubleValue()>10000)throw new RunException("几率错误,该奖池几率已经溢出，请检查"); 
			if(a.intValue()>=1)
			mjc.setDqyz(a.intValue());
			mjc.setM_num(listjc.get(0).getM_num());
			mjc.setStar(listjc.get(0).getStar());
			mjc.setEnd(listjc.get(0).getEnd());
			mjc1=listjc.get(0);
			if(Stringutil.isBlank(mjc.getTitle_su())){
				mjc.setTitle_su(listjc.get(0).getTitle_su());
			}else{
				if(!mjc.getTitle_su().equals(mjc1.getTitle_su())){
					throw new RunException("奖池描述错误，相同编号已有别的描述，如果不知道可以不填，系统会自动校正");
				}
			}
		}else{
			if(Stringutil.isBlank(mjc.getTitle_su())){
				throw new RunException("初次添加的编号奖池描述不可为空");
			}
			if(mjc.getStar()==null)throw new RunException("开始时间不可为空！");
			if(mjc.getEnd()==null)throw new RunException("结束时间不可为空！");
			if(mjc.getM_num()==null)throw new RunException("所需积分不可为空！");
			mjc.setDqyz(mjc.getNum().intValue());
		}
		if(mjc.getNum().doubleValue()<1)mjc.setDqyz(-1);
		return super.add(mjc);
	}
	/**
	 * 反会可用奖池的编号与描述
	 * @throws Exception 
	 * */
	public List<?> getkeyAll(Long star,Long end) throws Exception {
		Sql msql = new Sql();
		String enda = "";
		String stara = "";
		if(end!=null){
			enda=" and end>"+end;
		}
		if(star!=null){
			stara=" and star<"+(star+1);
		}
		msql.setSql("select max(dqyz)/100 dqyz,bh,title_su,max(m_num)m_num from jc where 1=1 "+stara+enda+" group by bh,title_su");
		return exeSelectSql(msql);

	}
	/**
	 * 反会当前可用奖池奖池的编号与描述
	 * @throws Exception 
	 * */
	public List<?> getkey_system() throws Exception {
		Long time = System.currentTimeMillis();
		return getkeyAll(time,time);
		
	}
}
