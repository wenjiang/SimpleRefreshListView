package com.zwb.ui.refreshlistview.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zwb.ui.refreshlistview.R;

/**
 * Created by pc on 2015/4/7.
 */
public class SampleActivity extends Activity {
    private Button btnPullToRefresh;
    private Button btnAddMore;
    private Button btnCustomBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPullToRefresh = (Button) findViewById(R.id.btn_pull_to_refresh);
        btnAddMore = (Button) findViewById(R.id.btn_add_more);
        btnCustomBottom = (Button) findViewById(R.id.btn_bottom);

        btnPullToRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SampleActivity.this, SamplePullToRefreshActivity.class);
                startActivity(intent);
            }
        });

        btnAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SampleActivity.this, SampleAddMoreActivity.class);
                startActivity(intent);
            }
        });
    }
}
