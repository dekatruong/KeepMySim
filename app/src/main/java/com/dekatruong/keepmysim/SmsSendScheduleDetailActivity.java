package com.dekatruong.keepmysim;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.dekatruong.keepmysim.dto.SmsSendSchedule;

/**
 * An activity representing a single SmsSendSchedule detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * itemData details are presented side-by-side with a list of items
 * in a {@link SmsSendScheduleListActivity}.
 */
public class SmsSendScheduleDetailActivity extends AppCompatActivity {

    private SmsSendScheduleDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smssendschedule_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        //Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            //Input Intent
            Intent input_intent = this.getIntent();
            //int item_id = input_intent.getIntExtra(SmsSendScheduleDetailFragment.ARG_ITEM_ID, 0); //To do: default value
            SmsSendSchedule item = input_intent.getParcelableExtra(SmsSendScheduleDetailFragment.ARG_ITEM);

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            //arguments.putInt(SmsSendScheduleDetailFragment.ARG_ITEM_ID, item_id);
            arguments.putParcelable(SmsSendScheduleDetailFragment.ARG_ITEM, item);

            //Create fragment
            fragment = new SmsSendScheduleDetailFragment();
            fragment.setArguments(arguments);
            //Fill Fragment
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.smssendschedule_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, SmsSendScheduleListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
