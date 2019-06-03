package com.bm.base.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bm.Aenum.Enum_i;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Auth_wx{
	
	
	public x_key_ke[] x_key() default {};
	
	
	public enum x_key_ke implements Enum_i {
		admin(0, "管理员"),
		gong_ys(1, "供应商"),
		men_d(2, "门店"),
		jing_l(3, "督导"),
		lao_b(4, "老板"),
		;

		private x_key_ke(int i, String v) {
			_k = i;
			_v = v;
		}

		private int _k;
		private String _v;

		@Override
		public String getValue() {
			return _v;
		}

		@Override
		public int getKey() {
			return _k;
		}

	}
	


}
