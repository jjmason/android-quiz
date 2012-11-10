package com.jjm.android.quiz.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.jjm.android.quiz.R;

public class Util {
	private Util(){}
	public static byte[] serialize(Object object){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(bytes);
			out.writeObject(object);
			return bytes.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object deserialize(byte[] bytes){
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
			ObjectInputStream in = new ObjectInputStream(bin);
			return in.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	public static void errorAlert(Context context,  String message){
		new AlertDialog.Builder(context)
			.setTitle("FAIL!").setMessage(message)
			.setIcon(R.drawable.emo_im_wtf)
			.setPositiveButton("OK", null)
			.show();
	}
	
	public static File getSharedPrefsFile(Context context, String fileName){
		File dataDir = getDataDir(context);
		return new File(dataDir, fileName);
	}
	
	public static File getDataDir(Context context) {
		try {
			return new File(context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).applicationInfo.dataDir);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}

	}
}
