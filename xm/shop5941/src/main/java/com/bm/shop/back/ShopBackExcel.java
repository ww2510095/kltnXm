package com.bm.shop.back;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShopBackExcel {

	/**
	 * 店铺Excel数据2次处理
	 */
	public List<Shopback> readShop(List<List<String>> data, String phone) {
		List<Shopback> lh = new ArrayList<Shopback>();
		int i = 0;
		for (List<String> list : data) {
			i = 0;
			Shopback sh = new Shopback();
			sh.setCode(list.get(i));
			i++;
			sh.setShopname(list.get(i));
			i++;// 店铺名字
			sh.setShoptype(list.get(i));
			i++;// 店铺类型
			sh.setShopresume(list.get(i));
			i++;// 店铺简介
			sh.setProvince(list.get(i));
			i++;// 省
			sh.setCity(list.get(i));
			i++;// 市
			sh.setArea(list.get(i));
			i++;// 区
			sh.setStreet(list.get(i));
			i++;// 街道
			sh.setDetailed(list.get(i));
			
			Map<String, String> param = getAreaLongAndDimen(list.get(i));i++;// 详细地址
//			 sh.setLongitude(list.get(i));i++;//经度
//			 sh.setLatitude(list.get(i));i++;//纬度
			sh.setLongitude(param.get("longitude"));
			i++;// 经度
			sh.setLatitude(param.get("latitude"));
			i++;// 纬度

			sh.setMemberid(list.get(i));
			i++;// 用户
			sh.setSuperid(list.get(i));
			i++;// 父级
			sh.setOneid(list.get(i));
			i++;// 顶级
			sh.setLogo(list.get(i));
			i++;
			sh.setSystemtype(list.get(i));
			i++;// 所属系统
			sh.setAddtime(System.currentTimeMillis() + "");
			sh.setAdminphone(phone);
			lh.add(sh);
		}

		return lh;
	}

	/** * 根据地址获取经纬度 */

	public static Map<String, String> getAreaLongAndDimen(String addr) {
		Map<String, String> param = new HashMap<String, String>();

		// try {
		//
		// addr = new
		// String(addr.getBytes("UTF-8"),"GBK");}//因为高德地图用的是linux系统，所以其使用的是gbk的编码，所以在这里你用的是utf-8的话，就得转换成gbk的编码格式。
		//
		// catch (UnsupportedEncodingException e1) {
		//
		// e1.printStackTrace();
		//
		// }

		String str = "http://restapi.amap.com/v3/geocode/geo?key=fe83b3292250986b66f84fb762404cbc&address=" + addr
				+ "&city=";

		InputStream inputStream = null;

		try {

			URL url = new URL(str);

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.setRequestMethod("GET");

			urlConnection.setConnectTimeout(5 * 1000);// 超时时间

			urlConnection.setRequestProperty("contentType", "utf-8");// 字符集

			urlConnection.connect();

			inputStream = urlConnection.getInputStream();

			JsonNode jsonNode = new ObjectMapper().readTree(inputStream);// jackson

			String[] degree = jsonNode.findValue("geocodes").findValue("location").textValue().split(",");

			param.put("longitude", degree[0]);

			param.put("latitude", degree[1]);


		}  catch (Exception e) {


		} finally {

			try {

				if (null != inputStream) {

					inputStream.close();

				}

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

		return param;

	}

}
