package com.xiaotu.makeplays.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

public class BaseDao<T> {
	
	private Logger logger = LoggerFactory.getLogger(BaseDao.class);
	
	private String TABLE_NAME_FIELD="TABLE_NAME";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 查询方法
	 * 
	 * @param sql
	 *            标准sql
	 * @param args
	 *            object参数数组
	 * @param classT
	 *            返回list泛型定义class
	 * @param page
	 *            分页对象
	 * @return
	 */
	public List<T> query(String sql, Object[] args, Class<T> classT, Page page) {

		RowMapper<T> rm = ParameterizedBeanPropertyRowMapper
				.newInstance(classT);

		if (null != page) {
			page.setTotal(getResultCount(sql, args));

			sql += " limit " + page.getNextIndex() + "," + page.getPagesize();
		}
		long exceTime=System.currentTimeMillis();
		if (null == args) {
			List<T> list = this.getJdbcTemplate().query(sql, rm);
			logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
			return list;
		} else {
			List<T> list = this.getJdbcTemplate().query(sql, args, rm);
			logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
			return list;
		}

	}

	/**
	 * 查询方法
	 * 
	 * @param sql
	 *            标准sql
	 * @param args
	 *            object参数数组
	 * @param classT
	 *            返回list泛型定义class
	 * @param page
	 *            分页对象
	 * @param orderBy
	 *            需要排序的字段
	 * 
	 * @param direction
	 *            排序的方向（desc，asc）
	 * 
	 * @return
	 */
	public List<T> query(String sql, Object[] args, Class<T> classT, Page page,
			String orderBy, String direction) {

		RowMapper<T> rm = ParameterizedBeanPropertyRowMapper
				.newInstance(classT);

		if (StringUtils.isNotBlank(orderBy)) {
			sql += " order by " + orderBy;

			if (StringUtils.isNotBlank(direction)) {
				sql += " " + direction;
			}
		}

		if (null != page) {
			page.setTotal(getResultCount(sql, args));

			sql += " limit " + page.getNextIndex() + "," + page.getPagesize();
		}

//		if (null == args) {
//			return this.getJdbcTemplate().query(sql, rm);
//		} else {
//			return this.getJdbcTemplate().query(sql, args, rm);
//		}
		long exceTime=System.currentTimeMillis();
		if (null == args) {
			List<T> list = this.getJdbcTemplate().query(sql, rm);
			logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
			return list;
		} else {
			List<T> list = this.getJdbcTemplate().query(sql, args, rm);
			logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
			return list;
		}
	}

	/**
	 * 查询方法
	 * 
	 * @param sql
	 *            标准sql
	 * @param args
	 *            object参数数组
	 * @param page
	 *            分页对象
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List query(String sql, Object[] args, Page page) {

		if (null != page) {
			page.setTotal(getResultCount(sql, args));

			sql += " limit " + page.getNextIndex() + "," + page.getPagesize();
		}
		long exceTime=System.currentTimeMillis();
		if (null == args) {
			List list = this.getJdbcTemplate().queryForList(sql);
			logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
			return list;
		} else {
			List list = this.getJdbcTemplate().queryForList(sql, args);
			logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
			return list;
		}

	}

	public int getResultCount(String sql, Object[] args) {

		sql = "select count(*) from (" + sql + ") res";
		long exceTime=System.currentTimeMillis();
		int count=this.getJdbcTemplate().queryForInt(sql, args);
		logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
		return count;
	}

	Object[] objArray;

	/**
	 *  * 自动update方法 obj必须为一个与数据库表相对应的一个实体类并且字段名也必须和数据库的字段名相同。
	 * 实体类中必须有一个名为tableName属性，并赋值为相对应的表名，例如:public String TABLE_NAME="fieldName";
	 * @param obj
	 * @param idFieldName 实体类的id字段名
	 * @return
	 * @throws Exception
	 */
	public int update(Object obj,String idFieldName) throws Exception {

		if (null == obj) {
			return 0;
		}

		// 拿到该类
		Class<?> clz = obj.getClass();
		// 获取实体类的所有属性，返回Field数组

		Field fieldTableName = clz.getField(this.TABLE_NAME_FIELD);


		String tableName = (String) fieldTableName.get(obj);

		Field[] fields = clz.getDeclaredFields();
		StringBuffer updateSql = new StringBuffer();
		updateSql.append(" update ");
		updateSql.append(tableName);
		updateSql.append(" set ");

		List<Object> list = new ArrayList<Object>();

		for (Field field : fields) {

			if (this.TABLE_NAME_FIELD.equals(field.getName())
					||field.getName().indexOf("noField")==0) {
				continue;
			}

			Method m = obj.getClass().getMethod(
					"get" + getMethodName(field.getName()));

			Object val = m.invoke(obj);
			if (val != null && !"".equals(val.toString())) {

				updateSql.append(field.getName());
				updateSql.append("=?,");
				list.add(val);

			}

		}
		updateSql = new StringBuffer(updateSql.substring(0,
				updateSql.length() - 1));
		updateSql.append("  where "+idFieldName+"=?");
		list.add(obj.getClass().getMethod(
				"get" + getMethodName(idFieldName)).invoke(obj));
		objArray = list.toArray();

		return this.jdbcTemplate.update(updateSql.toString(),
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						for (int i = 0; i < objArray.length; i++) {
							ps.setObject(i + 1, objArray[i]);
						}
					}
				});
	}
	
	/**
	 *  * 自动update方法 obj必须为一个与数据库表相对应的一个实体类并且字段名也必须和数据库的字段名相同。
	 * 实体类中必须有一个名为tableName属性，并赋值为相对应的表名，例如:public String TABLE_NAME="fieldName";
	 * 
	 * 允许空值
	 * @param obj
	 * @param idFieldName 实体类的id字段名
	 * @return
	 * @throws Exception
	 */
	public int updateWithNull(Object obj, String idFieldName) throws Exception {

		if (null == obj) {
			return 0;
		}

		// 拿到该类
		Class<?> clz = obj.getClass();
		// 获取实体类的所有属性，返回Field数组

		Field fieldTableName = clz.getField(this.TABLE_NAME_FIELD);


		String tableName = (String) fieldTableName.get(obj);

		Field[] fields = clz.getDeclaredFields();
		StringBuffer updateSql = new StringBuffer();
		updateSql.append(" update ");
		updateSql.append(tableName);
		updateSql.append(" set ");

		List<Object> list = new ArrayList<Object>();

		for (Field field : fields) {

			if (this.TABLE_NAME_FIELD.equals(field.getName())
					||field.getName().indexOf("noField")==0) {
//				if (id.equals(field.getName())) {
//					Method m = obj.getClass().getMethod(
//							"get" + getMethodName(field.getName()));
//
//					Object val = m.invoke(obj);
//					idValue = val;
//				}
				continue;
			}

			Method m = obj.getClass().getMethod(
					"get" + getMethodName(field.getName()));

			Object val = m.invoke(obj);
			updateSql.append(field.getName());
			updateSql.append("=?,");
			list.add(val);

		}
		updateSql = new StringBuffer(updateSql.substring(0,
				updateSql.length() - 1));
		updateSql.append("  where "+idFieldName+"=?");
		list.add(obj.getClass().getMethod(
				"get" + getMethodName(idFieldName)).invoke(obj));
		objArray = list.toArray();

		return this.jdbcTemplate.update(updateSql.toString(),
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						for (int i = 0; i < objArray.length; i++) {
							ps.setObject(i + 1, objArray[i]);
						}
					}
				});
	}

	/**
	 * 自动insert方法 obj必须为一个与数据库表相对应的一个实体类并且字段名也必须和数据库的字段名相同。
	 * 实体类中必须有一个名为tableName属性，并赋值为相对应的表名。 需要使用者为id主键赋值
	 */
	public synchronized int add(Object obj) throws Exception {

		if (null == obj) {
			return 0;
		}

		// 拿到该类
		Class<?> clz = obj.getClass();
		// 获取实体类的所有属性，返回Field数组
		Field fieldTableName = clz.getField(this.TABLE_NAME_FIELD);

		if (null == fieldTableName) {
			logger.info("error:insert object "+obj.getClass().getName()+" not have TABLE_NAME");
			return 0;
		}
		
		String tableName = (String) fieldTableName.get(obj);

		Field[] fields = clz.getDeclaredFields();
		StringBuffer insertSql = new StringBuffer();
		insertSql.append("insert into ");
		insertSql.append(tableName);
		insertSql.append(" (");

		List<Object> list = new ArrayList<Object>();

		for (Field field : fields) {

			if (this.TABLE_NAME_FIELD.equals(field.getName())
					|| field.getName().indexOf("noField")==0) {
				continue;
			}

			Method m = obj.getClass().getMethod(
					"get" + getMethodName(field.getName()));

			Object val = m.invoke(obj);
			if (val != null) {
				insertSql.append(field.getName());
				insertSql.append(",");
				list.add(val);
			}
		}
		insertSql = new StringBuffer(insertSql.substring(0,
				insertSql.length() - 1));
		insertSql.append(" ) values (");
		objArray = new Object[list.size()];

		for (int i = 0; i < list.size(); i++) {
			objArray[i] = list.get(i);
			insertSql.append("?,");
		}

		insertSql = new StringBuffer(insertSql.substring(0,
				insertSql.length() - 1));
		insertSql.append(" ) ");
		return this.jdbcTemplate.update(insertSql.toString(),
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						for (int i = 0; i < objArray.length; i++) {
							ps.setObject(i + 1, objArray[i]);
						}
					}
				});
	}
	
	
	/**
	 * 批量新增
	 * 实体类必须与数据库对应的表字段在个数和名称上完全相同
	 * 实体类中必须有一个名为TABLE_NAME属性，并赋值为相对应的表名
	 * @param obj
	 * @param objList
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	public synchronized void addBatch(List<T> objList, Class<T> clz) throws Exception {
		//获取对应的数据库表名
		Field fieldTableName = clz.getField(this.TABLE_NAME_FIELD);
		String tableName = (String) fieldTableName.get(objList);

		//拼接sql语句
		Field[] fields = clz.getDeclaredFields();
		StringBuffer insertSql = new StringBuffer();
		insertSql.append("insert into ");
		insertSql.append(tableName);
		insertSql.append(" (");

		List<String> fieldNameList = new ArrayList<String>();

		for (Field field : fields) {
			if (this.TABLE_NAME_FIELD.equals(field.getName()) || field.getName().indexOf("noField")==0) {
				continue;
			}
			insertSql.append(field.getName());
			insertSql.append(",");
			fieldNameList.add(field.getName());
		}
		
		insertSql = new StringBuffer(insertSql.substring(0, insertSql.length() - 1));
		insertSql.append(" ) values (");
		for (int i = 0; i < fieldNameList.size(); i++) {
			insertSql.append("?,");
		}

		insertSql = new StringBuffer(insertSql.substring(0, insertSql.length() - 1));
		insertSql.append(" ) ");
		
		
		//拼接要插入的值
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (T obj : objList) {
			Class<?> myClz = obj.getClass();
			Field[] myFields = myClz.getDeclaredFields();
			
			Object[] args = new Object[myFields.length - 1];	//把每个类中TABLE_NAME字段排除
			
			int index = 0;	//自定义一个值的数组下标，为了防止一下循环中continue情况的产生
			for (int i = 0; i < myFields.length; i++) {
				Field myField = myFields[i];
				if (this.TABLE_NAME_FIELD.equals(myField.getName()) || myField.getName().indexOf("noField")==0) {
					continue;
				}
				
				Method m = myClz.getMethod("get" + getMethodName(myField.getName()));
				
				Object val = m.invoke(obj);
				args[index] = val;
				index ++;
			}
			
			batchArgs.add(args);
		}
		
		//调用批量新增方法
		this.jdbcTemplate.batchUpdate(insertSql.toString(), batchArgs);
	}
	
	/**
	 * 批量更新
	 * 实体类必须与数据库对应的表字段在个数和名称上完全相同
	 * 实体类中必须有一个名为TABLE_NAME属性，并赋值为相对应的表名
	 * @param objList
	 * @param clz
	 * @return
	 * @throws Exception
	 */
	public void updateBatch(final List<T> objList, String idFieldName, Class<T> clz) throws Exception {
		//获取对应的数据库表名
		Field fieldTableName = clz.getField(this.TABLE_NAME_FIELD);
		String tableName = (String) fieldTableName.get(objList);

		//拼接sql语句
		Field[] fields = clz.getDeclaredFields();
		StringBuffer updateSql = new StringBuffer();
		updateSql.append(" update ");
		updateSql.append(tableName);
		updateSql.append(" set ");


		for (Field field : fields) {
			if (this.TABLE_NAME_FIELD.equals(field.getName()) || field.getName().indexOf("noField") == 0) {
				continue;
			}
			updateSql.append(field.getName());
			updateSql.append(" = ?,");
		}
		
		updateSql = new StringBuffer(updateSql.substring(0, updateSql.length() - 1));
		updateSql.append(" where " + idFieldName + " = ? ");

		//拼接值
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (T obj : objList) {
			Class<?> myClz = obj.getClass();
			Field[] myFields = myClz.getDeclaredFields();
			
			Object[] args = new Object[myFields.length];
			
			int index = 0;	//自定义一个值的数组下标，为了防止以下循环中continue情况的产生
			for (int i = 0; i < myFields.length; i++) {
				Field myField = myFields[i];
				if (this.TABLE_NAME_FIELD.equals(myField.getName()) || myField.getName().indexOf("noField")==0) {
					continue;
				}
				
				Method m = myClz.getMethod("get" + getMethodName(myField.getName()));
				
				Object val = m.invoke(obj);
				args[index] = val;
				index ++;
			}
			
			Method getIdMethod = myClz.getMethod("get" + getMethodName(idFieldName));
			Object idObj = getIdMethod.invoke(obj);
			
			args[myFields.length - 1] = idObj;
			
			batchArgs.add(args);
		}
		
		//调用批量新增方法
		this.jdbcTemplate.batchUpdate(updateSql.toString(), batchArgs);
	}
	
	private String getMethodName(String fildeName) throws Exception {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	/**
	 * 批量修改
	 * 
	 * @param editData
	 *            json格式的字符串
	 * @param tableName
	 *            需要修改表的名称
	 * @return 修改的条数
	 * @throws Exception
	 */
	public synchronized int updateMany(String editData, String tableName) throws Exception {
		JSONObject editObj = JSONObject.fromObject(editData);
		JSONArray head = editObj.getJSONArray("head");
		JSONArray datas = editObj.getJSONArray("data");
		String sql = updateSql(head, tableName);
		int result = 0;
		for (int i = 0; i < datas.size(); i++) {
			JSONArray data = datas.getJSONArray(i);
			Object[] obj = new Object[data.size()];
			for (int j = 0; j < data.size(); j++) {
				obj[j] = data.getString(j);
			}
			result += this.getJdbcTemplate().update(sql.toString(), obj);
		}

		return result;
	}

	/**
	 * 单条修改
	 * 
	 * @param editData
	 *            json格式的字符串
	 * @param tableName
	 *            需要修改表的名称
	 * @return 修改的条数
	 * @throws Exception
	 */
	public int updateOne(String editData, String tableName) throws Exception {
		JSONObject editObj = JSONObject.fromObject(editData);
		JSONArray head = editObj.getJSONArray("head");
		JSONArray data = editObj.getJSONArray("data");
		String sql = updateSql(head, tableName);
		Object[] obj = new Object[data.size()];
		for (int i = 0; i < data.size(); i++) {
			obj[i] = data.getString(i);
		}
		return this.getJdbcTemplate().update(sql.toString(), obj);
	}

	/**
	 * 单条删除
	 * 
	 * @param id
	 *            需要删除的数据的id
	 * @param idName
	 *            需要删除的主键名称
	 * @param tableName
	 *            需要删除数据的表格名称
	 * @return 删除的条数
	 * @throws Exception
	 */
	public int deleteOne(String id, String idName, String tableName)
			throws Exception {
		if (id == null) {
			return 0;
		}
		return deleteMany(new String[] { id }, idName, tableName);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 *            需要删除的数据的id数组集合
	 * @param idName
	 *            需要删除的主键名称
	 * @param tableName
	 *            需要删除数据的表格名称
	 * @return 删除的条数
	 * @throws Exception
	 */
	public int deleteMany(String[] ids, String idName, String tableName)
			throws Exception {
		if (ids == null || ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer("DELETE FROM " + tableName
				+ " WHERE " + idName + " IN ('");
		for (String id : ids) {
			sql.append(id + "','");
		}
		sql = new StringBuffer(sql.substring(0, sql.length() - 2));
		sql.append(")");
		return this.getJdbcTemplate().update(sql.toString());
	}

	private String updateSql(JSONArray head, String tableName) {
		StringBuffer sql = new StringBuffer("UPDATE " + tableName + " SET ");
		// 遍历head，获取需要修改的字段名
		for (int i = 0; i < head.size(); i++) {
			if (i == head.size() - 1) {
				sql = new StringBuffer(sql.substring(0, sql.length() - 1));
				sql.append(" WHERE " + head.getString(i) + "=?");
				break;
			}
			sql.append(head.getString(i) + "=?,");
		}
		return sql.toString();
	}

	/**
	 * 通过对象id来查询对象
	 * 
	 * @param entity
	 * @param idFieldName
	 * @return
	 * @throws Exception
	 * @throws NoSuchMethodException
	 */
	public T getEntityById(T entity, String idFieldName)
			throws NoSuchMethodException, Exception {
		Class classT=entity.getClass();
		Field fieldTableName = classT.getField(this.TABLE_NAME_FIELD);

		if (null == fieldTableName||StringUtils.isBlank(idFieldName)) {
			return null;
		}

		String tableName = (String) fieldTableName.get(entity);
		
		Method m = classT.getMethod("get" + getMethodName(idFieldName));

		Object val = m.invoke(entity);
		String objectId = (String) val;

		if (StringUtils.isBlank(objectId)) {
			return null;
		}

		String sql = "select * from " + tableName + " where " + idFieldName + " ='"
				+ objectId + "'";
		RowMapper<T> rm = ParameterizedBeanPropertyRowMapper
				.newInstance(classT);
		entity = this.jdbcTemplate.queryForObject(sql, rm);
		return entity;
	}
	
	/**
	 * 获取单个对象
	 * @param sql
	 * @param params
	 * @param classT
	 * @return
	 * @throws Exception 
	 */
	public T queryForObject(String sql,Object[] params, Class<T> classT) throws Exception{
		try {
			RowMapper<T> rm = ParameterizedBeanPropertyRowMapper
					.newInstance(classT);
			long exceTime=System.currentTimeMillis();
			
			T t = null;
			if (getResultCount(sql, params) == 1) {
				t = this.jdbcTemplate.queryForObject(sql,params, rm);
			}
			logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
			return t;
		} catch (Exception e) {
			if(e instanceof EmptyResultDataAccessException){
				logger.warn(e.getMessage());
				return null;
			}else{
				throw e;
			}
		}
		
	}

	/**
	 * 创建数据库字段
	 * @param colunmName
	 * @return
	 */
	public static String buildColunmName(String colunmName){
		String strArray[]=colunmName.split("[A-Z]");
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for(String str:strArray){
			if(first){
				sb.append("f_"+str);
				first=false;
			}else{
				sb.append("_"+colunmName.substring(colunmName.indexOf(str)-1,colunmName.indexOf(str))+str);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 查询全部
	 * @param entity
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public List<T> getAll(T entity) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Field fieldTableName = entity.getClass().getField(this.TABLE_NAME_FIELD);
		
		String sql = "select * from "+(String) fieldTableName.get(entity);
		long exceTime=System.currentTimeMillis();
		List<T> list = this.query(sql, null, (Class<T>) entity.getClass(), null);
		logger.debug("exceutTime="+(System.currentTimeMillis()-exceTime));
		
		return list;
		
	}
	
}
