package com.bm.Aenum;

public enum Orderrelevance_gdf implements Enum_i {

	DEFAULT(1, "默认规则"), key(2, "自定义规则");

	private Orderrelevance_gdf(int i, String v) {
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
