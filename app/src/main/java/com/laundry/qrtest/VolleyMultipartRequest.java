package com.laundry.qrtest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();

    private final Response.Listener<NetworkResponse> mListener;
    private final Map<String, String> mHeaders;
    private final Map<String, String> mParams;
    private final Map<String, DataPart> mByteData;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mHeaders = null;
        this.mParams = null;
        this.mByteData = null;
    }

    public VolleyMultipartRequest(int method, String url,
                                  Map<String, String> params,
                                  Map<String, DataPart> byteData,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mParams = params;
        this.mByteData = byteData;
        this.mHeaders = null;
    }

    @Override
    public Map<String, String> getHeaders() {
        try {
            return mHeaders != null ? mHeaders : super.getHeaders();
        } catch (AuthFailureError e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Menambahkan parameter teks
            if (mParams != null && mParams.size() > 0) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    buildTextPart(dos, entry.getKey(), entry.getValue());
                }
            }

            // Menambahkan file
            if (mByteData != null && mByteData.size() > 0) {
                for (Map.Entry<String, DataPart> entry : mByteData.entrySet()) {
                    buildDataPart(dos, entry.getValue(), entry.getKey());
                }
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            return bos.toByteArray();
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return null;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    private void buildTextPart(DataOutputStream dos, String parameterName, String parameterValue) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(parameterValue + lineEnd);
    }

    private void buildDataPart(DataOutputStream dos, DataPart dataFile, String inputName) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
            dos.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
        }
        dos.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            dos.write(buffer, 0, bytesRead);
        }

        dos.writeBytes(lineEnd);
    }

    protected abstract Map<String, DataPart> getByteData();

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String fileName, byte[] content) {
            this.fileName = fileName;
            this.content = content;
            this.type = "application/octet-stream";
        }

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}
