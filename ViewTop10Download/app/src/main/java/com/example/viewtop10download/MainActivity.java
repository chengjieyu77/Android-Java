package com.example.viewtop10download;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ListView listApps;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        listApps = (ListView) findViewById(R.id.xmlListView);

        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");

    }

    private class DownloadData extends AsyncTask<String,Void,String>{
        private static final String TAG = "DownloadData";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null){
                Log.e(TAG,"doInBackground:Error downing...");
            }
            return rssFeed;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG,"onPostExecute:"+s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
                    MainActivity.this,R.layout.list_item,parseApplications.getApplications()
            );
            listApps.setAdapter(arrayAdapter);
        }

        private String downloadXML(String uriPath){
            StringBuilder xmlResult = new StringBuilder();
            try{
                URL url = new URL(uriPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG,"downloadXML:The response code was:"+response);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                int charsRead;
                char[] inputBuffer = new char[500];
                while(true){
                    charsRead = reader.read(inputBuffer);
                    if(charsRead<0){
                        break;
                    }
                    if (charsRead>0){
                        xmlResult.append(String.copyValueOf(inputBuffer,0,charsRead));
                    }
                }
                reader.close();
                return xmlResult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG,"downloadXML:Invalid URl"+e.getMessage());
            }catch(IOException e){
                Log.e(TAG,"downloadXML:IO Exception reading data: "+ e.getMessage());
            }catch(SecurityException e){
                Log.e(TAG,"downloadXML:Security Exception. Needs permission"+e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
}