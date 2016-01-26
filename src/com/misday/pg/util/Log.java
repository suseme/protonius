package com.misday.pg.util;

public class Log {
	private static Integer key = 1;

	public static void i(String tag, String msg) {
		synchronized (key) {
			System.out.println("I:[" + tag + "]: " + msg);
		}
	}

	public static void d(String tag, String msg) {
		synchronized (key) {
			System.out.println("D:[" + tag + "]: " + msg);
		}
	}

	public static void v(String tag, String msg) {
		synchronized (key) {
			System.out.println("V:[" + tag + "]: " + msg);
		}
	}

	public static void w(String tag, String msg) {
		synchronized (key) {
			System.out.println("W:[" + tag + "]: " + msg);
		}
	}

	public static void e(String tag, String msg) {
		synchronized (key) {
			System.out.println("E:[" + tag + "]: " + msg);
		}
	}

	public static void hex(String tag, byte[] data, int off, int len) {
		len += off;

		synchronized (key) {
			System.out.print(tag + ": " + len + ": [");
			for (int i = off; i < len; i++) {
				System.out.printf("%02x, ", data[i]);
			}
			System.out.println("]");
		}
	}
}
