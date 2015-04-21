package com.zwb.ui.refreshlistview.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.zwb.ui.refreshlistview.R;
import com.zwb.ui.refreshlistview.model.Contact;
import com.zwb.ui.refreshlistview.ui.CustomSwipeRefreshLayout;
import com.zwb.ui.refreshlistview.ui.RefreshListView;
import com.zwb.ui.refreshlistview.ui.SampleAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pc on 2015/4/7.
 */
public class SampleCustomBottomActivity extends ActionBarActivity implements CustomSwipeRefreshLayout.OnRefreshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh);

        RefreshListView rlvContent = (RefreshListView) findViewById(R.id.rlv_content);
        List<Contact> contactList = new ArrayList<Contact>();
        Contact contact = new Contact();
        contact.setName("你好");
        contactList.add(contact);
        View view = getLayoutInflater().inflate(R.layout.view_bottom, null);
        rlvContent.addFooterView(view);
        SampleAdapter adapter = new SampleAdapter(this, contactList);
        rlvContent.setAdapter(adapter);
        rlvContent.setRefreshListener(this);
    }

    @Override
    public void onRefresh() {

    }
}
