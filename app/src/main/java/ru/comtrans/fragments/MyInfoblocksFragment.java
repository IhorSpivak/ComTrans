package ru.comtrans.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.adapters.MyInfoBlocksAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.InfoBlocksStorage;
import ru.comtrans.items.MyInfoBlockItem;

/**
 * Created by Artco on 06.07.2016.
 */
public class MyInfoBlocksFragment extends Fragment {
    private RecyclerView recyclerView;
    private InfoBlocksStorage storage;
    private MyInfoBlocksAdapter adapter;
    private InfoBlocksRefreshReceiver refreshReceiver;
    private TextView tvEmpty;
    private ProgressBar emptyBar;
    private ArrayList<MyInfoBlockItem> items;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_infoblocks,container,false);

        recyclerView = (RecyclerView) v.findViewById(android.R.id.list);
        tvEmpty = (TextView)v.findViewById(R.id.tv_empty);
        emptyBar = (ProgressBar)v.findViewById(R.id.empty_bar);
        storage = InfoBlocksStorage.getInstance();
        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),AddInfoBlockActivity.class);
                startActivity(i);
            }
        });

        refreshReceiver = new InfoBlocksRefreshReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshReceiver,new IntentFilter(Const.REFRESH_INFO_BLOCKS_FILTER));

        new AsyncTaskForMyInfoBlocks().execute();


        return v;
    }



    private void setupList(){
        adapter = new MyInfoBlocksAdapter(getContext(),items, new MyInfoBlocksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyInfoBlockItem item, int position) {
                Log.d("TAG","extra id"+item.getId());
                Intent i = new Intent(getContext(),AddInfoBlockActivity.class);
                i.putExtra(Const.EXTRA_INFO_BLOCK_ID,item.getId());
                startActivity(i);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(manager);
    }

    private class AsyncTaskForMyInfoBlocks extends AsyncTask<Void,Void,Void>{

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
            if(items.size()>0){
                tvEmpty.setVisibility(View.GONE);
                setupList();
            }else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
            emptyBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    private class InfoBlocksRefreshReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            new AsyncTaskForMyInfoBlocks().execute();
        }
    }
}
