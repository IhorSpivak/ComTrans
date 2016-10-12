package ru.comtrans.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.activities.ShowInfoBlockActivity;
import ru.comtrans.adapters.DialogArrayAdapter;
import ru.comtrans.adapters.MyInfoBlocksAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.DialogItem;
import ru.comtrans.items.MyInfoBlockItem;
import ru.comtrans.services.SendingService;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.tasks.DeleteInfoBlockTask;

/**
 * Created by Artco on 06.07.2016.
 */

public class MyInfoBlocksFragment extends Fragment {
    private RecyclerView recyclerView;
    private InfoBlocksStorage storage;
    private MyInfoBlocksAdapter adapter;
    private InfoBlocksRefreshReceiver refreshReceiver;
    private InfoBlocksUpdateProgressReceiver updateProgressReceiver;
    private InfoBlocksUpdateStatusReceiver updateStatusReceiver;
    private TextView tvEmpty;
    private ProgressBar emptyBar;
    private ArrayList<MyInfoBlockItem> items;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_infoblocks, container, false);

        recyclerView = (RecyclerView) v.findViewById(android.R.id.list);
        tvEmpty = (TextView) v.findViewById(R.id.tv_empty);
        emptyBar = (ProgressBar) v.findViewById(R.id.empty_bar);
        storage = InfoBlocksStorage.getInstance();
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddInfoBlockActivity.class);
                startActivity(i);
            }
        });

        refreshReceiver = new InfoBlocksRefreshReceiver();
        updateProgressReceiver = new InfoBlocksUpdateProgressReceiver();
        updateStatusReceiver = new InfoBlocksUpdateStatusReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshReceiver, new IntentFilter(Const.REFRESH_INFO_BLOCKS_FILTER));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateProgressReceiver, new IntentFilter(Const.UPDATE_PROGRESS_INFO_BLOCKS_FILTER));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateStatusReceiver, new IntentFilter(Const.UPDATE_STATUS_INFO_BLOCKS_FILTER));

        new AsyncTaskForMyInfoBlocks().execute();


        return v;
    }


    private void setupList() {
        adapter = new MyInfoBlocksAdapter(getContext(), items, new MyInfoBlocksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyInfoBlockItem item, int position) {
                Intent i;
                switch (storage.getInfoBlockStatus(item.getId())) {
                    case MyInfoBlockItem.STATUS_DRAFT:
                        i = new Intent(getContext(), AddInfoBlockActivity.class);
                        i.putExtra(Const.EXTRA_INFO_BLOCK_ID, item.getId());
                        i.putExtra(Const.EXTRA_INFO_BLOCK_PAGE, item.getLastPosition());
                        startActivity(i);
                        break;
                    case MyInfoBlockItem.STATUS_SENDING:
                        Toast.makeText(getContext(), R.string.click_on_sending, Toast.LENGTH_SHORT).show();
                        break;
                    case MyInfoBlockItem.STATUS_SENT:
                        i = new Intent(getContext(),ShowInfoBlockActivity.class);
                        i.putExtra(Const.EXTRA_INFO_BLOCK_ID,item.getId());
                        i.putExtra(Const.EXTRA_INFO_BLOCK_PAGE,item.getLastPosition());
                        startActivity(i);

                        break;
                }

            }

            @Override
            public void onEllipsisClick(final MyInfoBlockItem item, int position) {


                final DialogArrayAdapter adapter = new DialogArrayAdapter();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(R.string.dialog_ellipsis)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (adapter.getItem(which).getResId()) {
                                    case R.string.dialog_restore:
                                    case R.string.dialog_send:
                                        sendInfoBlock(item.getId(), (int) item.getSize());
                                        break;
                                    case R.string.dialog_delete:
                                        deleteInfoBlock(item.getId());
                                        break;
                                    case R.string.dialog_send_email:
                                        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                        i.setType("text/plain");
                                        i.putExtra(Intent.EXTRA_EMAIL, "extra@email.com");
                                        i.putExtra(Intent.EXTRA_SUBJECT,"android - email with attachment");
                                        i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, storage.getPhotoAndVideo(item.getId(),getContext()));
                                        startActivity(Intent.createChooser(i, "Select application"));
                                        break;
                                    case R.string.dialog_neutral:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                ArrayList<DialogItem> items = new ArrayList<>();
                DialogItem dialogItem1 = new DialogItem(R.string.dialog_delete,getContext());
                DialogItem dialogItem3 = new DialogItem(R.string.dialog_neutral,getContext());
                DialogItem dialogItem4 = new DialogItem(R.string.dialog_send_email,getContext());


                if(storage.getInfoBlockStatus(item.getId())!=MyInfoBlockItem.STATUS_SENT) {
                    DialogItem dialogItem2;
                    if(storage.getInfoBlockStatus(item.getId())==MyInfoBlockItem.STATUS_STOPPED) {
                        dialogItem2 = new DialogItem(R.string.dialog_restore, getContext());
                    }else {
                        dialogItem2 = new DialogItem(R.string.dialog_send,getContext());
                    }

                    items.add(dialogItem2); items.add(dialogItem1);  items.add(dialogItem4); items.add(dialogItem3);
                }else {
                    items.add(dialogItem1);  items.add(dialogItem4); items.add(dialogItem3);
                }
                adapter.setItems(items);

                builder.create().show();






                }

        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(manager);

    }

    private void sendInfoBlock(String infoBlockId,int size){
        if(!Utility.isConnectingToInternet(getContext())) {
            Toast.makeText(getContext(),R.string.toast_not_connected_to_internet,Toast.LENGTH_SHORT).show();
        }else if(Utility.getBoolean(Const.SETTINGS_ALLOWS_MOBILE_CONN)&&!Utility.isConnectingToWifi(getContext())&&!Utility.isConnectingToFastNetwork(getContext())){
            Toast.makeText(getContext(),R.string.toast_weak_internet,Toast.LENGTH_LONG).show();
        }else if(!Utility.getBoolean(Const.SETTINGS_ALLOWS_BIG_DATA)&&size>100){
           Toast.makeText(getContext(),R.string.toast_settings_data_not_enabled,Toast.LENGTH_LONG).show();
        }else if(!Utility.getBoolean(Const.SETTINGS_ALLOWS_MOBILE_CONN)&&!Utility.isConnectingToWifi(getContext())){
            Toast.makeText(getContext(),R.string.toast_settings_wifi_not_enabled,Toast.LENGTH_LONG).show();
        }else {
            Intent intentMyIntentService = new Intent(getContext(), SendingService.class);
            intentMyIntentService.putExtra(Const.EXTRA_INFO_BLOCK_ID, infoBlockId);
            getActivity().startService(intentMyIntentService);
        }
    }


    private void deleteInfoBlock(final String infoBlockId){
        new DeleteInfoBlockTask(infoBlockId, getContext(), new DeleteInfoBlockTask.OnPostExecuteListener() {
            @Override
            public void onPostExecute() {
                adapter.removeItem(infoBlockId);
                if(adapter.getItemCount()==0){
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class AsyncTaskForMyInfoBlocks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvEmpty.setVisibility(View.GONE);
            emptyBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            items = storage.getPreviewItems();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (items.size() > 0) {
                tvEmpty.setVisibility(View.GONE);
                setupList();
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
            emptyBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(refreshReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(updateProgressReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(updateStatusReceiver);
        super.onDestroy();

    }

    private class InfoBlocksRefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new AsyncTaskForMyInfoBlocks().execute();
        }
    }

    private class InfoBlocksUpdateProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
            adapter.updateProgress(id);
        }
    }

    private class InfoBlocksUpdateStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
            adapter.updateStatus(id);
        }
    }
}
