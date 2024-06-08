package com.example.viewflickerbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetFlickrJsonData extends AsyncTask<String,Void,List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";
    private List<Photo> mPhotoList = null;
    private String mBaseURL;
    private String mLanguage;
    private boolean mMatchAll;
    private final OnDataAvailable mCallBack;
    private boolean runningOnSameThread = false;

    @Override
    public void onDownloadComplete(String data, DownloadsStatus status) {
        Log.d(TAG, "onDownloadComplete: starts = " + status);
        if (status == DownloadsStatus.OK){
            mPhotoList = new ArrayList<>();
            try{
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");
                for(int i=0;i<itemsArray.length();i++){
                    JSONObject jsonPhoto = itemsArray.getJSONObject((i));
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");
                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");//image field of the object

                    String link = photoUrl.replaceFirst("_m.","_b.");//provide the URl of the full size picture

                    Photo photoObject = new Photo(title,author,authorId,photoUrl,tags,link);
                    mPhotoList.add(photoObject);
                    Log.d(TAG, "onDownloadComplete: "+photoObject.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error processing json data"+e.getMessage() );
                status = DownloadsStatus.FILED_OR_EMPTY;
                //throw new RuntimeException(e);
            }
        }
        if (runningOnSameThread && mCallBack != null){
            mCallBack.onDataAvailable(mPhotoList,status);
        }
        Log.d(TAG, "onDownloadComplete: ends");
    }

    interface OnDataAvailable{
        void onDataAvailable(List<Photo> data,DownloadsStatus status);
    }

    public GetFlickrJsonData(OnDataAvailable callBack,String baseURL, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData: called");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        mCallBack = callBack;
    }

    void executeOnSameThread(String searchCriteria){
        Log.d(TAG, "executeOnSameThread: starts");
        runningOnSameThread = true;
        String destinationUri = createUri(searchCriteria,mLanguage,mMatchAll);
        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");
        if (mCallBack != null){
            mCallBack.onDataAvailable(mPhotoList,DownloadsStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUrl = createUri(strings[0],mLanguage,mMatchAll);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUrl);//without creating a new thread
        Log.d(TAG, "doInBackground: ends");
        return mPhotoList;
    }

    private String createUri(String searchCriteria, String language, boolean matchAll){
        Log.d(TAG, "createUri: starts");
        Uri uri = Uri.parse(mBaseURL);
        Uri.Builder builder = uri.buildUpon();//to build the parameters on top of the URL
        builder = builder.appendQueryParameter("tags",searchCriteria);//to add each parameter to the uri
        builder = builder.appendQueryParameter("tagmode",matchAll ? "All":"ANY");
        builder = builder.appendQueryParameter("lang",language);
        builder = builder.appendQueryParameter("format","json");
        builder = builder.appendQueryParameter("nojsoncallback","1");
        uri = builder.build();
        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags",searchCriteria)
                .appendQueryParameter("tagmode",matchAll ? "All":"ANY")
                .appendQueryParameter("lang",language)
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback","1")
                .build().toString();
    }
}
