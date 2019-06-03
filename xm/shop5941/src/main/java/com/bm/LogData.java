package com.bm;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.bm.base.BaseDao;
import com.bm.base.Sql;
import com.bm.base.redis.RedisUtils;

public class LogData implements Runnable{
	
	private final Logger mLogger= LoggerFactory.getLogger(getClass());

	private String phone;//访问人账号||id
	private String ip;//访问人ip||
	private String url;//访问地址||tabneme
	private String parameter;//格式化参数||select or update
	private BaseDao mBaseDao;
	private boolean b;//false||true
	private RedisTemplate<String,String> mRedisTemplate;

	@Override
	public void run() {
		try {
			Sql msql = new Sql();
			if(b){
				msql.setSql("select * from "+url+" where id = '"+phone+"'");
				List<Map<String, Object>> listmap  =  mBaseDao.exeSelectSql(msql);
				if(listmap.size()!=0)
					RedisUtils.setDtae(mRedisTemplate,parameter, phone, listmap.get(0));
			}else{
				if(parameter.contains("'"))
					parameter=byteArr2HexStr(parameter.getBytes());
				msql.setSql("INSERT INTO systemlogs (id,time,phone,ip,url,parameter) VALUES('"+UUID.randomUUID()+"',"+System.currentTimeMillis()+",'"+phone+"','"+ip+"','"+url+"','"+parameter+"')");
				mBaseDao.execSQL(msql);
			}
			
			
		} catch (Exception e) {
			mLogger.error(e.getMessage());
		}
		
	}
	private static String byteArr2HexStr(byte[] arrB){
		int iLen = arrB.length;
		// ÿ��byte�������ַ����ܱ�ʾ�������ַ����ĳ��������鳤�ȵ�����
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// �Ѹ���ת��Ϊ����
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// С��0F������Ҫ��ǰ�油0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}
	
	
	private LogData() {
	}
	/**
	 * 增加日志
	 */
	public static void start(BaseDao mBaseDao,String phone,String ip,String url,String parameter) {
		LogData mLogData = new LogData();
		mLogData.phone=phone;
		mLogData.ip=ip;
		mLogData.url=url;
		mLogData.parameter=parameter;
		mLogData.mBaseDao=mBaseDao;
		mLogData.b=false;
		new Thread(mLogData).start();
	}
	/**
	 * 缓存修改的数据
	 */
	public static void start(RedisTemplate<String,String> mRedisTemplate,BaseDao mBaseDao,String tabname,String id,String parameter) {
		LogData mLogData = new LogData();
		mLogData.mRedisTemplate=mRedisTemplate;
		mLogData.phone=id;
		mLogData.parameter=parameter;
		mLogData.url=tabname;
		mLogData.mBaseDao=mBaseDao;
		mLogData.b=true;
		new Thread(mLogData).start();
	}

	
	

}
