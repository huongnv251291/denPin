package com.tohsoft.ads.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AdDebugLog {
	private static final String TAG = "AdDebugLog";
	public static boolean DEBUG_LOG = true;

	public static void logd(Object obj) {
		if (obj == null || !DEBUG_LOG)
			return;
		String message = String.valueOf(obj);
		String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		if (className.contains("$")) {
			className = className.substring(0, className.lastIndexOf("$"));
		}
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

		Log.d(TAG, "at (" + className + ".java:" + lineNumber + ") " + "[" + methodName + "]" + message);
	}

	public static void logn(Object obj) {
		if (obj == null || !DEBUG_LOG)
			return;
		String message = String.valueOf(obj);
		String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		if (className.contains("$")) {
			className = className.substring(0, className.lastIndexOf("$"));
		}
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

		Log.i(TAG, "at (" + className + ".java:" + lineNumber + ") " + "[" + methodName + "]" + message);
	}

	public static void loge(Object obj) {
		if (obj == null || !DEBUG_LOG)
			return;
		String message = String.valueOf(obj);
		String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		if (className.contains("$")) {
			className = className.substring(0, className.lastIndexOf("$"));
		}
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

		Log.e(TAG, "at (" + className + ".java:" + lineNumber + ") " + "[" + methodName + "]" + message);
	}

	public static void loge(Exception e) {
		if (e == null || !DEBUG_LOG)
			return;
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));

		String message = errors.toString();

		String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		if (className.contains("$")) {
			className = className.substring(0, className.lastIndexOf("$"));
		}
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

		Log.e(TAG, "at (" + className + ".java:" + lineNumber + ") " + "[" + methodName + "]" + message);
	}

	public static void logi(Object obj) {
		if (obj == null || !DEBUG_LOG)
			return;
		String message = String.valueOf(obj);
		String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		if (className.contains("$")) {
			className = className.substring(0, className.lastIndexOf("$"));
		}
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

		Log.i(TAG, "at (" + className + ".java:" + lineNumber + ") " + "[" + methodName + "]" + message);
	}

}
