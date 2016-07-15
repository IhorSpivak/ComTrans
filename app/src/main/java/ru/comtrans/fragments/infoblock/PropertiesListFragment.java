package ru.comtrans.fragments.infoblock;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ru.comtrans.R;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.activities.SearchValueActivity;
import ru.comtrans.adapters.InfoBlockAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.PropHelper;
import ru.comtrans.items.MainItem;
import ru.comtrans.views.DividerItemDecoration;

/**
 * Created by Artco on 06.07.2016.
 */
public class PropertiesListFragment extends Fragment {

    PropHelper propHelper;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    InfoBlockAdapter adapter;
    int page;



    public static PropertiesListFragment newInstance(int page) {

        Bundle args = new Bundle();
        args.putInt(Const.PAGE,page);
        PropertiesListFragment fragment = new PropertiesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);
        initUi(v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        page = getArguments().getInt(Const.PAGE);
        new AsyncTaskForGetProp().execute();

    }

    private void initUi(View v){
        recyclerView = (RecyclerView)v.findViewById(android.R.id.list);
        layoutManager = new LinearLayoutManager(getActivity());
    }




    private void initPage(){
        adapter = new InfoBlockAdapter(getActivity(), propHelper.getScreenItems(page), new InfoBlockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MainItem item,View view) {
                Intent i;
                switch (item.getType()){
                    case MainItem.TYPE_LIST:
                        i = new Intent(getActivity(), SearchValueActivity.class);
                        i.putParcelableArrayListExtra(Const.EXTRA_VALUES,item.getValues());
                        startActivity(i);
                        break;
                    case MainItem.TYPE_PHOTO:
                        i = new Intent(getActivity(), CameraActivity.class);
                        i.putExtra(Const.CAMERA_MODE,Const.MODE_PHOTO);
                        i.putParcelableArrayListExtra(Const.EXTRA_VALUES,item.getPhotoItems());
                        startActivity(i);
                        break;
                }

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));

    }

    private class AsyncTaskForGetProp extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            propHelper = PropHelper.getInstance();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initPage();
        }
    }


}
