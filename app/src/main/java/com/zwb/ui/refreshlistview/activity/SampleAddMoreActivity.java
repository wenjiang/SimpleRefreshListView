package com.zwb.ui.refreshlistview.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.zwb.ui.refreshlistview.R;
import com.zwb.ui.refreshlistview.model.Contact;
import com.zwb.ui.refreshlistview.ui.CustomSwipeRefreshLayout;
import com.zwb.ui.refreshlistview.ui.OnMoreListener;
import com.zwb.ui.refreshlistview.ui.RefreshListView;
import com.zwb.ui.refreshlistview.ui.SampleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2015/4/7.
 */
public class SampleAddMoreActivity extends ActionBarActivity implements OnMoreListener, CustomSwipeRefreshLayout.OnRefreshListener {
    private List<Contact> contactList;
    private RefreshListView rlvContent;
    private SampleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh);

        contactList = new ArrayList<Contact>();
        Contact contact = new Contact();
        contact.setName("我好");
        contactList.add(contact);
        rlvContent = (RefreshListView) findViewById(R.id.rlv_content);
        rlvContent.setupMoreListener(this, 0);
        adapter = new SampleAdapter(this, contactList);
        rlvContent.setAdapter(adapter);
        rlvContent.setRefreshListener(this);
    }


    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        Contact contact = new Contact();
        contact.setName("我好");
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
