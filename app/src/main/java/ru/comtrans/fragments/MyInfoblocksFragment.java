package ru.comtrans.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.adapters.MyInfoBlocksAdapter;
import ru.comtrans.items.MyInfoBlockItem;

/**
 * Created by Artco on 06.07.2016.
 */
public class MyInfoBlocksFragment extends Fragment {
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_infoblocks,container,false);

        recyclerView = (RecyclerView) v.findViewById(android.R.id.list);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupList();
    }

    private void setupList(){
        MyInfoBlocksAdapter adapter = new MyInfoBlocksAdapter(getContext(), new ArrayList<MyInfoBlockItem>(), new MyInfoBlocksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyInfoBlockItem item, int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }
}
