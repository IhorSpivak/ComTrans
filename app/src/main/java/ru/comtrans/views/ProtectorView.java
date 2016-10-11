package ru.comtrans.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Px;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.adapters.ProtectorAdapter;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.ProtectorItem;
import ru.comtrans.singlets.InfoBlockHelper;

/**
 * Created by Artco on 10.10.2016.
 */

public class ProtectorView extends RelativeLayout {
    private int resourceId;
    private Context context;
    private InfoBlockHelper helper;
    private ArrayList<ProtectorItem> items;
    private boolean isEditable;


    public ProtectorView(Context context,int resource,ArrayList<ProtectorItem> items) {
        super(context);
        this.context = context;
        this.resourceId = resource;
        this.items = items;
        init();
    }

    public ProtectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ProtectorView, 0, 0);

        int resId = a.getResourceId(R.styleable.ProtectorView_pvResId,0);
        if(resId!=0)
            resourceId = resId;
        a.recycle();
    }



    public void setItems(ArrayList<ProtectorItem> items){
        this.items = items;
        init();
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    private void init(){
        helper = InfoBlockHelper.getInstance();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.protector_view, this, true);
        RelativeLayout leftLayout = (RelativeLayout) v.findViewById (R.id.left_layout);
        RelativeLayout rightLayout = (RelativeLayout) v.findViewById (R.id.right_layout);
        ImageView image = (ImageView)v.findViewById(R.id.tire_scheme_image);
        ListItem tireSchemeItem = helper.getTireSchemeValue();
        int schemeId = tireSchemeItem.getTireSchemeId();
        setResourceId(schemeId);



            if(items!=null) {
                ArrayList<ProtectorItem> protectorItems = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {

                    for (int j = 0; j < tireSchemeItem.getProtectorValues().size(); j++) {
                        if ((items.get(i).getCode() != null && items.get(i).getCode().equals(tireSchemeItem.getProtectorValues().get(j)))
                                ) {
                            protectorItems.add(items.get(i));
                        }
                    }
                }

                if(resourceId!=0){
                    Log.d("TAG","resource id "+resourceId);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),resourceId);
                    image.setImageResource(resourceId);

                    RelativeLayout.LayoutParams paramsForLeftLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,bitmap.getHeight());
                    paramsForLeftLayout.addRule(RelativeLayout.LEFT_OF,R.id.tire_scheme_image);

                    RelativeLayout.LayoutParams paramsForRightLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,bitmap.getHeight());
                    paramsForRightLayout.addRule(RelativeLayout.RIGHT_OF,R.id.tire_scheme_image);

                    leftLayout.setLayoutParams(paramsForLeftLayout);
                    rightLayout.setLayoutParams(paramsForRightLayout);

                    int height = bitmap.getHeight();
                    int etHeight = (int) (height*0.11);
                    int topMargin = (int) (height*0.07);
                    int wheelMargin = (int) (height*0.05);
                    int thirdAxisMargin = etHeight+topMargin+wheelMargin;
                    int fourthAxisMargin = thirdAxisMargin+etHeight+wheelMargin;
                    int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());
                    RelativeLayout.LayoutParams params;
                    TextView editText;



                    for(int i=0; i<protectorItems.size(); i++) {
                        ProtectorItem item = protectorItems.get(i);
                        if(isEditable){
                            editText = new EditText(context);
                        }else {
                           editText = new TextView(context);
                        }

                        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, etHeight);
                        InfoBlockTextWatcher textWatcher = new InfoBlockTextWatcher();
                        editText.addTextChangedListener(textWatcher);
                        textWatcher.updatePosition(i);
                        editText.setGravity(Gravity.CENTER);
                        editText.setHint(item.getTitle());
                        editText.setText(item.getValue());
                        editText.setMaxLines(1);
                        editText.setSingleLine();
                        Log.d("TAG","hint "+item.getTitle());
                        switch (item.getCode()){
                            case "shin_axis11":
                                params.setMargins(margin, topMargin, margin, 0);
                                editText.setLayoutParams(params);
                                leftLayout.addView(editText);
                                break;
                            case "shin_axis12":
                                params.setMargins(margin, topMargin, margin, 0);
                                editText.setLayoutParams(params);
                                rightLayout.addView(editText);
                                break;
                            case "shin_axis21":
                                params.setMargins(margin, 0, margin, topMargin);
                                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                editText.setLayoutParams(params);
                                leftLayout.addView(editText);
                                break;
                            case "shin_axis22":
                                params.setMargins(margin, 0, margin, topMargin);
                                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                editText.setLayoutParams(params);
                                rightLayout.addView(editText);
                                break;
                            case "shin_axis31":

                                    params.setMargins(margin, 0, margin, thirdAxisMargin);
                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(params);
                                    leftLayout.addView(editText);



                                break;
                            case "shin_axis32":

                                    params.setMargins(margin, 0, margin, thirdAxisMargin);
                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(params);
                                    rightLayout.addView(editText);


                                break;
                            case "shin_axis41":
                                if(schemeId==6||schemeId==8||schemeId==9||schemeId==11||schemeId==12){
                                    params.setMargins(margin, thirdAxisMargin, margin, 0);
                                    editText.setLayoutParams(params);
                                    leftLayout.addView(editText);
                                }else {
                                    params.setMargins(margin, 0, margin, fourthAxisMargin);
                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(params);
                                    leftLayout.addView(editText);
                                }

                                break;
                            case "shin_axis42":
                                if(schemeId==6||schemeId==8||schemeId==9||schemeId==11||schemeId==12){
                                    params.setMargins(margin, thirdAxisMargin, margin, 0);
                                    editText.setLayoutParams(params);
                                    rightLayout.addView(editText);
                                }else {
                                    params.setMargins(margin, 0, margin, fourthAxisMargin);
                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(params);
                                    rightLayout.addView(editText);
                                }

                                break;
                            case "shin_axis51":
                                params.setMargins(margin, thirdAxisMargin, margin, 0);
                                editText.setLayoutParams(params);
                                leftLayout.addView(editText);
                                break;
                            case "shin_axis52":
                                params.setMargins(margin, thirdAxisMargin, margin, 0);
                                editText.setLayoutParams(params);
                                rightLayout.addView(editText);
                                break;




                        }

                    }

                }else {
                    v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                }
            }else {
            }









    }

    private class InfoBlockTextWatcher implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            items.get(position).setValue(charSequence.toString());
            helper.saveProtector(items);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public void setResourceId(int tireSchemeId){
        switch (tireSchemeId) {
            case 1:
                resourceId = R.drawable.s4x2;
                break;
            case 2:
                resourceId = R.drawable.s4x4;
                break;
            case 3:
                resourceId = R.drawable.s6x2;
                break;
            case 4:
                resourceId = R.drawable.s6x4;
                break;
            case 5:
                resourceId = R.drawable.s6x6;
                break;
            case 6:
                resourceId = R.drawable.s8x2;
                break;
            case 7:
                resourceId = R.drawable.s8x2x4;
                break;
            case 8:
                resourceId = R.drawable.s8x2x6;
                break;
            case 9:
                resourceId = R.drawable.s8x4;
                break;
            case 10:
                resourceId = R.drawable.s8x4x4;
                break;
            case 11:
                resourceId = R.drawable.s8x6;
                break;
            case 12:
                resourceId = R.drawable.s8x8;
                break;
            case 13:
                resourceId = R.drawable.s10x4x6;
                break;
            default:
                resourceId = 0;
                break;

        }

    }













}
