package com.dekatruong.keepmysim;

import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dekatruong.keepmysim.dao.SmsSendScheduleDao;
import com.dekatruong.keepmysim.dto.SmsSendSchedule;

/**
 * A fragment representing a single SmsSendSchedule detail screen.
 * This fragment is either contained in a {@link SmsSendScheduleListActivity}
 * in two-pane mode (on tablets) or a {@link SmsSendScheduleDetailActivity}
 * on handsets.
 */
public class SmsSendScheduleDetailFragment extends Fragment {
    /**
     * The fragment argument representing the itemData ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM = "item"; // itemData

    /**
     * The dummy content this fragment is presenting.
     */
    private SmsSendSchedule itemData;

    //View
    private CollapsingToolbarLayout appBarLayout; //in parent Activity

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SmsSendScheduleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //View
        appBarLayout = (CollapsingToolbarLayout) this.getActivity()
                .findViewById(R.id.toolbar_layout);

        Bundle arguments = this.getArguments();

        if(arguments.containsKey(ARG_ITEM)){
            //transfer data. prevent load from DB
            this.itemData = arguments.getParcelable(ARG_ITEM);

        } else if (arguments.containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            this.itemData = SmsSendScheduleDao.getById(arguments.getInt(ARG_ITEM_ID));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.smssendschedule_detail, container, false);

        Spinner spinnerRecipients = (Spinner) rootView.findViewById(R.id.smssendschedule_detail_spinnerRecipients);
        TextView textViewMessage = ((TextView) rootView.findViewById(R.id.smssendschedule_detail_message));

        // Show the dummy content as text in a TextView.
        if (itemData != null) {
            textViewMessage.setText(itemData.getSmsSend().getMessage());

            spinnerRecipients.setAdapter(new ArrayAdapter<String>(
                    this.getActivity(),
                    android.R.layout.simple_spinner_dropdown_item,
                    itemData.getSmsSend().getRecipients()));

            //View
            appBarLayout.setTitle(this.itemData.getSendingCalendarStringByFormat("dd/MM/yyyy HH:mm"));

        }

        return rootView;
    }
}
