package ru.comtrans.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.activities.ShowInfoBlockActivity;
import ru.comtrans.adapters.DialogArrayAdapter;
import ru.comtrans.adapters.MyInfoBlocksAdapter;
import ru.comtrans.adapters.ListDialogAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.DialogItem;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MyInfoblockItem;
import ru.comtrans.services.SendingService;
import ru.comtrans.singlets.AppController;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.tasks.DeleteInfoBlockTask;
import ru.comtrans.views.ConnectionProgressDialog;

/**
 * Created by Artco on 06.07.2016.
 */

public class MyInfoblocksFragment extends Fragment {
    private RecyclerView recyclerView;
    private InfoBlocksStorage storage;
    private MyInfoBlocksAdapter adapter;
    private InfoBlocksRefreshReceiver refreshReceiver;
    private InfoBlocksUpdateProgressReceiver updateProgressReceiver;
    private InfoBlocksUpdateStatusReceiver updateStatusReceiver;
    private TextView tvEmpty;
    private ProgressBar emptyBar;
    private ArrayList<MyInfoblockItem> items;
    private ConnectionProgressDialog progressDialog;


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
                checkAudioRecordingPermission();
            }
        });

        refreshReceiver = new InfoBlocksRefreshReceiver();
        updateProgressReceiver = new InfoBlocksUpdateProgressReceiver();
        updateStatusReceiver = new InfoBlocksUpdateStatusReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshReceiver, new IntentFilter(Const.REFRESH_INFO_BLOCKS_FILTER));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateProgressReceiver, new IntentFilter(Const.UPDATE_PROGRESS_INFO_BLOCKS_FILTER));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateStatusReceiver, new IntentFilter(Const.UPDATE_STATUS_INFO_BLOCKS_FILTER));

        new AsyncTaskForMyInfoBlocks().execute();

        progressDialog = new ConnectionProgressDialog(getActivity());

        getInspectionCategories(false);
        getInspectionTypes(false);


        return v;
    }

    private void getInspectionCategories(final boolean isFromButton){
        Call<JsonObject> call = AppController.apiInterface.getInspectionCategories(Utility.getToken());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.body() != null && !response.body().get("result").isJsonNull()) {
                    if (response.body().get("status").getAsInt() == 1){
                        JsonArray result = response.body().get("result").getAsJsonArray();
                        if(!result.isJsonNull()&&result.size()>0){
                            ArrayList<ListItem> typeArray = new ArrayList<>();
                            List<Map<String, List<String>>> categories = new ArrayList<>();
                            for (int i = 0; i < result.size(); i++) {
                                JsonObject typeObject = result.get(i).getAsJsonObject();
                                if(!typeObject.isJsonNull()){
                                    ListItem item = new ListItem(typeObject.get("id").getAsInt(),typeObject.get("name").getAsString());
                                    typeArray.add(item);

                                    JsonElement id = typeObject.get("id");

                                    if (id != null && id.getAsInt() > 0) {
                                        JsonArray array = typeObject.getAsJsonArray("type_link");
                                        List<String> vehicleIds = new ArrayList<>();
                                        if (array != null) {
                                            for (int j = 0; j < array.size(); j++) {
                                                vehicleIds.add(array.get(j).toString());
                                            }
                                        }
                                        Map<String, List<String>> map = new HashMap<>();
                                        map.put(typeObject.get("id").getAsString(), vehicleIds);
                                        categories.add(map);
                                    }
                                }
                            }
                            Utility.saveVehicleTypes(typeArray);
                            Utility.saveCategories(categories);
                            if(isFromButton)
                                createNewInfoBlock();
                        }
                    }else{
                        Toast.makeText(getActivity(), response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }



            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),R.string.something_went_wrong,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getInspectionTypes(final boolean isFromButton){
        Call<JsonObject> call = AppController.apiInterface.getInspectionTypes(Utility.getToken());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.body() != null && !response.body().get("result").isJsonNull()) {
                    if (response.body().get("status").getAsInt() == 1){
                        JsonArray result = response.body().get("result").getAsJsonArray();
                        if(!result.isJsonNull()&&result.size()>0){
                            ArrayList<ListItem> typeArray = new ArrayList<>();
                            for (int i = 0; i < result.size(); i++) {
                                JsonObject typeObject = result.get(i).getAsJsonObject();
                                if(!typeObject.isJsonNull()){
                                    ListItem item = new ListItem(typeObject.get("id").getAsInt(),typeObject.get("name").getAsString());
                                    typeArray.add(item);
                                }
                            }
                            Log.d("TAG","save array");
                            Utility.saveInspectionTypes(typeArray);
                            if(isFromButton)
                                createNewInfoBlock();

                        }
                    }else{
                        Toast.makeText(getActivity(), response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }



            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),R.string.something_went_wrong,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupList() {
        adapter = new MyInfoBlocksAdapter(getContext(), items, new MyInfoBlocksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyInfoblockItem item, int position) {
                Intent i;
                switch (storage.getInfoBlockStatus(item.getId())) {
                    case MyInfoblockItem.STATUS_DRAFT:
                        i = new Intent(getContext(), AddInfoBlockActivity.class);
                        i.putExtra(Const.EXTRA_INFO_BLOCK_ID, item.getId());
                        i.putExtra(Const.EXTRA_INFO_BLOCK_PAGE, item.getLastPosition());
                        i.putExtra(Const.EXTRA_PROP_CODE, InfoBlocksStorage.getInfoBlockCategoryCode(item.getId()));
                        i.putExtra(Const.EXTRA_INSPECTION_CODE, InfoBlocksStorage.getInfoBlockInspectionCode(item.getId()));
                        startActivity(i);
                        break;
                    case MyInfoblockItem.STATUS_SENDING:
                        Toast.makeText(getContext(), R.string.click_on_sending, Toast.LENGTH_SHORT).show();
                        break;
                    case MyInfoblockItem.STATUS_SENT:
                        // storage.setInfoBlockStatus(item.getId(),MyInfoBlockItem.STATUS_DRAFT);
                        i = new Intent(getContext(), ShowInfoBlockActivity.class);
                        i.putExtra(Const.EXTRA_INFO_BLOCK_ID, item.getId());
                        i.putExtra(Const.EXTRA_INFO_BLOCK_PAGE, item.getLastPosition());
                        startActivity(i);
//                        i = new Intent(getContext(),ShowInfoBlockActivity.class);
//                        i.putExtra(Const.EXTRA_INFO_BLOCK_ID,item.getId());
//                        i.putExtra(Const.EXTRA_INFO_BLOCK_PAGE,item.getLastPosition());
//                        startActivity(i);

                        break;
                }

            }

            @Override
            public void onEllipsisClick(final MyInfoblockItem item, int position) {


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
                //  DialogItem dialogItem4 = new DialogItem(R.string.dialog_send_email,getContext());


                if(storage.getInfoBlockStatus(item.getId())!= MyInfoblockItem.STATUS_SENT) {
                    DialogItem dialogItem2;
                    if(storage.getInfoBlockStatus(item.getId())== MyInfoblockItem.STATUS_STOPPED) {
                        dialogItem2 = new DialogItem(R.string.dialog_restore, getContext());
                    }else {
                        dialogItem2 = new DialogItem(R.string.dialog_send,getContext());
                    }

                    items.add(dialogItem2); items.add(dialogItem1); // items.add(dialogItem4);
                    items.add(dialogItem3);
                }else {
                    items.add(dialogItem1);  //items.add(dialogItem4);
                    items.add(dialogItem3);
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


    @TargetApi(Build.VERSION_CODES.M)
    private void checkAudioRecordingPermission(){
        int hasStoragePermission;
        int hasRecorderPermission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasStoragePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            hasRecorderPermission = getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            List<String> permissions = new ArrayList<>();

            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if(hasRecorderPermission!= PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (!permissions.isEmpty()) {

                requestPermissions(permissions.toArray(new String[permissions.size()]),
                        Const.REQUEST_PERMISSION_AUDIO_RECORDING);


            } else {
                createNewInfoBlock();
            }
        } else {
            createNewInfoBlock();
        }
    }

    private void createNewInfoBlock(){
        if(Utility.getVehicleTypes()==null) {
            progressDialog.show();
            //  Toast.makeText(getContext(),R.string.vehicle_type_not_exist,Toast.LENGTH_SHORT).show();
            getInspectionCategories(true);
        }else if(Utility.getInspectionTypes()==null) {
            progressDialog.show();
            //   Toast.makeText(getContext(),R.string.inspection_type_not_exist,Toast.LENGTH_SHORT).show();
            getInspectionTypes(true);
        }else {
            final ListDialogAdapter vehicleTypeAdapter = new ListDialogAdapter(Utility.getVehicleTypes(), getContext());


            LayoutInflater inflater = getActivity().getLayoutInflater();


            View vehicleTypeTitle = inflater.inflate(R.layout.dialog_title, null);
            AlertDialog.Builder vehicleTypeBuilder = new AlertDialog.Builder(getContext());
            vehicleTypeBuilder.setCustomTitle(vehicleTypeTitle);
            vehicleTypeBuilder.setAdapter(vehicleTypeAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (vehicleTypeAdapter.getItem(i).getId() != -1){
                    setInspectionType(vehicleTypeAdapter.getItem(i).getId());
                    }else {
                        Toast.makeText(getContext(), R.string.must_to_choose_category, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            vehicleTypeBuilder.show();

        }
    }

    public void setInspectionType(final long vehicleTypeId){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final ListDialogAdapter inspectionAdapter = new ListDialogAdapter(Utility.getInspectionTypes(), getContext());


        TextView inspectionTypeTitle = (TextView) inflater.inflate(R.layout.dialog_title, null);
        inspectionTypeTitle.setText(R.string.choose_inspection_type_title);
        AlertDialog.Builder inspectionTypeBuilder = new AlertDialog.Builder(getContext());
        inspectionTypeBuilder.setCustomTitle(inspectionTypeTitle);
        inspectionTypeBuilder.setAdapter(inspectionAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (inspectionAdapter.getItem(i).getId() != -1) {
                    Intent intent = new Intent(getContext(), AddInfoBlockActivity.class);
                    intent.putExtra(Const.EXTRA_PROP_CODE, vehicleTypeId);
                    intent.putExtra(Const.EXTRA_INSPECTION_CODE, inspectionAdapter.getItem(i).getId());
                    startActivity(intent);
                    dialogInterface.dismiss();
                } else {
                    dialogInterface.cancel();
                    Toast.makeText(getContext(), R.string.must_to_choose_inspection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        inspectionTypeBuilder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allow = true;
        switch (requestCode){

            case Const.REQUEST_PERMISSION_AUDIO_RECORDING:
                for (int i = 0; i < permissions.length; i++) {
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        allow = false;
                    }
                }
                if(allow) {
                    createNewInfoBlock();
                }
                else {
                    Toast.makeText(getContext(), R.string.not_all_permissions_granted, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
