package com.cc.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * 自动记录异常信息至SD卡
 *
 * @author cc
 * @date 2012-12-25 下午9:31:43
 */
public class LogCrashHandler implements UncaughtExceptionHandler {

	private Context context;
	private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static LogCrashHandler crashHandler;
	private UncaughtExceptionHandler defaultExceptionHandler;
	private final static String NMMS_NAME = "自动记录异常信息至SD卡";
	private final static String SDPATH = Environment
			.getExternalStorageDirectory() + "/cc/";// 文件目录
	private final static String ERROR_FILE_NAME = "cc_error.log";// 文件名称
	private final static int FILE_MAX_SIZE = 5;// 限制文件最大大小

	private LogCrashHandler() {

	}

	public static LogCrashHandler getInstance() {
		if (crashHandler == null) {
			crashHandler = new LogCrashHandler();
		}
		return crashHandler;
	}

	/**
	 * 初始化方法
	 */
	public void init(Context context) {
		this.context = context;
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(thread, ex) && defaultExceptionHandler != null) {
			defaultExceptionHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 程序异常处理方法
	 * */
	@SuppressLint("SimpleDateFormat")
	private boolean handleException(Thread thread, Throwable ex) {
		try {
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
				return false;
			}
			StringBuilder sb = new StringBuilder(NMMS_NAME);
			Date firstDate = new Date(System.currentTimeMillis());
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
			String str = formatter.format(firstDate);
			sb.append("\nModel:" + android.os.Build.MODEL);// 手机型号
			sb.append("\nVersion:" + android.os.Build.VERSION.RELEASE);// android版本
			sb.append("\nDate:" + str); // 写入当前日期
			sb.append("\nInfo:\n"); // 错误信息
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			sb.append(writer.toString()).append("\n");// 写入错误信息
			// 创建文件夹及文件
			File dir = new File(SDPATH);
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}
			File file = new File(dir, ERROR_FILE_NAME);
			if (!file.exists()) {
				file.createNewFile();
			}
			// 文件大小限制
			@SuppressWarnings("resource")
			FileInputStream inputStream = new FileInputStream(file);
			int fileSize = inputStream.available() / 1024; // 单位是KB
			int totalSize = FILE_MAX_SIZE * 1024;
			if (fileSize > totalSize) {
				if (file.delete()) {
					// 删除成功,重新创建一个文件
					File filesTwo = new File(dir, ERROR_FILE_NAME);
					if (!filesTwo.exists()) {
						filesTwo.createNewFile();
					}
					writeDataToFile(file, sb.toString());
				} else {
					// 删除失败,清空后写入空格
					writeDataToFile(file, "");
				}
			} else {
				writeDataToFile(file, sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		defaultExceptionHandler.uncaughtException(thread, ex);
		return true;
	}

	/**
	 * 写入文件内容
	 * */
	private void writeDataToFile(File file, String data) throws Exception {
		if (file != null) {
			FileOutputStream fos = null;
			if (data == null || data.equals("")) {
				fos = new FileOutputStream(file, false);
				fos.write(" \n".getBytes());
			} else {
				fos = new FileOutputStream(file, true);// 追加内容
				fos.write(data.getBytes());
			}
			fos.close();
		}
	}
}
