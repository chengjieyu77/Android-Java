package com.example.viewflickerbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadsStatus {IDLE,PROCESSING,NOT_INITIALIZED,FILED_OR_EMPTY,OK}
public class GetRawData extends AsyncTask<String,Void,String> {
    private static final String TAG = "GetRawData";
    private DownloadsStatus mDownloadStatus;
    private final OnDownloadComplete mCallBack;


    public GetRawData(OnDownloadComplete mCallBack) {
       this.mDownloadStatus = DownloadsStatus.IDLE;
       this.mCallBack = mCallBack;
    }

    interface OnDownloadComplete{
        void onDownloadComplete(String data, DownloadsStatus status);
    }
    void runInSameThread(String s){//when you call the execute method of an asyncTask,
                                    // it creates a new thread and run the doInBackground method
                                    //when that completes, the onPostMethod is called on the main thread
        Log.d(TAG, "runInSameThread: starts");
//        onPostExecute(doInBackground(s)); //without creating a new background thread
        if (mCallBack!=null){
            String result = doInBackground(s);
            mCallBack.onDownloadComplete(result,mDownloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: "+s);
        if (mCallBack != null){
            mCallBack.onDownloadComplete(s,mDownloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");

    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        if (strings == null){
            mDownloadStatus = DownloadsStatus.NOT_INITIALIZED;
            return null;
        }
        try{
            mDownloadStatus = DownloadsStatus.PROCESSING;
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");//default is get
            connection.connect();

            int response = connection.getResponseCode();
            Log.d(TAG,"doInBackground:The response code was:"+response);
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            String line;
            while(null != (line = reader.readLine())){
                result.append(line).append("\n");
            }
            mDownloadStatus = DownloadsStatus.OK;
            return result.toString();

//            int charsRead;
//            char[] inputBuffer = new char[500];
//            while(true){
//                charsRead = reader.read(inputBuffer);
//                if (charsRead<0){
//                    break;
//                }
//                if (charsRead>0){
//                    result.append(String.copyValueOf(inputBuffer,0,charsRead));
//                }
//            }
//            reader.close();
//            return result.toString();
        }catch (MalformedURLException e){
            Log.e(TAG,"doInBackground:Invalid URl"+e.getMessage());
        }catch(IOException e){
            Log.e(TAG,"doInBackground:IO Exception reading data: "+ e.getMessage());
        }catch(SecurityException e){
            Log.e(TAG,"doInBackground:Security Exception. Needs permission"+e.getMessage());
            e.printStackTrace();
        }finally {
            if (connection != null){
                connection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                }catch (IOException e){
                    Log.e(TAG,"doInBackground:error closing stream"+e.getMessage());
                }
            }
        }
        mDownloadStatus = DownloadsStatus.FILED_OR_EMPTY;
        return null;
    }


}
