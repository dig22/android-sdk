/** 
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 * 
 */
package com.kinvey.sample.oracledlc;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.api.client.http.HttpTransport;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kinvey.android.Client;
import com.kinvey.sample.oracledlc.account.LoginActivity;

/**
 * @author edwardf
 * @since 2.0
 */
public class OracleDLC extends SherlockFragmentActivity implements AdapterView.OnItemClickListener {


    private static final Level LOGGING_LEVEL = Level.FINEST;
    public static final String TAG = "OracleDLC";

    public static final String collectionName = "person";

    private ListView mList;
    private FeatureAdapter mAdapter;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oracledlc);
        bindViews();

        Client myClient = ((OracleDLCApplication)getApplication()).getClient();
        if (!myClient.user().isUserLoggedIn()) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }

        // run the following comamnd to turn on verbose logging:
        //
        // adb shell setprop log.tag.HttpTransport DEBUG
        //
        Logger.getLogger(HttpTransport.class.getName()).setLevel(LOGGING_LEVEL);

    }

    private void bindViews(){
        List<Loader.Feature> featureList = Loader.getFeatureList();

        mList = (ListView) findViewById(R.id.ks_list);
        mAdapter = new FeatureAdapter(this, featureList,
                (LayoutInflater) getSystemService(
                        Activity.LAYOUT_INFLATER_SERVICE));
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent feature = new Intent(this, mAdapter.getItem(position).getActivity());
        startActivity(feature);
    }

    // Using ActionbarSherlock to handle options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // depending on which option is tapped, act accordingly
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_login:
                login();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void login(){
       Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);

    }


    /**
     *
     * This Adapter is used to maintain data and push_legacy individual row views to
     * the ListView object, note it constructs the Views used by each row and
     * uses the ViewHolder pattern.
     *
     */
    private class FeatureAdapter extends ArrayAdapter<Loader.Feature> {

        private LayoutInflater mInflater;

        public FeatureAdapter(Context context, List<Loader.Feature> objects,
                                LayoutInflater inf) {
            // NOTE: I pass an arbitrary textViewResourceID to the super
            // constructor-- Below I override
            // getView(...), which causes the underlying adapter to ignore this
            // field anyways, it is just needed in the constructor.
            super(context, R.id.row_feature_name, objects);
            this.mInflater = inf;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureViewHolder holder = null;

            TextView name = null;
            TextView blurb = null;

            Loader.Feature rowData = getItem(position);

            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.row_feature_list, null);
                holder = new FeatureViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder = (FeatureViewHolder) convertView.getTag();

            name = holder.getName();
            name.setText(rowData.getName());
            blurb = holder.getBlurb();
            blurb.setText(rowData.getBlurb());

            return convertView;
        }

        /**
         * This pattern is used as an optimization for Android ListViews.
         *
         * Since every row uses the same layout, the View object itself can be
         * recycled, only the data/content of the row has to be updated.
         *
         * This allows for Android to only inflate enough Row Views to fit on
         * screen, and then they are recycled. This allows us to avoid creating
         * a new view for every single row, which can have a negative effect on
         * performance (especially with large lists on large screen devices).
         *
         */
        private class FeatureViewHolder {
            private View mRow;

            private TextView tvName = null;
            private TextView tvBlurb = null;

            public FeatureViewHolder(View row) {
                mRow = row;
            }

            public TextView getName() {
                if (null == tvName) {
                    tvName = (TextView) mRow.findViewById(R.id.row_feature_name);
                }
                return tvName;
            }

            public TextView getBlurb() {
                if (null == tvBlurb) {
                    tvBlurb = (TextView) mRow.findViewById(R.id.row_feature_blurb);
                }
                return tvBlurb;
            }



        }
    }



}
