package com.wrms.spraymonitor.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.VideoData;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by WRMS on 18-05-2016.
 */
public class MultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();

    private static final String FILE_PART_NAME = "video_file";
    private static final String STRING_PART_NAME = "text";

    private final Response.Listener<String> mListener;
    private final File mFilePart;
    private final VideoData mVideoData;
    private final Credential mCredential;
    private final String tankFillingCount;

    public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, File file, VideoData videoData, Credential credential,String tankFillingCountL)
    {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mFilePart = file;
        mVideoData = videoData;
        mCredential = credential;
        tankFillingCount = tankFillingCountL;
        buildMultipartEntity();
    }

    private void buildMultipartEntity()
    {
        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
        try
        {
//            entity.addPart(STRING_PART_NAME, new StringBody(mVideoData));

            entity.addPart("user_id", new StringBody(mCredential.getUserId()));
            entity.addPart("experiment_id", new StringBody(mVideoData.getFormId()));
            entity.addPart("tankFillingNumber", new StringBody(tankFillingCount));
            entity.addPart("latitude", new StringBody(mVideoData.getLat()));
            entity.addPart("longitude", new StringBody( mVideoData.getLon()));
            entity.addPart("imei", new StringBody(mCredential.getImei()));
            entity.addPart("device_date", new StringBody(mVideoData.getDateTime()));
            entity.addPart("mcc", new StringBody(mVideoData.getMcc()));
            entity.addPart("mnc", new StringBody(mVideoData.getMnc()));
            entity.addPart("lac_id", new StringBody(mVideoData.getLacId()));
            entity.addPart("cell_id", new StringBody( mVideoData.getCellId()));
        }
        catch (UnsupportedEncodingException e)
        {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType()
    {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            entity.writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response){

        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            System.out.println("video response json : "+json);
            return Response.success(json,getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }

       /* Map<String, String> responseHeaders = response.headers;
        if (response.statusCode == 401) {
        // Here we are, we got a 401 response and we want to do something with some header field; in this example we return the "Content-Length" field of the header as a succesfully response to the Response.Listener<String>
        Response<String> result = Response.success(responseHeaders.get("Content-Length"), HttpHeaderParser.parseCacheHeaders(response));
        return result;
    }*/
//        return super.parseNetworkResponse(response);

//        return Response.success("Uploaded", getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response)
    {
        mListener.onResponse(response);
    }
}
