package com.misday.pg.util;


import java.nio.charset.Charset;

public class TextUtils {
	public static Charset UTF8 = Charset.forName("UTF-8");

	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		
		if (str.length() < 1) {
			return true;
		}
		
		return false;
	}
	
	public static boolean equal(String a, String b) {
		if (a == null || b == null) {
			return false;
		}
		
		return a.equals(b);
	}
}
