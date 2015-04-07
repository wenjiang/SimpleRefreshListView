package com.zwb.ui.refreshlistview.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zwb.ui.refreshlistview.R;
import com.zwb.ui.refreshlistview.model.Contact;

import java.util.List;

/**
 * Created by pc on 2015/4/7.
 */
public class SampleAdapter extends BaseAdapter {
    private List<Contact> contactList;
    private Context context;

    public SampleAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        }

        TextView tvName = (TextView) CommonViewHolder.get(convertView, R.id.tv_name);
        tvName.setText(contactList.get(position).getName());
        return convertView;
    }
}
