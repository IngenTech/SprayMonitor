/*
 * Copyright (C) 2014 Yuya Tanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ypresto.androidtranscoder.format;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.util.ArrayList;

class Android720pFormatStrategy implements MediaFormatStrategy {
    private static final String TAG = "720pFormatStrategy";

    /*private static final int LONGER_LENGTH = 1280;
    private static final int SHORTER_LENGTH = 720;
    private static final int DEFAULT_BITRATE = 8000 * 1000;*/ // From Nexus 4 Camera in 720p

    /*private static final int LONGER_LENGTH = 320;
    private static final int SHORTER_LENGTH = 240;
    private static final int DEFAULT_BITRATE = 2000 * 1000;*/ // From Nexus 4 Camera in 720p


    /*private static final int LONGER_LENGTH = 176;
    private static final int SHORTER_LENGTH = 144;
    private static final int DEFAULT_BITRATE = 1000 * 1000;*/

    private static int LONGER_LENGTH = 192;
    private static int SHORTER_LENGTH = 128;
    private static int DEFAULT_BITRATE = 1000 * 1000;
    private final int mBitRate;

    public Android720pFormatStrategy() {
        mBitRate = DEFAULT_BITRATE;
    }

    public Android720pFormatStrategy(int bitRate) {
        mBitRate = bitRate;
    }

    @Override
    public MediaFormat createVideoOutputFormat(MediaFormat inputFormat) {
        int width = inputFormat.getInteger(MediaFormat.KEY_WIDTH);
        int height = inputFormat.getInteger(MediaFormat.KEY_HEIGHT);

        System.out.println("Input width : "+width+" Input Height : "+height);

        ArrayList<Integer> ratio;
        if(width>height){
            ratio = ratio(height,width);
        }else{
            ratio = ratio(width,height);
        }
        if(ratio.size()>=2) {
            myRecursiveMethod(ratio.get(0),ratio.get(1));
        }

        int longer, shorter, outWidth, outHeight;
        if (width >= height) {
            longer = width;
            shorter = height;
            outWidth = LONGER_LENGTH;
            outHeight = SHORTER_LENGTH;

        } else {
            shorter = width;
            longer = height;
            outWidth = SHORTER_LENGTH;
            outHeight = LONGER_LENGTH;
        }

        System.out.println("output width : "+outWidth+" output Height : "+outHeight);

        System.out.println("shorter : " + shorter);
        if (shorter <= SHORTER_LENGTH) {
            Log.d(TAG, "This video is less or equal to 720p, pass-through. (" + width + "x" + height + ")");
            return null;
        }
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", outWidth, outHeight);
        // From Nexus 4 Camera in 720p
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 3);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        return format;
    }

    @Override
    public MediaFormat createAudioOutputFormat(MediaFormat inputFormat) {
        return null;
    }

    /*
     * Java method to find GCD of two number using Euclid's method
     * @return GDC of two numbers in Java
     */
    private static int findGCD(int number1, int number2) {
        //base case
        if (number2 == 0) {
            return number1;
        }
        return findGCD(number2, number1 % number2);
    }

    private static ArrayList<Integer> ratio(int number1, int number2) {
        final int gcd = findGCD(number1, number2);
        System.out.println(number1 / gcd + " " + number2 / gcd);
        ArrayList<Integer> ratioArray = new ArrayList<>();
        ratioArray.add(number1 / gcd);
        ratioArray.add(number2 / gcd);
        return ratioArray;
    }

    public void myRecursiveMethod (int lowRatio , int highRatio)
    {
        SHORTER_LENGTH = lowRatio*16;
        LONGER_LENGTH = highRatio*16;
        if(LONGER_LENGTH<100 || SHORTER_LENGTH<100){
            myRecursiveMethod(lowRatio*2,highRatio*2);
        }
    }

}
