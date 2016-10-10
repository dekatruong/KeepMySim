package com.dekatruong.keepmysim;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import com.dekatruong.keepmysim.dao.SmsSendScheduleDao;
import com.dekatruong.keepmysim.dto.SmsSendSchedule;

import java.text.SimpleDateFormat;
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

    private Toolbar toolbar;
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerViewSmsSendScheduleList;
    private SmsSendScheduleRecyclerViewAdapter mSmsSendScheduleRecyclerViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayoutSmsSendScheduleList;


    //Dependency
    private SmsSendScheduleDao mSmsSendScheduleDao;

    private AlarmManager alarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smssendschedule_list);

        //Dependency
        mSmsSendScheduleDao = new SmsSendScheduleDao(this);
        //Service Locator:
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        //View
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getTitle());
        this.setSupportActionBar(toolbar);

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        //Detach Screen-size
        if (findViewById(R.id.smssendschedule_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

        //SmsSendSchedule List
        recyclerViewSmsSendScheduleList = (RecyclerView)findViewById(R.id.smssendschedule_list);
        mSmsSendScheduleRecyclerViewAdapter = new SmsSendScheduleRecyclerViewAdapter(mSmsSendScheduleDao.getAll());
        recyclerViewSmsSendScheduleList.setAdapter(mSmsSendScheduleRecyclerViewAdapter);

        //
        swipeRefreshLayoutSmsSendScheduleList =
                (SwipeRefreshLayout) findViewById(R.id.smssendschedule_list_swipe_refresh_container);
        swipeRefreshLayoutSmsSendScheduleList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            /**
             * Called when a swipe gesture triggers a refresh.
             */
            @Override
            public void onRefresh() {
                //Load.
                List<SmsSendSchedule> new_data = SmsSendScheduleListActivity.this.mSmsSendScheduleDao.getAll();
                //Log.i("MyApp",new_data.toString());
                //Fill data and notify view
                SmsSendScheduleListActivity.this.mSmsSendScheduleRecyclerViewAdapter.updateAllData(new_data);

                //Job has done: stop refresh progress.
                swipeRefreshLayoutSmsSendScheduleList.setRefreshing(false);
        }
        });

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

        private List<SmsSendSchedule> mValues;

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
            holder.mIntervalView.setText(String.valueOf(holder.itemData.getInterval()));
            holder.mSwitchEnableSheduleView.setChecked(SmsSendSchedule.Status.ENABLE == holder.itemData.getStatus());

            //Click to an Item View
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        //arguments.putInt(SmsSendScheduleDetailFragment.ARG_ITEM_ID, holder.itemData.getRequestCode());
                        arguments.putParcelable(SmsSendScheduleDetailFragment.ARG_ITEM, holder.itemData);

                        SmsSendScheduleDetailFragment fragment = new SmsSendScheduleDetailFragment();
                        fragment.setArguments(arguments);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.smssendschedule_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, SmsSendScheduleDetailActivity.class);
                        //intent.putExtra(SmsSendScheduleDetailFragment.ARG_ITEM_ID, holder.itemData.getRequestCode());
                        intent.putExtra(SmsSendScheduleDetailFragment.ARG_ITEM, holder.itemData);

                        context.startActivity(intent);
                    }
                }
            });

            //switch Enable/Disable Schedule
            holder.mSwitchEnableSheduleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ///Info
                    int request_code = holder.itemData.getRequestCode();

                    if (isChecked) {

                        Log.i("MyApp","Reset request-code: "+request_code);

                    } else {
                        Log.i("MyApp","Cancel request-code: "+request_code);
                        //Build PendingIntent that match last PendingIntent: request-code, receiver-class

                        //Perform cancel
                        alarmMgr.cancel(
                                PendingIntent.getBroadcast(
                                    SmsSendScheduleListActivity.this, //can use any context
                                    request_code, //IMPORTANT parameter
                                    new Intent(SmsSendScheduleListActivity.this, //can use any context
                                            SmsSendingAlarmReceiver.class), //IMPORTANT parameter
                                    PendingIntent.FLAG_UPDATE_CURRENT //can use any
                                )
                            );

                        //Model: update DB


                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * To do: think about performance problem
         * @param new_data
         */
        public void updateAllData(List<SmsSendSchedule> new_data) {
            this.mValues = new_data;

            this.notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mScheduleView;
            public final TextView mSmsSendRecipientsView;
            public final TextView mSmsSendMessageView;
            public final TextView mIntervalView;
            public final Switch mSwitchEnableSheduleView;

            //Data
            public SmsSendSchedule itemData;

            public ViewHolder(View view) {
                super(view);

                mView = view;

                mScheduleView = (TextView) view.findViewById(R.id.schedule_content_schedule);
                mSmsSendRecipientsView = (TextView) view.findViewById(R.id.smssend_recipients);
                mSmsSendMessageView = (TextView) view.findViewById(R.id.smssend_message);
                mIntervalView = (TextView) view.findViewById(R.id.schedule_content_interval);
                mSwitchEnableSheduleView = (Switch)view.findViewById(R.id.schedule_content_switchEnableShedule);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mSmsSendMessageView.getText() + "'";
            }
        }
    }

}
