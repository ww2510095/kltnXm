package com.bm.user.goldcoin;

import com.bm.base.BaseEN;

/*
 * 积分规则
 * */
public class GoldcoinKey extends BaseEN{
		private Long id;
		private Integer num;//分
		private String youcode;//条码
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Integer getNum() {
			return num;
		}
		public void setNum(Integer num) {
			this.num = num;
		}
		public String getYoucode() {
			return youcode;
		}
		public void setYoucode(String youcode) {
			this.youcode = youcode;
		}
		@Override
		public String toString() {
			return "GoldcoinKeyid" + id + "1num" + num + "1youcode" + youcode;
		}
		
		
		
}
