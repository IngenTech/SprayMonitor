package com.wrms.spraymonitor.app;

import java.text.SimpleDateFormat;

/**
 * Created by WRMS on 23-10-2015.
 */
public class Constents {


    public static final String APP_DIR = "SprayMonitor" ;// Constant to generate application directory in external memory
    public static final String APP_RAW_VID_DIR = "SprayMonitorRawVid" ;// Constant to generate directory for row video in external memory

    public static final String FUEL_DIR = "FuelDetail" ;

    public static final String CONTENT_DATAS = "ContentDatas" ;

    public static final String DATA = "data";//Constant key to sendRegistrationRequest form data from one intent to another intent
    public static final String SPRAY_MONITORING_ID = "id";//Constant key to sendRegistrationRequest form data from one intent to another intent
    public static final String SPRAY_PRODUCT_DATA = "spray_product_data";//Constant key to sendRegistrationRequest form data from one intent to another intent
    public static final String SPRAY_CROP_DATA = "spray_crop_data";//Constant key to sendRegistrationRequest form data from one intent to another intent
    public static final String SPRAY_TANK_FILLINF_DATA = "spray_tank_filling_data";//Constant key to sendRegistrationRequest form data from one intent to another intent

    public static final String IMAGE_DIR = "Image" ;//Constant to generate Image directory in external memory
    public static final String VIDEO_DIR = "Video" ;//Constant to generate Video directory in external memory

    public static final String CROP_DIR = "Crop" ;//Constant to generate Crop directory in external memory and image name for database
    public static final String FARMER_WITH_PRODUCT_DIR = "FarmerWithProduct" ;//Constant to generate farmer with product image directory in external memory and image name for database

    public static final String MACHINE_IMAGE_DIR = "MachineImage" ;//Constant to generate farmer with product image directory in external memory and image name for database

    public static final String FUEL_IMAGE_DIR = "MachineStartStop" ;//Constant to generate farmer with product image directory in external memory and image name for database


    public static final String CROP_AFTER_SPRAY_DIR = "CropAfterSpray" ;//Constant to generate crop after spray image directory in external memory and image name for database
    public static final String MIXTURE_PREPARATION = "MixturePreparation";//Constant to generate mixture preparation video directory in external memory and video name for database
    public static final String TANK_FILLING_VIDEO = "TankFillingVideo";//Constant to generate tank filling video directory in external memory and video name for database
    public static final String SPRAY_VIDEO = "SprayVideo";//Constant to generate spray video directory in external memory and video name for database

    public static final int IMAGE_HIEGHT = 400;
    public static final int IMAGE_WIDTH = 400;

    public static final String FARMER_DATA = "farmer_data";

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
