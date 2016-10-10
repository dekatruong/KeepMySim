package com.dekatruong.keepmysim;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.dekatruong.keepmysim.dao.SmsSendScheduleDao;
import com.dekatruong.keepmysim.dto.SmsSendSchedule;
import com.dekatruong.keepmysim.dummy.DummyContent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * An activity representing a list of SmsSendSchedules. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SmsSendScheduleDetailActivity} representing
 * itemData details. On tablets, the activity presents the list of items and
 * itemData details side-by-side using two vertical panes.
 */
public class SmsSendScheduleListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    Toolbar toolbar;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smssendschedule_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getTitle());
        this.setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fabAdd);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.smssendschedule_list);
        recyclerView.setAdapter(
                new SmsSendScheduleRecyclerViewAdapter(SmsSendScheduleDao.getAll()));

        //
        if (findViewById(R.id.smssendschedule_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {


//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public void onClick_fabAdd(View view) {

        //open SmsSetupActivity
        Context context = view.getContext();
        context.startActivity(new Intent(context, SmsSetupActivity.class));
    }

    public class SmsSendScheduleRecyclerViewAdapter
            extends RecyclerView.Adapter<SmsSendScheduleRecyclerViewAdapter.ViewHolder> {

        private final List<SmsSendSchedule> mValues;

        public SmsSendScheduleRecyclerViewAdapter(List<SmsSendSchedule> items) {
            this.mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.smssendschedule_list_content, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.itemData = this.mValues.get(position);
            //View
            holder.mScheduleView.setText((new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(holder.itemData.getSendingCalendar().getTime()));
            holder.mSmsSendRecipientsView.setText(holder.itemData.getSmsSend().getRecipientsString());
            holder.mSmsSendMessageView.setText(holder.itemData.getSmsSend().getMessage());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        //arguments.putInt(SmsSendScheduleDetailFragment.ARG_ITEM_ID, holder.itemData.getRequestId());
                        arguments.putParcelable(SmsSendScheduleDetailFragment.ARG_ITEM, holder.itemData);

                        SmsSendScheduleDetailFragment fragment = new SmsSendScheduleDetailFragment();
                        fragment.setArguments(arguments);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.smssendschedule_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, SmsSendScheduleDetailActivity.class);
                        //intent.putExtra(SmsSendScheduleDetailFragment.ARG_ITEM_ID, holder.itemData.getRequestId());
                        intent.putExtra(SmsSendScheduleDetailFragment.ARG_ITEM, holder.itemData);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mScheduleView;
            public final TextView mSmsSendRecipientsView;
            public final TextView mSmsSendMessageView;
            //Data
            public SmsSendSchedule itemData;

            public ViewHolder(View view) {
                super(view);

                mView = view;

                mScheduleView = (TextView) view.findViewById(R.id.schedule);
                mSmsSendRecipientsView = (TextView) view.findViewById(R.id.smssend_recipients);
                mSmsSendMessageView = (TextView) view.findViewById(R.id.smssend_message);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mSmsSendMessageView.getText() + "'";
            }
        }
    }

}
