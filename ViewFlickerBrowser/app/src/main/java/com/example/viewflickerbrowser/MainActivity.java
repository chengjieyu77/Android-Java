package com.example.viewflickerbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewflickerbrowser.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable,
RecyclerItemClickListener.OnRecyclerClickListener
{
    private static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private FlickrRecycleViewAdapter mFlickrRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate:starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar(false);

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAnchorView(R.id.fab)
//                        .setAction("Action", null).show();
//            }
//        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView,this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFlickrRecycleViewAdapter = new FlickrRecycleViewAdapter(this,new ArrayList<Photo>());
        recyclerView.setAdapter(mFlickrRecycleViewAdapter);
        Log.d(TAG,"onCreate:ends");

//        GetRawData getRawData = new GetRawData(this);
//        getRawData.execute("https://www.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(FLICKR_QUERY,"");
        if (queryResult.length()>0){
            GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this,"https://www.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1","en-us",true);
            //getFlickrJsonData.executeOnSameThread("android");
            getFlickrJsonData.execute(queryResult);
        }

        Log.d(TAG, "onResume: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search){
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public void onDataAvailable(List<Photo> data, DownloadsStatus status){
        Log.d(TAG, "onDataAvailable: starts");
        if (status == DownloadsStatus.OK){
            mFlickrRecycleViewAdapter.loadNewData(data);
            Log.d(TAG, "onDataAvailable: data is"+data);
        }else{
            Log.e(TAG, "onDataAvailable: failed with status"+status);
        }
        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
        Toast.makeText(MainActivity.this,"Normal tap at position "+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: starts");
//        Toast.makeText(MainActivity.this,"Long tap at position "+position,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER,mFlickrRecycleViewAdapter.getPhoto(position));//the object has to be serializable
        //if an object is serializable it can be stored and retrieved, so a byte stream can be saved to disk or held in memory.
        startActivity(intent);
    }
}