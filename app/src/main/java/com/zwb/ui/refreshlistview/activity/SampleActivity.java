package com.zwb.ui.refreshlistview.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zwb.ui.refreshlistview.R;
import com.zwb.ui.refreshlistview.model.Contact;
import com.zwb.ui.refreshlistview.ui.CustomSwipeRefreshLayout;
import com.zwb.ui.refreshlistview.ui.OnMoreListener;
import com.zwb.ui.refreshlistview.ui.RefreshListView;
import com.zwb.ui.refreshlistview.ui.SampleAdapter;

import java.util.ArrayList;
import java.util.List;


public class SampleActivity extends ActionBarActivity implements CustomSwipeRefreshLayout.OnRefreshListener, OnMoreListener {
    private List<Contact> contactList;
    private SampleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<Contact>();
        for (int i = 0; i < 10; i++) {
            Contact contact = new Contact();
            contact.setName("你好");
            contactList.add(contact);
        }
        RefreshListView rlvContent = (RefreshListView) findViewById(R.id.rlv_content);
        View progressView = LayoutInflater.from(this).inflate(R.layout.widget_progress, null);
        rlvContent.addFooterView(progressView);
        adapter = new SampleAdapter(this, contactList);
        rlvContent.setAdapter(adapter);
        rlvContent.setRefreshListener(this);
        rlvContent.setupMoreListener(this, 0);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        Contact contact = new Contact();
        contact.setName("他好");
        contactList.add(contact);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        Contact contact = new Contact();
        contact.setName("我好");
        contactList.add(contact);
        adapter.notifyDataSetChanged();
    }
}