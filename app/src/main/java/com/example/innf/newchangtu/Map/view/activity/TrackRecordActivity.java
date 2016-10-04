package com.example.innf.newchangtu.Map.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.innf.newchangtu.Map.adapter.PositionAdapter;
import com.example.innf.newchangtu.Map.bean.Position;
import com.example.innf.newchangtu.Map.bean.Track;
import com.example.innf.newchangtu.Map.model.PositionLab;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TrackRecordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "TrackRecordActivity";
    /*Track Record 数据传递键值*/
    private static final String EXTRA_TRACK = "com.example.innf.changtu.view.activity.track";
    private static final String KEY_BITMAP = "com.example.innf.changtu.view.activity.bitmap";

    private RecyclerView mTrackRecordRecyclerView;
    private PositionAdapter mPositionAdapter;
    private TextView mDetailTrackTextView;
    private TextView mTransportationTextView;
    private TextView mTrackDateTextView;
    private TextView mTrackTimeTextView;
    private TextView mTrackTakeTimeTextView;
    private TextView mTrackDistanceTextView;
    private TextView mRemarkTextView;
    private FloatingActionButton mShowPhotoFAB;
    private Track mTrack;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_record);

        mTrack = (Track) getIntent().getSerializableExtra(EXTRA_TRACK);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mTrackRecordRecyclerView = (RecyclerView) findViewById(R.id.track_recycler_view);
        mTrackRecordRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDetailTrackTextView = (TextView) findViewById(R.id.detail_track_text_view);
        mTransportationTextView = (TextView) findViewById(R.id.ride_type_text_view);
        mTrackDateTextView = (TextView) findViewById(R.id.track_date_text_view);
        mTrackTimeTextView = (TextView) findViewById(R.id.track_time_text_view);
        mTrackTakeTimeTextView = (TextView) findViewById(R.id.track_take_time_text_view);
        mTrackDistanceTextView = (TextView) findViewById(R.id.track_distance_text_view);
        mRemarkTextView = (TextView) findViewById(R.id.remark_text_view);
        mShowPhotoFAB = (FloatingActionButton) findViewById(R.id.show_photo_floating_action_button);

        mShowPhotoFAB.setOnClickListener(this);

        if (savedInstanceState != null) {
            bitmap = savedInstanceState.getParcelable(KEY_BITMAP);
        }

        updateUI();
    }


    private void updateUI() {
        PositionLab positionLab = PositionLab.get(this);
        List<Position> positionList = positionLab.getPositionList();

        mPositionAdapter = new PositionAdapter(positionList);
        mTrackRecordRecyclerView.setAdapter(mPositionAdapter);

        mDetailTrackTextView.setText(mTrack.getDetailTrack());
        mTransportationTextView.setText(mTrack.getTransportation());
        mTrackDateTextView.setText(mTrack.getTrackDate());
        mTrackTimeTextView.setText(mTrack.getTrackTime());
        mTrackTakeTimeTextView.setText(mTrack.getTakeTime());
        mTrackDistanceTextView.setText(mTrack.getDistance());
        mRemarkTextView.setText(mTrack.getRemark());
    }

    public static Intent newIntent(Context context, Track track) {
        Intent intent = new Intent(context, TrackRecordActivity.class);
        intent.putExtra(EXTRA_TRACK, track);
        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.show_photo_floating_action_button:
                final ImageView imageView = new ImageView(this);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("正在加载...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                if(null == bitmap){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            String url = mTrack.getPhoto().getUrl();
                            InputStream is = null;
                            try {
                                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                                conn.setConnectTimeout(5000);
                                conn.connect();
                                is = conn.getInputStream();
                                bitmap = BitmapFactory.decodeStream(is);
//                               bitmap = PictureUtils.getScaleBitmap(is, TrackRecordActivity.this);
                                is.close();
//                               showToast(bitmap == null ? "为空" : "不为空" );
                                imageView.setImageBitmap(bitmap);
                                progressDialog.cancel();
                                AlertDialog.Builder builder = new AlertDialog.Builder(TrackRecordActivity.this);
                                builder.setView(imageView)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }
                    }).start();
                } else {
//                   showToast(bitmap == null ? "为空" : "不为空" );
                    progressDialog.cancel();

                    imageView.setImageBitmap(bitmap);
                    AlertDialog.Builder builder = new AlertDialog.Builder(TrackRecordActivity.this);
                    builder.setView(imageView)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_BITMAP, bitmap);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(KEY_BITMAP, bitmap);
    }
}
