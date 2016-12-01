package ru.comtrans.fragments.infoblock.add;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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


    public static InfoBlockTutorialFragment newInstance() {
        Bundle args = new Bundle();
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


        return v;
    }
    private void removeFragment(){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(this);
        trans.commit();
    }

    @Override
    public void onClick(View view) {
        Utility.saveBoolean(Const.IS_FIRST_ADD_INFOBLOCK_LAUNCH,true);
        getTargetFragment().onActivityResult(Const.TUTORIAL_FRAGMENT_REQUEST,Const.TUTORIAL_FRAGMENT_REQUEST,null);
        removeFragment();
    }
}
