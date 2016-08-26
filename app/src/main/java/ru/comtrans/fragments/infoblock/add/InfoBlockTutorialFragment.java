package ru.comtrans.fragments.infoblock.add;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;

/**
 * Created by Artco on 03.08.2016.
 */
public class InfoBlockTutorialFragment extends Fragment implements View.OnClickListener {

    private ImageView close;
    private Button confirm;
    private String infoBlockId;
    private int page;
    private boolean isNew;

    public static InfoBlockTutorialFragment newInstance(String id, int page, boolean isNew) {
        Bundle args = new Bundle();
        args.putString(Const.EXTRA_INFO_BLOCK_ID,id);
        args.putInt(Const.EXTRA_INFO_BLOCK_PAGE,page);
        args.putBoolean(Const.IS_NEW_INFO_BLOCK,isNew);
        InfoBlockTutorialFragment fragment = new InfoBlockTutorialFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_block_tutorial,container,false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        close = (ImageView)v.findViewById(R.id.button_close);
        confirm = (Button)v.findViewById(R.id.button_confirm);
        close.setOnClickListener(this);
        confirm.setOnClickListener(this);
        infoBlockId = getArguments().getString(Const.EXTRA_INFO_BLOCK_ID);
        page = getArguments().getInt(Const.EXTRA_INFO_BLOCK_PAGE,0);
        isNew = getArguments().getBoolean(Const.IS_NEW_INFO_BLOCK);
        return v;
    }

    @Override
    public void onClick(View view) {
        Utility.saveBoolean(Const.IS_FIRST_ADD_INFOBLOCK_LAUNCH,true);
        getFragmentManager().beginTransaction().replace(R.id.container, AddInfoBlockFragment.newInstance(infoBlockId, page, isNew)).commit();
    }
}
