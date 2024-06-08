package com.example.viewflickerbrowser;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.BlockingDeque;

class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    interface OnRecyclerClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector; //initialized in the constructor

    //need a context for the gesture detector to work, also a reference to the recycle view that we detecting the taps on
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView,OnRecyclerClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if (childView != null && mListener != null){
                    Log.d(TAG, "onSingleTapUp: calling listener onItemClick");
                    mListener.onItemClick(childView,recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                Log.d(TAG, "onLongPress: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if (childView != null && mListener != null){
                    Log.d(TAG, "onLongPress: calling listener onItemLongClick");
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if(mGestureDetector != null){
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent(): returned"+result);
            return result;
        }else{
            Log.d(TAG, "onInterceptTouchEvent(): returned false");
            return false;
        }
    }
}
