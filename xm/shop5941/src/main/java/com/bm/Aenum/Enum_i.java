package com.bm.Aenum;

public interface Enum_i {
	default String getValue(){
		return toString();
	}
	default int getKey(){
		return 0;
	}

}
