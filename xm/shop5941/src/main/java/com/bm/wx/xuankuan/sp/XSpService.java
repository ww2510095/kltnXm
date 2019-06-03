package com.bm.wx.xuankuan.sp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;
 
@Service
public class XSpService extends BaseService{
	
	public static final String fgf="\\*";
	public static final String zwys="暂无颜色";
	
	@Override
	public String getTabName() {return "xsp";}
	
	public String forString(String key,Integer size) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(key);
		}
		return sb.toString();

	}
	
	/*public String updateBySelect(XSp mxsp) throws Exception {
		if(!Stringutil.isBlank(mxsp.getYan_s())){
			String[] s = mxsp.getYan_s().split(fgf);
			boolean b =false;
			for (int i = 0; i < s.length; i++) {
				if(s[i].equals(zwys+"*")||s[i].equals(zwys)){
					mxsp.setYan_s(mxsp.getYan_s().substring(4));
					mxsp.setSheng_ykc(mxsp.getSheng_ykc().substring(2));
					mxsp.setZong_kc(mxsp.getZong_kc().substring(2));
					mxsp.setDong_jkc(mxsp.getDong_jkc().substring(2));
					b=true;
				}
			}
			if(!b){
				XSp mxsp1=getById(mxsp,XSp.class);
				if(mxsp1==null)
					throw new RunException("未找到商品");
				if(mxsp1.getFa_b()==1)
					throw new RunException("已发布的商品不可修改颜色");
				else{
					
					if(mxsp.getFa_b()!=null&&mxsp.getFa_b()!=1){

						String sa =forString("0*", s.length);
						sa=sa.substring(0,sa.length()-1);
						mxsp.setSheng_ykc(sa);
						mxsp.setZong_kc(sa);
						mxsp.setDong_jkc(sa);
					
					}
				
				}
			}
		}
		if(mxsp.getFa_b()!=null){
			if(mxsp.getFa_b()==1){
				try {
					int a1 = mxsp.getYan_s().split(fgf).length;
					int a2 = mxsp.getZong_kc().split(fgf).length;
					int a3 = mxsp.getSheng_ykc().split(fgf).length;
					int a4 = mxsp.getDong_jkc().split(fgf).length;
					if(a1!=a2)
						throw new RunException("错误，库存和颜色数量不一致");
					if(a1!=a3)
						throw new RunException("错误，库存和颜色数量不一致");
					if(a1!=a4)
						throw new RunException("错误，库存和颜色数量不一致");
				} catch (Exception e) {
					throw new RunException("错误，库存和颜色数量不一致");
				}
			}
		}
		if(!Stringutil.isBlank(mxsp.getSheng_ykc())){
			String[] sykc = mxsp.getSheng_ykc().split(fgf);
			for (int i = 0; i < sykc.length; i++) {
				if(Integer.parseInt(sykc[i])<0){
					String str = mxsp.getYan_s();
					if(Stringutil.isBlank(str)){
						mxsp=getById(mxsp,XSp.class);
						str = mxsp.getYan_s();
					}
					String[] strs =  mxsp.getYan_s().split(fgf);
					throw new RunException("错误，("+strs[i]+")库存不足");
				}
			}
		}
		return super.updateBySelect(mxsp);
	}*/
	
	
   
	public List<XSp> getALL(XSp mxsp, String orderbykey, Integer orderbytype, Integer page, Integer rows)
			throws Exception {
		@SuppressWarnings("unchecked")
		List<XSp> listxsp = (List<XSp>) super.getALL(mxsp, orderbykey, orderbytype, page, rows);
		for (XSp xSp : listxsp) {
			String[] str = xSp.getYan_s().split(fgf);
			/*String[] zkc = xSp.getZong_kc().split(fgf);
			String[] djkc = xSp.getDong_jkc().split(fgf);
			String[] sykc = xSp.getSheng_ykc().split(fgf);*/
			Yan_s_and_kc_array[] mYan_s_and_kc_array = new Yan_s_and_kc_array[str.length];
			for (int i = 0; i < mYan_s_and_kc_array.length; i++) {
				mYan_s_and_kc_array[i] = new Yan_s_and_kc_array();
				//mYan_s_and_kc_array[i].setDong_jkc(Integer.valueOf(djkc[i]));
				//mYan_s_and_kc_array[i].setZong_kc(Integer.valueOf(zkc[i]));
				mYan_s_and_kc_array[i].setYan_s(str[i]);
				//mYan_s_and_kc_array[i].setSheng_ykc(Integer.valueOf(sykc[i]));
			}
			xSp.setYan_s_array(mYan_s_and_kc_array);
		}
		return listxsp;
	}

	/*public void updateBySelect_orders(XSp mxsp) throws Exception {
		if(!Stringutil.isBlank(mxsp.getSheng_ykc())){
			String[] sykc = mxsp.getSheng_ykc().split(fgf);
			for (int i = 0; i < sykc.length; i++) {
				if(Integer.parseInt(sykc[i])<0){
					String str = mxsp.getYan_s();
					if(Stringutil.isBlank(str)){
						mxsp=getById(mxsp,XSp.class);
						str = mxsp.getYan_s();
					}
					String[] strs =  mxsp.getYan_s().split(fgf);
					throw new RunException("错误，("+strs[i]+")库存不足");
				}
			}
		}
		super.updateBySelect(mxsp);
	}*/
}
