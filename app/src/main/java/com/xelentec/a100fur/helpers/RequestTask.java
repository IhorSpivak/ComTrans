package com.xelentec.a100fur.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xelentec.a100fur.R;
import com.xelentec.a100fur.items.ResponseItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;




public class RequestTask extends AsyncTask<Void, Void, ResponseItem> {

    private static final String TAG = RequestTask.class.getSimpleName();
    private static final int SEND_TIMEOUT = 30000;
    Context context;
    JsonObject jsonObject;
    private boolean isNeedDialog = true;
    ProgressDialog progressDialog;
    public static final int HTTP_GET_REQUEST = 1;
    public static final int HTTP_POST_REQUEST = 2;
    public static final int IMAGE_UPLOAD = 3;
    public static final int HTTP_PUT_REQUEST = 4;
    public static final int HTTP_DELETE_REQUEST = 5;
    private OnRequestObtainedListener mRequestObtainedListener;
    private OnRequestErrorListener mRequestErrorListener;
    private HttpURLConnection postConnection = null;

    private String url;
    private String token;
    private int requestType;
    private boolean isConnected;


    public static class RequestTaskBuilder {
        Context context;
        JsonObject jsonObject;
        String url;
        String token;
        int requestType;



        private OnRequestObtainedListener mRequestObtainedListener;
        private OnRequestErrorListener mRequestErrorListener;

        public RequestTaskBuilder(Context context, String url, String token,
                                  JsonObject jsonObject, int requestType) {
            this.context = context;
            this.jsonObject = jsonObject;
            this.url = url;
            this.token = token;
            this.requestType = requestType;
        }

        public RequestTask buildRequestTask() {
            return new RequestTask(context,url,token,jsonObject,requestType,mRequestObtainedListener,mRequestErrorListener);
        }

        public RequestTaskBuilder errorListener(OnRequestErrorListener listener) {
            this.mRequestErrorListener = listener;
            return this;
        }

        public RequestTaskBuilder obtainListener(OnRequestObtainedListener listener) {
            this.mRequestObtainedListener = listener;
            return this;
        }

    }


    public RequestTask(Context context,String url, String token,
                       JsonObject jsonObject, int type,
                       OnRequestObtainedListener mRequestObtainedListener, OnRequestErrorListener mRequestErrorListener) {
        super();
        this.context = context;
        this.mRequestObtainedListener = mRequestObtainedListener;
        this.mRequestErrorListener = mRequestErrorListener;
        this.jsonObject = jsonObject;
        this.url = url;
        this.token = token;
        this.requestType = type;
    }

    public void setNeedDialog(boolean isNeedDialog) {
        this.isNeedDialog = isNeedDialog;
    }

    @Override
    protected void onPreExecute() {
        isConnected = Utility.isConnectingToInternet(context);
        if (isNeedDialog) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(context.getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        super.onPreExecute();
    }

    @Override
    protected ResponseItem doInBackground(Void... str) {

        if(isConnected) {

            BufferedReader reader = null;
            String response = null;
            InputStream inputStream = null;
            Integer responseCode = 0;

            Log.d("TAG", url);
            Log.d("TAG", jsonObject.toString());

            switch (requestType) {
                case HTTP_GET_REQUEST:
                    try {
                        URL requestUrl = new URL(url);
                        postConnection = (HttpURLConnection) requestUrl.openConnection();
                        postConnection.setDoInput(true);
                        postConnection.setUseCaches(false);
                        postConnection.setRequestProperty("Authorization",
                                token);
                        postConnection.setRequestMethod("GET");
                        postConnection.setConnectTimeout(SEND_TIMEOUT);
                        postConnection.setRequestProperty("Content-Type", "application/json");
                        postConnection.setRequestProperty("Accept", "application/json");
                        postConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
                        responseCode = postConnection.getResponseCode();
                        if (responseCode >= 400) {
                            inputStream = postConnection.getErrorStream();
                            Log.d("TAG", "getting error stream" + inputStream.available());
                        } else {
                            inputStream = postConnection.getInputStream();
                        }
                    } catch (SocketTimeoutException e) {
                        //    mContext.startActivity(new Intent(mContext, Connection_problemActivity.class));
                    } catch (java.io.IOException e) {
                        Log.e("TAG", "error", e);
                    }

                    break;
                case HTTP_POST_REQUEST:
                    try {
                        URL requestUrl = new URL(url);
                        postConnection = (HttpURLConnection) requestUrl.openConnection();
                        postConnection.setDoOutput(true);
                        postConnection.setDoInput(true);
                        if (token != null && !token.equals(""))
                            postConnection.setRequestProperty("Authorization",
                                    token);
                        postConnection.setRequestMethod("POST");
                        postConnection.setConnectTimeout(SEND_TIMEOUT);
                      //  postConnection.setRequestProperty("Content-Type", "application/json");
                        postConnection.setRequestProperty("Connection","keep-alive");

                      //  postConnection.setRequestProperty("Accept", "application/json");
                        postConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));

                        Writer writer = new BufferedWriter
                                (new OutputStreamWriter(postConnection.getOutputStream(), "UTF-8"));

                        writer.write(new Gson().toJson(jsonObject));
                        writer.flush();
                        writer.close();

                        responseCode = postConnection.getResponseCode();
                        Log.d("TAG", responseCode.toString());
                        if (responseCode >= 400) {
                            inputStream = postConnection.getErrorStream();
                        } else {
                            Log.d("TAG", "getting input stream");
                            inputStream = postConnection.getInputStream();
                        }

                    } catch (SocketTimeoutException e) {
                        Log.e("TAG", "e", e);
                        //   mContext.startActivity(new Intent(mContext, Connection_problemActivity.class));
                    } catch (java.io.IOException e) {
                        Log.e("TAG", "error", e);
                    }
                    break;
                case HTTP_PUT_REQUEST:
                    try {
                        URL requestUrl = new URL(url);
                        postConnection = (HttpURLConnection) requestUrl.openConnection();
                        postConnection.setDoInput(true);
                        postConnection.setUseCaches(false);
                        postConnection.setRequestProperty("Authorization",
                                token);
                        postConnection.setRequestMethod("PUT");
                        postConnection.setConnectTimeout(SEND_TIMEOUT);
                        postConnection.setRequestProperty("Content-Type", "application/json");
                        postConnection.setRequestProperty("Accept", "application/json");
                        postConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
                        Writer writer = new BufferedWriter
                                (new OutputStreamWriter(postConnection.getOutputStream(), "UTF-8"));
                        writer.write(jsonObject.toString());
                        writer.close();
                        responseCode = postConnection.getResponseCode();
                        if (responseCode >= 400) {
                            inputStream = postConnection.getErrorStream();
                            Log.d("TAG", "getting error stream" + inputStream.available());
                        } else {
                            inputStream = postConnection.getInputStream();
                        }
                    } catch (SocketTimeoutException e) {
                        //    mContext.startActivity(new Intent(mContext, Connection_problemActivity.class));
                    } catch (java.io.IOException e) {
                        Log.e("TAG", "error", e);
                    }

                    break;
                case HTTP_DELETE_REQUEST:
                    try {
                        URL requestUrl = new URL(url);
                        postConnection = (HttpURLConnection) requestUrl.openConnection();
                        postConnection.setDoInput(true);
                        postConnection.setUseCaches(false);
                        postConnection.setRequestProperty("Authorization",
                                token);
                        postConnection.setRequestMethod("DELETE");
                        postConnection.setConnectTimeout(SEND_TIMEOUT);
                        postConnection.setRequestProperty("Content-Type", "application/json");
                        postConnection.setRequestProperty("Accept", "application/json");
                        postConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
                        responseCode = postConnection.getResponseCode();
                        if (responseCode >= 400) {
                            inputStream = postConnection.getErrorStream();
                            Log.d("TAG", "getting error stream" + inputStream.available());
                        } else {
                            inputStream = postConnection.getInputStream();
                        }
                    } catch (SocketTimeoutException e) {
                        //    mContext.startActivity(new Intent(mContext, Connection_problemActivity.class));
                    } catch (java.io.IOException e) {
                        Log.e("TAG", "error", e);
                    }

                    break;
                case IMAGE_UPLOAD:
                    try {
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "===" + System.currentTimeMillis() + "===";

                        String charset = "UTF-8";
                        OutputStream outputStream;
                        PrintWriter writer;

                        URL imageUrl = new URL(url);
                        postConnection = (HttpURLConnection) imageUrl.openConnection();
                        postConnection.setDoInput(true);
                        postConnection.setDoOutput(true);
                        postConnection.setUseCaches(false);
                        postConnection.setInstanceFollowRedirects(true);
                        postConnection.setConnectTimeout(150000);
                        postConnection.setReadTimeout(150000);
                        postConnection.setRequestMethod("POST");
                        postConnection.setRequestProperty("Connection", "Keep-Alive");
                        postConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                        postConnection.setRequestProperty("Accept", "application/json");
                        postConnection.setChunkedStreamingMode(-1);

                        outputStream = new DataOutputStream(postConnection.getOutputStream());
                        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                                true);

                        JsonArray imagesArray = jsonObject.getAsJsonArray("images");

                        for (int i = 0; i < imagesArray.size(); i++) {
                            File file = new File(imagesArray.get(i).getAsString());
                            String fileName = file.getName();
                            writer.append(twoHyphens + boundary + lineEnd);
                            writer.append("Content-Disposition: form-data; name=\"" + "images" + "\"" + "; filename=\"" + fileName + "\"" + lineEnd);
                            writer.append(
                                    "Content-Type: "
                                            + URLConnection.guessContentTypeFromName(fileName))
                                    .append(lineEnd);
                            writer.append("Content-Transfer-Encoding: binary").append(lineEnd);
                            writer.append(lineEnd);
                            writer.flush();

                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] buffer = new byte[4096];
                            int bytesRead = -1;
                            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            outputStream.flush();
                            fileInputStream.close();

                            writer.append(lineEnd);
                            writer.flush();
                        }

                        writer.append(lineEnd).flush();
                        writer.append(twoHyphens + boundary + twoHyphens).append(lineEnd);
                        writer.close();


                        responseCode = postConnection.getResponseCode();
                        if (responseCode >= 400) {
                            inputStream = postConnection.getErrorStream();
                        } else {
                            inputStream = postConnection.getInputStream();
                        }
                    } catch (SocketTimeoutException e) {
                        //    mContext.startActivity(new Intent(mContext, Connection_problemActivity.class));
                    } catch (Exception e) {
                        Log.e("TAG", "error", e);
                    }

                    break;


            }


            try {
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return new ResponseItem();
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine).append("\n");

                }
                if (buffer.length() == 0) {
                    return new ResponseItem();
                }
                response = buffer.toString();

                Log.d("TAG", "response " + response);
            } catch (IOException e) {
                Log.e("TAG", "e", e);
            }

            if (postConnection != null) {
                postConnection.disconnect();
            }
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e("TAG", "Error closing stream", e);
            }


            return new ResponseItem(response, responseCode);
        }else {
            return null;
        }

    }

    @Override
    protected void onPostExecute(ResponseItem responseItem) {
        super.onPostExecute(responseItem);

        try{
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }catch (Exception ignored){}


        if(responseItem!=null) {

            if (isJSONValid(responseItem.getResponse())) {
                Gson gson = new Gson();
                    try {
                        JsonObject object = gson.fromJson(responseItem.getResponse(),JsonObject.class);

                        try{
                            if (!object.has("status")&&!object.get("status").getAsString().equals("error")){
                                mRequestErrorListener.onRequestError(object.get("message").getAsString());
                            }else {
                                mRequestObtainedListener.onRequestObtained(responseItem);
                            }
                        }catch (Exception e){
                            mRequestObtainedListener.onRequestObtained(responseItem);
                        }


                    } catch (Exception e) {
                        try{
                            JsonArray array = gson.fromJson(responseItem.getResponse(),JsonArray.class);
                            if(array.size()==0)
                                mRequestErrorListener.onRequestError(context.getString(R.string.something_went_wrong));
                            else
                            mRequestObtainedListener.onRequestObtained(responseItem);
                        }catch (Exception e1){
                            mRequestErrorListener.onRequestError(context.getString(R.string.something_went_wrong));
                        }
                    }




            } else {
                mRequestErrorListener.onRequestError(context.getString(R.string.something_went_wrong));
                Log.e(TAG, responseItem.getResponse() + "");
            }

        }else {
            mRequestErrorListener.onRequestError(context.getString(R.string.internet_not_exist));
        }

    }

    public interface OnRequestObtainedListener {
         void onRequestObtained(ResponseItem responseItem);
    }

    public interface OnRequestErrorListener {
         void onRequestError(String error);
    }

    public boolean isJSONValid(String test) {
        Gson gson = new Gson();
        try {
            gson.fromJson(test, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }


}
