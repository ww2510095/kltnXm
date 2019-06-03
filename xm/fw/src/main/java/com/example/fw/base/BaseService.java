package com.example.fw.base;

import com.example.fw.Application;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class BaseService {

	@Autowired
	protected BaseDao mBaseDao;

	/**
	 * 根据指定非空字段查询数据
	 * 
	 * @param orderbykey
	 *            排序字段
	 * @param orderbytype
	 *            排序规则，0，升序，1：降序，
	 * @param page
	 *            当前页数
	 * @param rows
	 *            每一页条数
	 *  @return list json
	 * 
	 * 
	 */
	public List<?> getALL(Object t, String orderbykey, Integer orderbytype, Integer page, Integer rows,_where m_where)
			throws Exception {
		Sql msql = new Sql();
		msql.setSql("select * from " + getTabName() + " " + getWhere(t,m_where));
		msql.setOrderbykey(orderbykey);
		msql.setOrderbytype(orderbytype);
		msql.setPage(page);
		msql.setRows(rows);
		//先从缓存里面获取数据
		List<?> listmap =IBeanUtil.ListMap2ListJavaBean(mBaseDao.getALL(msql), t.getClass());
		Integer size1 = rows==null?10:rows;
		for (Object object : listmap) {
			object.getClass().getMethod("setIstabledata",Boolean.class).invoke(object, size1==listmap.size());
		}
		return listmap ;
	}
	public List<?> getALL(Object t, String orderbykey, Integer orderbytype, Integer page, Integer rows)
			throws Exception {
		return getALL(t, orderbykey, orderbytype, page, rows, _where.eq);
	}
	/**
	 * @param page
	 *            当前页数
	 * @param rows
	 *            每一页条数
	 */
	public List<?> getALL(Object t, Integer page, Integer rows) throws Exception {
		return getALL(t, null, 1, page, rows,_where.eq);
	}
	public List<?> getALL(Object t, Integer page, Integer rows,_where m_where) throws Exception {
		return getALL(t, null, 1, page, rows,m_where);
	}

	/**
	 * 根据指定非空字段查询数据
	 * 
	 * @param orderbykey
	 *            排序字段
	 * @param orderbytype
	 *            排序规则，0，升序，1：降序，
	 */
	public List<?> getALL(Object t, String orderbykey, int orderbytype) throws Exception {
		return getALL(t, orderbykey, orderbytype, null, null,_where.eq);
	}
	public List<?> getALL(Object t, String orderbykey, int orderbytype,_where m_where) throws Exception {
		return getALL(t, orderbykey, orderbytype, null, null,m_where);
	}

	/**
	 * 根据指定非空字段查询数据
	 * 
	 * @param orderbykey
	 *            排序字段
	 */
	public List<?> getALL(Object t, String orderbykey) throws Exception {
		return getALL(t, orderbykey, 0,_where.eq);
	}
	public List<?> getALL(Object t, String orderbykey,_where m_where) throws Exception {
		return getALL(t, orderbykey, 0,m_where);
	}



	/**
	 * 根据指定非空字段查询数据
	 */
	public List<?> getALL(Object t) throws Exception {
		return getALL(t, null, 0);
	}
	public List<?> getALL(Object t,_where m_where) throws Exception {
		return getALL(t, null,m_where);
	}
	
	

	/**
	 * 批量插入
	 * 返回插入数量
	 * */
	@Transactional
	public int addList(List<?> listdata) throws Exception{
		long index =System.currentTimeMillis();
		for (Object object : listdata){
			if(object.getClass().getMethod("getId").invoke(object)==null)
				object.getClass().getMethod("setId", Long.class).invoke(object, index);
			index++;

			while (true) {
				try {
					add(object);
					break;
				} catch (Exception e) {
					index=index+1;
					object.getClass().getMethod("setId", Long.class).invoke(object,index);// 设置id
				}
				
			}
			
			
		
//			add(object);
		}
		return listdata.size();
	}

	/**
	 * 添加非空数据
	 */
	public int add(Object obj) throws Exception {
		if(obj.getClass().getMethod("getId").invoke(obj)==null){
			Long lid =System.currentTimeMillis();
			while (getById(lid)!=null) {
				lid = lid+1;
				
			}
			obj.getClass().getMethod("setId", Long.class).invoke(obj,lid);// 设置id
		}else{
			//obj.getClass().getMethod("setId", Long.class).invoke(obj,lid);// 设置id
		}
			
		String MethodName;// 方法名
		Method m;// 方法
		Object value;// 执行结果
		Field[] field = obj.getClass().getDeclaredFields(); // 拿到所有的字段值
		StringBuilder mkey = new StringBuilder();
		StringBuilder mvalue = new StringBuilder();
		int fieldlength = field.length;
		if (fieldlength == 0)
			return 0;// 空的javaben
		for (int i = 0; i < fieldlength; i++) {
			MethodName = field[i].getName();
			MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase());
			m = obj.getClass().getMethod("get" + MethodName);
			value = m.invoke(obj);

			if (value != null ) {
				if(value instanceof Iterable){
					//如果是集合框架的内容则跳过去
					continue;
				}
				mkey.append(MethodName);
				mkey.append(",");
				mvalue.append("'");
				String va = value.toString().trim();
				if(va.length()>0){
					if(va.substring(0,1).equals("？")||va.substring(0,1).equals("?")){
						va=va.substring(1,va.length());
					}
					if(va.substring(va.length()-1,va.length()).equals("？")||va.substring(va.length()-1,va.length()).equals("?")){
						va=va.substring(0,va.length()-1);
					}
				}
			
				mvalue.append(value.toString().trim());
				mvalue.append("',");
			}

		}
		String sql = "INSERT INTO " + getTabName() + " (" + mkey.substring(0, mkey.length() - 1) + ") " + "VALUES("
				+ mvalue.substring(0, mvalue.length() - 1) + ")";
		Application.out("====================INSERT INTO============================");
		Application.out("SQL:" + sql);
		Sql msql = new Sql();
		msql.setSql(sql);
		
		try {
			execSQL(msql);
		} catch (Exception e) {
			obj.getClass().getMethod("setId", Long.class).invoke(obj,Long.valueOf(obj.getClass().getMethod("getId").invoke(obj).toString())+1 );// 设置id
			execSQL(msql);
		}
		

		return 1;
	}

	// 得到最大值+1，用于插入非自增长的字段
	public Long getMaxParameter(String parametername) {
		return mBaseDao.getMaxParameter(getTabName(), parametername);
	}
	
	/**
	 * 根据id查询表所有数据
	 */
	public <T> T  getById(Object id,Class<T> clazz) throws Exception {
		return IBeanUtil.Map2JavaBean(getById(id),clazz);
	}

	/**
	 * 根据字段查出对于数据，只取第一条
	 */
	public <T> T getByparameter(String parametername, String parametervalue,Class<T> clazz) throws Exception {
		return IBeanUtil.Map2JavaBean(getByparameter(parametername, parametervalue), clazz);
	}

	/**
	 * 根据id查询表所有数据
	 */
	public Map<String, Object> getById(Object id) throws Exception {
		if (id instanceof String || id instanceof Integer || id instanceof Long){
			return mBaseDao.getById(getTabName(), id.toString());
		}
			
		return mBaseDao.getById(getTabName(), id.getClass().getMethod("getId").invoke(id).toString());
	}

	/**
	 * 根据字段查出对于数据，只取第一条
	 */
	public Map<String, Object> getByparameter(String parametername, String parametervalue) throws Exception {
		 Map<String, Object> map = mBaseDao.getByparameter(getTabName(), parametername, parametervalue);
		 if(map!=null&&map.size()==0)return null;
		return map;
	}

	/**
	 * 根据id删除数据
	 */
	public void deleteByid(Object id) throws Exception {
		if (id instanceof String || id instanceof Integer || id instanceof Long){
			mBaseDao.deleteByid(getTabName(), id.toString());
			//清除缓存
			return ;
		}
			
		mBaseDao.deleteByid(getTabName(), id.getClass().getMethod("getId").invoke(id).toString());
	}

	/**
	 * 修改不为空的数据,根据id修改*/
	public String updateBySelect(Object obj) throws Exception {
	 
		update(obj, false);
		return "操作成功";
	}

	/**
	 * 修改不所有的数据,根据id修改
	 */
	public void update(Object obj) throws Exception {
		update(obj, true);
	}
	/**
	 * 执行一条查询语句
	 */
	public List<Map<String, Object>> exeSelectSql(Sql sql) throws Exception {
		//从数据库里面获取数据
		Integer size1= sql.getRows()==null?10:sql.getRows();
		List<Map<String, Object>> listmap= mBaseDao.exeSelectSql(sql);
		for (Map<String, Object> map : listmap) {
			try {
				map.put("ISTABLEDATA", size1==listmap.size());
				map2map(map);
			} catch (Exception e) {}

		}
		//将结果缓存
		return listmap;
	}
	/**对map重构，把数值转化为string序列化到redis里面
	 * @param map 参考型集合
	 * */
	private static Map<String, Object> map2map(Map<String, Object> map){
		Set<String> mSet = map.keySet();
		for (String string : mSet) {
			//将数值转化为字符串
			if(map.get(string).getClass().toString().trim().equals("class java.math.BigDecimal"))
				map.put(string,map.get(string).toString());
		}
		return map;
	}


	/**
	 * 执行一条无返回的语句,不缓存
	 */
	public String execSQL(Sql msql) throws Exception {
		mBaseDao.execSQL(msql);
		return "操作成功";
	}


	
	/**
	 * 修改不所有的数据
	 */
	private void update(Object obj, boolean updateNull) throws Exception {
		String idvalue = null;// id值
		String MethodName;// 方法名
		Method m;// 方法
		Object value;// 执行结果
		Field[] field = obj.getClass().getDeclaredFields(); // 拿到所有的字段值
		StringBuilder sql = new StringBuilder("update " + getTabName() + " set ");
		int fieldlength = field.length;
		if (fieldlength == 0)
			return;// 空的javaben
		for (int i = 0; i < fieldlength; i++) {
			MethodName = field[i].getName();
			MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase());
			if ("id".equals(MethodName.toLowerCase())) {
				m = obj.getClass().getMethod("get" + MethodName);
				value = m.invoke(obj);
				if (value == null)
					return;// id为空，没有可以修改的数据
				idvalue = value.toString();
			} else {
				try {
					m = obj.getClass().getMethod("get" + MethodName);
//					if(MethodName.equals("Goldcoin")){
//						System.out.println();
//					}
					value = m.invoke(obj);
				} catch (Exception e) {
					value=null;
				}
				if(value !=null){
					if(value instanceof Iterable){
						//如果是集合框架的内容则跳过去
						continue;
					}
				}

				if ("删除".equals(value))
					sql.append(MethodName + "='', ");// 拼接值不空sql
				else if (value != null && !Stringutil.isBlank(value.toString()))
					sql.append(MethodName + "='" + value.toString() + "', ");// 拼接值不空sql
				 
				else if(updateNull)
					sql.append(MethodName + "=null, ");// 拼接空的sql

			}
		}
		sql = new StringBuilder(sql.toString().trim().substring(0, sql.toString().trim().length() - 1));
		if (!Stringutil.isBlank(idvalue)) {
			sql.append(" where id ='" + idvalue + "'");
			Sql msql = new Sql();
			msql.setSql(sql.toString());
			
			//修改数据
			execSQL(msql);
		}

	}

	public String getWhere(Object obj,_where m_where,_or_and or_and) throws Exception {
		if(obj==null) return "";
		if(m_where==null)m_where= _where.eq;
		String MethodName;// 方法名
		Method m;// 方法
		Object value;// 执行结果
		StringBuilder sb = new StringBuilder(" where 1=1 ");
		Field[] field = obj.getClass().getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
			MethodName = field[i].getName();
			MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase());
			m = obj.getClass().getMethod("get" + MethodName);
			value = m.invoke(obj);
			if (value != null&&value.toString().trim().length()!=0){
				if(m_where==_where.like&&(!MethodName.toLowerCase().equals("id")))
					sb.append(or_and.toString() +" " + MethodName + " like '%" + value + "%' ");
				else
					sb.append(or_and.toString()  +" " +MethodName + "='" + value + "' ");
			}

		}
		return sb.toString();
	}
	public String getWhere(Object obj,_where m_where) throws Exception {
		return  getWhere(obj,m_where,_or_and.and);
	}
	public String getWhere(Object obj) throws Exception {
		return getWhere(obj,_where.eq);
	}
	public enum _where{
		like,//模糊解锁
		eq//精确解锁
	}
	public enum _or_and{
		or,
		and

	}

	/**
	 * 根据条件删除数据
	 */
	public void deleteBySelect(Object obj) throws Exception {
		String MethodName;// 方法名
		Method m;// 方法
		Object value;// 执行结果
		Field[] field = obj.getClass().getDeclaredFields(); // 拿到所有的字段值
		StringBuilder sql = new StringBuilder();
		int fieldlength = field.length;
		if (fieldlength == 0)
			return ;// 空的javaben
		for (int i = 0; i < fieldlength; i++) {
			MethodName = field[i].getName();
			MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase());
				m = obj.getClass().getMethod("get" + MethodName);
				value = m.invoke(obj);

				if (value != null && !Stringutil.isBlank(value.toString()))
					sql.append(MethodName + "='" + value.toString() + "'     and ");// 拼接值不空sql
			
		}
		if(Stringutil.isBlank(sql.toString()))throw new RunException("条件错误");
		sql = new StringBuilder(sql.toString().trim().substring(0, sql.toString().trim().length() - 5));
		
		Sql msql = new Sql();
		msql.setSql("delete " + getTabName() + " where " +sql.toString());
		execSQL(msql);

	}
	
    public static Map<String, Object> transformUpperCase(Map<String, Object> orgMap)
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (orgMap == null || orgMap.isEmpty())
        {
            return resultMap;
        }

        Set<String> keySet = orgMap.keySet();
        for (String key : keySet)
        {
            resultMap.put(key.toLowerCase(), orgMap.get(key));
        }

        return resultMap;
    }
	// 表名
	protected abstract String getTabName();


	
}
