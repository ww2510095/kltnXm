package com.bm.file;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.myjar.desutil.DESUtils;

@Service
public class FileService extends BaseService{

	
	@Override
	protected String getTabName() {
		return "myfiles";
	}
	
	@Override
	public int add(Object obj) throws Exception {
		Long sid =System.currentTimeMillis();
		obj.getClass().getMethod("setId", Long.class).invoke(obj,sid);// 设置id
		while (true) {
			try {
				return super.add(obj);
			} catch (Exception e) {
				sid=sid+1;
				obj.getClass().getMethod("setId", Long.class).invoke(obj,sid);// 设置id
			}
			
		}
		
		
	}
	
	
	public  String getFileMd5(byte[] filebyte) throws NoSuchAlgorithmException{
//		String hashString;
//		MessageDigest md5 = MessageDigest.getInstance("MD5");
//		byte[] uploadBytes1 = Arrays.copyOf(filebyte, filebyte.length);
//		String s =uploadBytes1.length+"";
//		StringBuilder sb = new StringBuilder(filebyte.length);
//		char[] ch = sb.toString().toCharArray();
//		for (char b : ch) {
//			sb.append(b+"1");
//		}
//		DESUtils.password(sb.toString());
//		sb.append(sb.length());
//		sb.append(filebyte.length);
//		uploadBytes1=	md5.digest(sb.toString().getBytes());
//		hashString = new BigInteger(1, uploadBytes1).toString(16);
//		
//		return hashString+s;
		StringBuilder sb = new StringBuilder(filebyte.length);
		for (byte b : filebyte) {
			sb.append(b+"1");
		}
		char[] ch = sb.toString().toCharArray();
		for (char b : ch) {
			sb.append(b+"1");
		}
		String sa =DESUtils.password(sb.toString());
		
//		byte[] uploadBytes1 = Arrays.copyOf(filebyte, filebyte.length);
		String s = DigestUtils.sha512Hex(sa.getBytes());
		String s1 =DigestUtils.md5Hex(sa.getBytes());
		return s+s1;

	}
	public  String getMd5(byte[] filebyte) throws NoSuchAlgorithmException{
//		String hashString;
//		MessageDigest md5 = MessageDigest.getInstance("MD5");
//		byte[] uploadBytes1 = Arrays.copyOf(filebyte, filebyte.length);
//		uploadBytes1 = md5.digest(uploadBytes1);
//		hashString = new BigInteger(1, uploadBytes1).toString(16);
		byte[] uploadBytes1 = Arrays.copyOf(filebyte, filebyte.length);
		String s = DigestUtils.sha512Hex(uploadBytes1);
		String s1 =DigestUtils.md5Hex(uploadBytes1);
		return s+s1;
		
	}
	


}
