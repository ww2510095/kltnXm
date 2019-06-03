package com.bm.user.goldcoin;

import com.bm.base.BaseEN;

/*
 * 默认积分规则
 * */
public class GoldcoinDefault extends BaseEN{
		private Long id;
		private Integer percentage;//百分比
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Integer getPercentage() {
			return percentage;
		}
		public void setPercentage(Integer percentage) {
			this.percentage = percentage;
		}
		@Override
		public String toString() {
			return "GoldcoinDefaultid" + id + "1Percentage" + percentage;
		}
		
}
