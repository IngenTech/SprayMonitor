package com.wrms.spraymonitor.utils;

import android.os.Environment;

import com.wrms.spraymonitor.app.Constents;

import java.io.File;
import java.util.HashMap;

public class FolderManager {

	public static final String HIGH_RESOLUTION_DIRECTORY = "High";
	public static final String LOW_RESOLUTION_DIRECTORY = "Low";

	public static final String IMG_EXT = ".jpg";
	public static final String VID_EXT = ".mp4";

	
	public static HashMap<String,String> getImgDir(String farmId,String ImageType,String imgName){

		HashMap<String,String> myDirectories = new HashMap<>();

		String highImagePath = Environment.getExternalStorageDirectory() + File.separator+ Constents.APP_DIR+File.separator
				+ farmId + File.separator+Constents.IMAGE_DIR+ File.separator+ImageType+File.separator+HIGH_RESOLUTION_DIRECTORY;
		File imagefile = new File(highImagePath);
		if(!imagefile.exists()){
			imagefile.mkdirs();
		}
		if (imgName != null) {
			highImagePath = highImagePath+File.separator+imgName+IMG_EXT;
		}
		myDirectories.put(HIGH_RESOLUTION_DIRECTORY,highImagePath);

		String lowImagePath = Environment.getExternalStorageDirectory() + File.separator+Constents.APP_DIR+File.separator
				+ farmId + File.separator+Constents.IMAGE_DIR+ File.separator+ImageType+File.separator+LOW_RESOLUTION_DIRECTORY;
		File lowimagefile = new File(lowImagePath);
		if(!lowimagefile.exists()){
			lowimagefile.mkdirs();
		}
		if (imgName != null) {
			lowImagePath = lowImagePath+File.separator+imgName+IMG_EXT;
		}
		myDirectories.put(LOW_RESOLUTION_DIRECTORY,lowImagePath);

		return myDirectories;
	}

	public static String getFarmDir(String farmId){

		String farmPath = Environment.getExternalStorageDirectory() + File.separator+ Constents.APP_DIR+File.separator
				+ farmId;
		File farmDir = new File(farmPath);
		if(!farmDir.exists()){
			farmDir.mkdirs();
		}
		return farmDir.getAbsolutePath();
	}


	public static String getFuelDir(){

		String fuelPath = Environment.getExternalStorageDirectory() + File.separator+ Constents.APP_DIR+File.separator
				+ Constents.FUEL_DIR;
		File fuelDir = new File(fuelPath);
		if(!fuelDir.exists()){
			fuelDir.mkdirs();
		}
		return fuelDir.getAbsolutePath();
	}


	public static HashMap<String,String> getVidDir(String farmId,String videoType,String vidName){

		HashMap<String,String> myDirectories = new HashMap<>();

		String highVideoPath = Environment.getExternalStorageDirectory() + File.separator+Constents.APP_RAW_VID_DIR+File.separator
				+ farmId + File.separator+Constents.VIDEO_DIR+ File.separator+videoType+File.separator+HIGH_RESOLUTION_DIRECTORY;
		File videofile = new File(highVideoPath);
		if(!videofile.exists()){
			videofile.mkdirs();
		}
		if (vidName != null) {
			highVideoPath = highVideoPath+File.separator+vidName+VID_EXT;
		}
		myDirectories.put(HIGH_RESOLUTION_DIRECTORY,highVideoPath);

		String lowVideoPath = Environment.getExternalStorageDirectory() + File.separator+Constents.APP_DIR+File.separator
				+ farmId + File.separator+Constents.VIDEO_DIR+ File.separator+videoType+File.separator+LOW_RESOLUTION_DIRECTORY;
		File lowVideoFile = new File(lowVideoPath);
		if(!lowVideoFile.exists()){
			lowVideoFile.mkdirs();
		}
		if (vidName != null) {
			lowVideoPath = lowVideoPath+File.separator+vidName+VID_EXT;
		}
		myDirectories.put(LOW_RESOLUTION_DIRECTORY,lowVideoPath);

		return myDirectories;
	}

}
