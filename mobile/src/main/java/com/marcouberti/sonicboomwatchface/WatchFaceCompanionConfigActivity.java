package com.marcouberti.sonicboomwatchface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.wearable.companion.WatchFaceCompanion;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.marcouberti.sonicboomwatchface.utils.TimeZoneUtils;

import java.util.Date;

/**
 * The phone-side config activity for {@code DigitalWatchFaceService}. Like the watch-side config
 * activity ({@code DigitalWatchFaceWearableConfigActivity}), allows for setting the background
 * color. Additionally, enables setting the color for hour, minute and second digits.
 */
public class WatchFaceCompanionConfigActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DataItemResult>, DataApi.DataListener{
    private static final String TAG = "AirheroFaceConfig";

    private GoogleApiClient mGoogleApiClient;
    private String mPeerId;

    private Toolbar toolbar;

    protected RecyclerView recyclerView;
    private ConfigListAdapter adapter;
    private RecyclerView.LayoutManager robotLayoutManager;
    CustomGradientView previewView;

    ConfigListModel listModel = new ConfigListModel();
    private String secondTimezoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nature_gradient_watch_face_config);

        mPeerId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        ComponentName name = getIntent().getParcelableExtra(
                WatchFaceCompanion.EXTRA_WATCH_FACE_COMPONENT);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        previewView = (CustomGradientView)findViewById(R.id.gradient);

        //CREATE LIST MODEL
        listModel.addItem(new ConfigListModel.GroupItem(R.string.other_configs));
        listModel.addItem(new ConfigListModel.SecondTimezoneItem());
        listModel.addItem(new ConfigListModel.GroupItem(R.string.accent_color));
        String[] availableColors = getResources().getStringArray(R.array.gradients_face_array);
        for (String colorName : availableColors) {
            listModel.addItem(new ConfigListModel.AccentColorItem(colorName, GradientsUtils.getColorID(colorName)));
        }
        listModel.addItem(new ConfigListModel.SeparatorItem());

        listModel.addItem(new ConfigListModel.RateAppItem(R.string.rate_this_app));
        //END CREATE LIST MODEL

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);//no animation for changes
        recyclerView.setItemAnimator(animator);
        robotLayoutManager = new LinearLayoutManager(this);
        //robotLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(robotLayoutManager);
        adapter = new ConfigListAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected: " + connectionHint);

        if (mPeerId != null) {
            Uri.Builder builder = new Uri.Builder();
            Uri uri = builder.scheme("wear").path(WatchFaceUtil.PATH_WITH_FEATURE).authority(mPeerId).build();
            Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(this);
            Wearable.DataApi.addListener(mGoogleApiClient, this);
        } else {
            displayNoConnectedDeviceDialog();
        }
    }

    @Override // ResultCallback<DataApi.DataItemResult>
    public void onResult(DataApi.DataItemResult dataItemResult) {
        if (dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null) {
            DataItem configDataItem = dataItemResult.getDataItem();
            DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
            DataMap config = dataMapItem.getDataMap();
            Log.d(TAG, "startup setup UI...");
            updateUiForConfigDataMap(config);
            //setUpAllPickers(config);
        } else {
            // If DataItem with the current config can't be retrieved, select the default items on
            // each picker.
            //setUpAllPickers(null);
        }
    }

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
            Log.d(TAG, "onConnectionSuspended: " + cause);
    }

    @Override // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
    }

    private void displayNoConnectedDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String messageText = getResources().getString(R.string.title_no_device_connected);
        String okText = getResources().getString(R.string.ok_no_device_connected);
        builder.setMessage(messageText)
                .setCancelable(false)
                .setPositiveButton(okText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendConfigUpdateMessage(String configKey, int value) {
        if (mPeerId != null) {
            DataMap config = new DataMap();
            //config.putLong(WatchFaceUtil.KEY_TIMESTAMP, new Date().getTime());
            config.putInt(configKey, value);
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, WatchFaceUtil.PATH_WITH_FEATURE, rawData);

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Sent watch face config message: " + configKey + " -> "
                        + Integer.toHexString(value));
            }
        }
    }

    private void sendConfigUpdateMessage(String configKey, String value) {
        if (mPeerId != null) {
            DataMap config = new DataMap();
            //config.putLong(WatchFaceUtil.KEY_TIMESTAMP, new Date().getTime());
            config.putString(configKey, value);
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, WatchFaceUtil.PATH_WITH_FEATURE, rawData);

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Sent watch face config message: " + configKey + " -> "
                        + value);
            }
        }
    }

    private void updateUiForConfigDataMap(final DataMap config) {
        boolean uiUpdated = false;
        for (String configKey : config.keySet()) {
            if (!config.containsKey(configKey)) {
                continue;
            }
            if(configKey.equalsIgnoreCase(WatchFaceUtil.KEY_BACKGROUND_COLOR)) {
                int color = config.getInt(configKey);
                Log.d(TAG, "Found watch face config key: " + configKey + " -> " + color);

                if (updateUiForKey(configKey, color)) {
                    uiUpdated = true;
                }
            }
            else if(configKey.equalsIgnoreCase(WatchFaceUtil.KEY_SECOND_TIMEZONE)) {
                String timezoneID = config.getString(configKey);
                Log.d(TAG, "Found watch face config key: " + configKey + " -> " + timezoneID);

                if (updateUiForKey(configKey, timezoneID)) {
                    uiUpdated = true;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Updates the color of a UI item according to the given {@code configKey}. Does nothing if
     * {@code configKey} isn't recognized.
     *
     * @return whether UI has been updated
     */
    private boolean updateUiForKey(String configKey, final int color) {
        Log.d(TAG,"updateUiForKey "+configKey+ " color = "+color);
        if (configKey.equals(WatchFaceUtil.KEY_BACKGROUND_COLOR)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    previewView.color = GradientsUtils.getGradients(getApplicationContext(), color);
                    previewView.invalidate();
                }
            });
        } else {
            Log.w(TAG, "Ignoring unknown config key: " + configKey);
            return false;
        }
        return true;
    }

    private boolean updateUiForKey(String configKey, final String value) {
        Log.d(TAG,"updateUiForKey "+configKey+ " value = "+value);
        if (configKey.equals(WatchFaceUtil.KEY_SECOND_TIMEZONE)) {
            secondTimezoneId = value;
        } else {
            Log.w(TAG, "Ignoring unknown config key: " + configKey);
            return false;
        }
        return true;
    }

    public void openTimezoneDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(WatchFaceCompanionConfigActivity.this);
        builderSingle.setTitle(getString(R.string.secondary_timezone));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                WatchFaceCompanionConfigActivity.this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(TimeZoneUtils.getAllTimezones());

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        Log.d(TAG, "timezone selected ID = " + strName);
                        //update data layer
                        sendConfigUpdateMessage(WatchFaceUtil.KEY_SECOND_TIMEZONE,strName);
                        secondTimezoneId = strName;
                        adapter.notifyDataSetChanged();
                    }
                });
        builderSingle.show();
    }

    public void rateThisAppClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.marcouberti.sonicboomwatchface"));
        startActivity(intent);
    }

    public void seeOtherAppsClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=pub:Marco Uberti"));
        startActivity(intent);
    }

    @Override // DataApi.DataListener
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG,"onDataChanged "+dataEvents);
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() != DataEvent.TYPE_CHANGED) {
                continue;
            }

            DataItem dataItem = dataEvent.getDataItem();
            if (!dataItem.getUri().getPath().equals(WatchFaceUtil.PATH_WITH_FEATURE)) {
                continue;
            }

            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
            DataMap config = dataMapItem.getDataMap();
            Log.d(TAG, "Config DataItem updated:" + config);

            updateUiForConfigDataMap(config);
        }
    }


    public class ConfigListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class AccentColorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            // each data item is just a string in this case
            public RelativeLayout itemContainer;
            public TextView nameText;
            public CustomGradientView gradientView;


            public AccentColorViewHolder(RelativeLayout v) {
                super(v);
                v.setOnClickListener(this);
                itemContainer = v;
                nameText = (TextView)v.findViewById(R.id.name);
                gradientView = (CustomGradientView)v.findViewById(R.id.gradient);
            }
            @Override
            public void onClick(View view) {
                    //position
                    int itemPosition = getAdapterPosition();
                    ConfigListModel.AccentColorItem item = (ConfigListModel.AccentColorItem)listModel.getItemByAbsoluteIndex(itemPosition);

                    Log.d(TAG, "clicked position " + itemPosition + " with color "+ item.colorID + "color name = "+item.colorName);
                    previewView.color = GradientsUtils.getGradients(getApplicationContext(),item.colorName);
                    previewView.invalidate();
                    sendConfigUpdateMessage(WatchFaceUtil.KEY_BACKGROUND_COLOR, GradientsUtils.getColorID(item.colorName));
            }
        }

        public class SecondaryTimezoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            // each data item is just a string in this case
            public RelativeLayout itemContainer;
            public TextView timezoneView;
            public SecondaryTimezoneViewHolder(RelativeLayout v) {
                super(v);
                v.setOnClickListener(this);
                itemContainer = v;
                timezoneView = (TextView)v.findViewById(R.id.timezone);
            }
            @Override
            public void onClick(View view) {
                openTimezoneDialog();
            }
        }

        public class RateAppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            // each data item is just a string in this case
            public RelativeLayout itemContainer;

            public RateAppViewHolder(RelativeLayout v) {
                super(v);
                v.setOnClickListener(this);
                itemContainer = v;
            }
            @Override
            public void onClick(View view) {
                rateThisAppClick();
            }
        }

        public class SeparatorViewHolder extends RecyclerView.ViewHolder{
            // each data item is just a string in this case
            public RelativeLayout itemContainer;

            public SeparatorViewHolder(RelativeLayout v) {
                super(v);
                itemContainer = v;
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder{
            // each data item is just a string in this case
            public RelativeLayout itemContainer;

            public FooterViewHolder(RelativeLayout v) {
                super(v);
                itemContainer = v;
            }
        }

        public class GroupViewHolder extends RecyclerView.ViewHolder{
            // each data item is just a string in this case
            public RelativeLayout itemContainer;
            public TextView nameView;

            public GroupViewHolder(RelativeLayout v) {
                super(v);
                itemContainer = v;
                nameView = (TextView)v.findViewById(R.id.name);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ConfigListAdapter() {}

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

            if(viewType == ConfigListModel.TYPE_ACCENT_COLOR) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.config_list_accent_color_item, parent, false);
                ConfigListAdapter.AccentColorViewHolder vh = new ConfigListAdapter.AccentColorViewHolder((RelativeLayout) v);
                return vh;
            }else if(viewType == ConfigListModel.TYPE_SECOND_TIMEZONE) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.config_list_secondary_timezone, parent, false);
                ConfigListAdapter.SecondaryTimezoneViewHolder vh = new ConfigListAdapter.SecondaryTimezoneViewHolder((RelativeLayout) v);
                return vh;
            }else if(viewType == ConfigListModel.TYPE_RATE_THIS_ASPP) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.config_list_rate_app, parent, false);
                ConfigListAdapter.RateAppViewHolder vh = new ConfigListAdapter.RateAppViewHolder((RelativeLayout) v);
                return vh;
            }else if(viewType == ConfigListModel.TYPE_GROUP) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.config_list_group, parent, false);
                ConfigListAdapter.GroupViewHolder vh = new ConfigListAdapter.GroupViewHolder((RelativeLayout) v);
                return vh;
            }
            else if(viewType == ConfigListModel.TYPE_FOOTER) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.config_list_footer, parent, false);
                ConfigListAdapter.FooterViewHolder vh = new ConfigListAdapter.FooterViewHolder((RelativeLayout) v);
                return vh;
            }
            else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.config_list_separator, parent, false);
                ConfigListAdapter.SeparatorViewHolder vh = new ConfigListAdapter.SeparatorViewHolder((RelativeLayout) v);
                return vh;
            }
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {

            if(vh instanceof AccentColorViewHolder) {
                ConfigListAdapter.AccentColorViewHolder holder = (ConfigListAdapter.AccentColorViewHolder) vh;
                ConfigListModel.AccentColorItem item = (ConfigListModel.AccentColorItem)listModel.getItemByAbsoluteIndex(position);
                //defaults
                holder.nameText.setText(item.colorName);
                holder.gradientView.color = GradientsUtils.getGradients(getApplicationContext(), item.colorName);
            }
            else if(vh instanceof SeparatorViewHolder) {
                //nothing to do
            }
            else if(vh instanceof GroupViewHolder) {
                ConfigListAdapter.GroupViewHolder holder = (ConfigListAdapter.GroupViewHolder) vh;
                ConfigListModel.GroupItem item = (ConfigListModel.GroupItem)listModel.getItemByAbsoluteIndex(position);
                //defaults
                holder.nameView.setText(getString(item.keyTitleRes));
            }else if(vh instanceof SecondaryTimezoneViewHolder) {
                ConfigListAdapter.SecondaryTimezoneViewHolder holder = (ConfigListAdapter.SecondaryTimezoneViewHolder) vh;
                if(secondTimezoneId != null) {
                    holder.timezoneView.setText(secondTimezoneId);
                }
            }else if(vh instanceof RateAppViewHolder) {
                //nothing to do
            }else if(vh instanceof FooterViewHolder) {
                //nothing to do
            }
        }

        @Override
        public int getItemViewType(int position) {
            return listModel.getRowType(position);
        }

        @Override
        public int getItemCount() {
            return listModel.getTotalRowCount();
        }

    }
}
