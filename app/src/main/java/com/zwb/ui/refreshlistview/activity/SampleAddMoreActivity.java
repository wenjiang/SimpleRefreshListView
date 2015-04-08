package com.zwb.ui.refreshlistview.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.zwb.ui.refreshlistview.R;
import com.zwb.ui.refreshlistview.model.Contact;
import com.zwb.ui.refreshlistview.ui.OnMoreListener;
import com.zwb.ui.refreshlistview.ui.RefreshListView;
import com.zwb.ui.refreshlistview.ui.SampleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2015/4/7.
 */
public class SampleAddMoreActivity extends ActionBarActivity implements OnMoreListener {
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
        adapter = new SampleAdapter(this, contactList);
        View progressView = LayoutInflater.from(this).inflate(R.layout.widget_progress, null);
        rlvContent.addProgressView(progressView);
        rlvContent.setAdapter(adapter);
        rlvContent.setupMoreListener(this, 0);
    }


    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        Contact contact = new Contact();
        contact.setName("我好");
        contactList.add(contact);
        adapter.notifyDataSetChanged();
    }
}
