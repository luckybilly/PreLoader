package com.billy.preloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.android.preloader.PreLoader;
import com.billy.android.preloader.interfaces.GroupedDataListener;

/**
 * pre-load data before launch the activity
 *
 * @author billy.qi
 */
public class PreLoadGroupBeforeLaunchActivity extends AppCompatActivity {

    TimeWatcher allTime;
    private TextView logTextView;
    private TextView readmeTextView;
    private int preLoaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //统计总耗时
        allTime = TimeWatcher.obtainAndStart("total");
        //开始布局初始化的计时
        TimeWatcher timeWatcher = TimeWatcher.obtainAndStart("init layout");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_load);
        setTitle(R.string.pre_loader_group_before_page_title);
        //模拟复杂页面布局初始化的耗时
        try {
            Thread.sleep(200);
        } catch(Exception e) {
            e.printStackTrace();
        }
        int option = getIntent().getIntExtra("option", 0);
        readmeTextView = (TextView)findViewById(R.id.readme);
        readmeTextView.setText(R.string.pre_loader_group_before_open_page_readme);
        logTextView = (TextView)findViewById(R.id.log);

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allTime.start();
                readmeTextView.setText(R.string.pre_loader_refresh);
                logTextView.setText("");
                if (preLoaderId > 0) {
                    PreLoader.refresh(preLoaderId);
                } else {
                    Toast.makeText(PreLoadGroupBeforeLaunchActivity.this, R.string.error_no_pre_loader_id, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //布局初始化计时结束
        String s = timeWatcher.stopAndPrint();
        logTextView.append(s + "\n");
        //显示预加载的数据
        preLoaderId = getIntent().getIntExtra("preLoaderId", -1);
        if (preLoaderId > 0) {
            PreLoader.listenData(preLoaderId
                    , new DataHolder1()
                    , new DataHolder2()
            );
//            PreLoader.listenData(preLoaderId, new DataHolder1());
//            PreLoader.listenData(preLoaderId, new DataHolder2());
        }
    }

    class DataHolder1 implements GroupedDataListener<String> {
        @Override
        public void onDataArrived(String data) {
            //总耗时结束
            String s = allTime.stopAndPrint();
            logTextView.append(data + "\n" + s + "\n");
        }

        @Override
        public String keyInGroup() {
            return "loader1";
        }
    }
    class DataHolder2 implements GroupedDataListener<String> {
        @Override
        public void onDataArrived(String data) {
            //总耗时结束
            String s = allTime.stopAndPrint();
            logTextView.append(data + "\n" + s + "\n");
        }

        @Override
        public String keyInGroup() {
            return "loader2";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preLoaderId > 0) {
            PreLoader.destroy(preLoaderId);
        }
    }
}
