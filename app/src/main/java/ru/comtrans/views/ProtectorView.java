package ru.comtrans.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;
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

    private TextView createEditText(boolean isEditable,ProtectorItem item,int position){

        TextView editText;
        if(isEditable){
            editText = new EditText(context);
        }else {
            editText = new TextView(context);
        }


        InfoBlockTextWatcher textWatcher = new InfoBlockTextWatcher();
        editText.addTextChangedListener(textWatcher);
        textWatcher.updatePosition(position);
        editText.setGravity(Gravity.CENTER);
        editText.setHint(item.getTitle());
        editText.setText(item.getValue());
        editText.setMaxLines(1);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        editText.setSingleLine();


        return editText;
    }

    private void init(){
        helper = InfoBlockHelper.getInstance();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.protector_view, this, true);
        final RelativeLayout leftLayout = (RelativeLayout) v.findViewById (R.id.left_layout);
        final RelativeLayout rightLayout = (RelativeLayout) v.findViewById (R.id.right_layout);
        ImageView image = (ImageView)v.findViewById(R.id.tire_scheme_image);
        ListItem tireSchemeItem = helper.getTireSchemeValue();
        int schemeId = tireSchemeItem.getTireSchemeId();
        setResourceId(schemeId);

//         OnKeyListener keyListener = new OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                    if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                        switch ((String)v.getTag()) {
//                            case "shin_axis11":
//                                for (int i = 0; i < rightLayout.getChildCount(); i++) {
//                                    View view = rightLayout.getChildAt(i);
//                                    if(view instanceof EditText &&view.getTag().equals("shin_axis12")){
//                                        ((EditText)view).requestFocus();
//                                    }
//                                }
//                                break;
//                        }
//                        return true;
//                    }
//
//                return false;
//            }
//        };




            if(items!=null&&tireSchemeItem.getProtectorValues()!=null) {
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
                    int etHeight = (int) (height*0.14);
                    int topMargin = (int) (height*0.0652);
                    int wheelMargin = (int) (height*0.0489);
                    int halfWheelMargin = wheelMargin/2;

                    int bottomSecondMargin = topMargin+halfWheelMargin+etHeight;
                    int bottomThirdMargin = bottomSecondMargin+halfWheelMargin+etHeight;


                    int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());

                        if(schemeId==1||schemeId==2) {
                            LinearLayout secondAxisLeftLayout = new LinearLayout(context);
                            secondAxisLeftLayout.setTag("2Left");
                            secondAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                            RelativeLayout.LayoutParams secondAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                            secondAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            secondAxisLeftLayout.setLayoutParams(secondAxisLeftParams);
                            secondAxisLeftParams.setMargins(0,0,0,topMargin);

                            LinearLayout secondAxisRightLayout = new LinearLayout(context);
                            secondAxisRightLayout.setTag("2Right");
                            secondAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                            RelativeLayout.LayoutParams secondAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                            secondAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            secondAxisRightLayout.setLayoutParams(secondAxisLeftParams);
                            secondAxisRightParams.setMargins(0,0,0,topMargin);

                            for(int i=0; i<protectorItems.size(); i++) {
                                ProtectorItem item = protectorItems.get(i);
                                TextView editText = createEditText(isEditable,item,i);
                                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0,etHeight);
                                linearParams.weight = 1;

                                switch (item.getCode()) {
                                    case "shin_axis11":
                                        relativeParams.setMargins(margin, topMargin, margin, 0);
                                        editText.setLayoutParams(relativeParams);
                                        editText.setTag("shin_axis11");
                                        leftLayout.addView(editText);


                                        break;
                                    case "shin_axis12":
                                        relativeParams.setMargins(margin, topMargin, margin, 0);
                                        editText.setLayoutParams(relativeParams);
                                        editText.setTag("shin_axis12");
                                        rightLayout.addView(editText);
                                        break;
                                    case "shin_axis21":
                                        editText.setTag("shin_axis21");
                                        linearParams.setMargins(margin, 0, margin,0);
                                        editText.setLayoutParams(linearParams);
                                        secondAxisLeftLayout.addView(editText);
                                        break;
                                    case "shin_axis22":
                                        editText.setTag("shin_axis22");
                                        linearParams.setMargins(margin, 0, margin,0);
                                        editText.setLayoutParams(linearParams);
                                        secondAxisLeftLayout.addView(editText);

                                        break;
                                    case "shin_axis23":
                                        editText.setTag("shin_axis23");
                                        linearParams.setMargins(margin, 0, margin,0);
                                        editText.setLayoutParams(linearParams);
                                        secondAxisRightLayout.addView(editText);
                                        break;
                                    case "shin_axis24":
                                        editText.setTag("shin_axis24");
                                        linearParams.setMargins(margin, 0, margin,0);
                                        editText.setLayoutParams(linearParams);
                                        secondAxisRightLayout.addView(editText);
                                        break;
                                }

                            }
                            leftLayout.addView(secondAxisLeftLayout);
                            rightLayout.addView(secondAxisRightLayout);

                        }

                    if(schemeId==3||schemeId==4||schemeId==5) {
                        LinearLayout secondAxisLeftLayout = new LinearLayout(context);
                        secondAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams secondAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        secondAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        secondAxisLeftLayout.setLayoutParams(secondAxisLeftParams);
                        secondAxisLeftParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout secondAxisRightLayout = new LinearLayout(context);
                        secondAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams secondAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        secondAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        secondAxisRightLayout.setLayoutParams(secondAxisLeftParams);
                        secondAxisRightParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout thirdAxisLeftLayout = new LinearLayout(context);
                        thirdAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisLeftLayout.setLayoutParams(thirdAxisLeftParams);
                        thirdAxisLeftParams.setMargins(0,0,0,topMargin);

                        LinearLayout thirdAxisRightLayout = new LinearLayout(context);
                        thirdAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisRightLayout.setLayoutParams(thirdAxisRightParams);
                        thirdAxisRightParams.setMargins(0,0,0,topMargin);


                        for(int i=0; i<protectorItems.size(); i++) {
                            ProtectorItem item = protectorItems.get(i);
                            TextView editText = createEditText(isEditable,item,i);
                            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0,etHeight);
                            linearParams.weight = 1;

                            switch (item.getCode()) {
                                case "shin_axis11":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis12":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis21":
                                case "shin_axis22":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    secondAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis23":
                                case "shin_axis24":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    secondAxisRightLayout.addView(editText);
                                    break;
                                case "shin_axis31":
                                case "shin_axis32":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis33":
                                case "shin_axis34":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisRightLayout.addView(editText);
                                    break;
                            }

                        }
                        leftLayout.addView(secondAxisLeftLayout);
                        leftLayout.addView(thirdAxisLeftLayout);
                        rightLayout.addView(secondAxisRightLayout);
                        rightLayout.addView(thirdAxisRightLayout);


                    }

                    if(schemeId==6||schemeId==9||schemeId==11||schemeId==12){


                        LinearLayout thirdAxisLeftLayout = new LinearLayout(context);
                        thirdAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisLeftLayout.setLayoutParams(thirdAxisLeftParams);
                        thirdAxisLeftParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout thirdAxisRightLayout = new LinearLayout(context);
                        thirdAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisRightLayout.setLayoutParams(thirdAxisRightParams);
                        thirdAxisRightParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout fourthAxisLeftLayout = new LinearLayout(context);
                        fourthAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisLeftLayout.setLayoutParams(fourthAxisLeftParams);
                        fourthAxisLeftParams.setMargins(0,0,0,topMargin);

                        LinearLayout fourthAxisRightLayout = new LinearLayout(context);
                        fourthAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisRightLayout.setLayoutParams(fourthAxisRightParams);
                        fourthAxisRightParams.setMargins(0,0,0,topMargin);


                        for(int i=0; i<protectorItems.size(); i++) {
                            ProtectorItem item = protectorItems.get(i);
                            TextView editText = createEditText(isEditable,item,i);
                            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0,etHeight);
                            linearParams.weight = 1;

                            switch (item.getCode()) {
                                case "shin_axis11":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis12":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis21":
                                    relativeParams.setMargins(margin, bottomSecondMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis22":
                                    relativeParams.setMargins(margin, bottomSecondMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis31":
                                case "shin_axis32":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis33":
                                case "shin_axis34":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisRightLayout.addView(editText);
                                    break;
                                case "shin_axis41":
                                case "shin_axis42":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    fourthAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis43":
                                case "shin_axis44":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    fourthAxisRightLayout.addView(editText);
                                    break;
                            }

                        }
                        leftLayout.addView(fourthAxisLeftLayout);
                        leftLayout.addView(thirdAxisLeftLayout);

                        rightLayout.addView(fourthAxisRightLayout);
                        rightLayout.addView(thirdAxisRightLayout);

                    }

                    if(schemeId==8){


                        LinearLayout thirdAxisLeftLayout = new LinearLayout(context);
                        thirdAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisLeftLayout.setLayoutParams(thirdAxisLeftParams);
                        thirdAxisLeftParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout thirdAxisRightLayout = new LinearLayout(context);
                        thirdAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisRightLayout.setLayoutParams(thirdAxisRightParams);
                        thirdAxisRightParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout fourthAxisLeftLayout = new LinearLayout(context);
                        fourthAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisLeftLayout.setLayoutParams(fourthAxisLeftParams);
                        fourthAxisLeftParams.setMargins(0,0,0,topMargin);

                        LinearLayout fourthAxisRightLayout = new LinearLayout(context);
                        fourthAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisRightLayout.setLayoutParams(fourthAxisRightParams);
                        fourthAxisRightParams.setMargins(0,0,0,topMargin);


                        for(int i=0; i<protectorItems.size(); i++) {
                            ProtectorItem item = protectorItems.get(i);
                            TextView editText = createEditText(isEditable,item,i);
                            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0,etHeight);
                            linearParams.weight = 1;

                            switch (item.getCode()) {
                                case "shin_axis11":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis12":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis21":
                                    relativeParams.setMargins(margin, bottomSecondMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis22":
                                    relativeParams.setMargins(margin, bottomSecondMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis31":
                                case "shin_axis32":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis33":
                                case "shin_axis34":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisRightLayout.addView(editText);
                                    break;
                                case "shin_axis41":
                                    relativeParams.setMargins(margin, 0, margin, topMargin);
                                    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis42":
                                    relativeParams.setMargins(margin, 0, margin, topMargin);
                                    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;

                            }

                        }
                        leftLayout.addView(fourthAxisLeftLayout);
                        leftLayout.addView(thirdAxisLeftLayout);

                        rightLayout.addView(fourthAxisRightLayout);
                        rightLayout.addView(thirdAxisRightLayout);

                    }

                    if(schemeId==7){

                        LinearLayout thirdAxisLeftLayout = new LinearLayout(context);
                        thirdAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisLeftLayout.setLayoutParams(thirdAxisLeftParams);
                        thirdAxisLeftParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout thirdAxisRightLayout = new LinearLayout(context);
                        thirdAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisRightLayout.setLayoutParams(thirdAxisRightParams);
                        thirdAxisRightParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout fourthAxisLeftLayout = new LinearLayout(context);
                        fourthAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisLeftLayout.setLayoutParams(fourthAxisLeftParams);
                        fourthAxisLeftParams.setMargins(0,0,0,topMargin);

                        LinearLayout fourthAxisRightLayout = new LinearLayout(context);
                        fourthAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisRightLayout.setLayoutParams(fourthAxisRightParams);
                        fourthAxisRightParams.setMargins(0,0,0,topMargin);


                        for(int i=0; i<protectorItems.size(); i++) {
                            ProtectorItem item = protectorItems.get(i);
                            TextView editText = createEditText(isEditable,item,i);
                            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0,etHeight);
                            linearParams.weight = 1;

                            switch (item.getCode()) {
                                case "shin_axis11":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis12":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis21":
                                    relativeParams.setMargins(margin, 0, margin, bottomThirdMargin);
                                    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis22":
                                    relativeParams.setMargins(margin, 0, margin, bottomThirdMargin);
                                    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis31":
                                case "shin_axis32":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis33":
                                case "shin_axis34":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisRightLayout.addView(editText);
                                    break;
                                case "shin_axis41":
                                case "shin_axis42":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    fourthAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis43":
                                case "shin_axis44":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    fourthAxisRightLayout.addView(editText);
                                    break;
                            }

                        }
                        leftLayout.addView(fourthAxisLeftLayout);
                        leftLayout.addView(thirdAxisLeftLayout);

                        rightLayout.addView(fourthAxisRightLayout);
                        rightLayout.addView(thirdAxisRightLayout);

                    }

                    if(schemeId==10){

                        LinearLayout secondAxisLeftLayout = new LinearLayout(context);
                        secondAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams secondAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        secondAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        secondAxisLeftLayout.setLayoutParams(secondAxisLeftParams);
                        secondAxisLeftParams.setMargins(0,0,0,bottomThirdMargin);

                        LinearLayout secondAxisRightLayout = new LinearLayout(context);
                        secondAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams secondAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        secondAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        secondAxisRightLayout.setLayoutParams(secondAxisLeftParams);
                        secondAxisRightParams.setMargins(0,0,0,bottomThirdMargin);


                        LinearLayout thirdAxisLeftLayout = new LinearLayout(context);
                        thirdAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisLeftLayout.setLayoutParams(thirdAxisLeftParams);
                        thirdAxisLeftParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout thirdAxisRightLayout = new LinearLayout(context);
                        thirdAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisRightLayout.setLayoutParams(thirdAxisRightParams);
                        thirdAxisRightParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout fourthAxisLeftLayout = new LinearLayout(context);
                        fourthAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisLeftLayout.setLayoutParams(fourthAxisLeftParams);
                        fourthAxisLeftParams.setMargins(0,0,0,topMargin);

                        LinearLayout fourthAxisRightLayout = new LinearLayout(context);
                        fourthAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisRightLayout.setLayoutParams(fourthAxisRightParams);
                        fourthAxisRightParams.setMargins(0,0,0,topMargin);


                        for(int i=0; i<protectorItems.size(); i++) {
                            ProtectorItem item = protectorItems.get(i);
                            TextView editText = createEditText(isEditable,item,i);
                            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0,etHeight);
                            linearParams.weight = 1;

                            switch (item.getCode()) {
                                case "shin_axis11":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis12":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis21":
                                case "shin_axis22":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    secondAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis23":
                                case "shin_axis24":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    secondAxisRightLayout.addView(editText);
                                    break;
                                case "shin_axis31":
                                case "shin_axis32":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis33":
                                case "shin_axis34":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisRightLayout.addView(editText);
                                    break;
                                case "shin_axis41":
                                    relativeParams.setMargins(margin, 0, margin, topMargin);
                                    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis42":
                                    relativeParams.setMargins(margin, 0, margin, topMargin);
                                    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;


                            }

                        }
                        leftLayout.addView(fourthAxisLeftLayout);
                        leftLayout.addView(thirdAxisLeftLayout);
                        leftLayout.addView(secondAxisLeftLayout);

                        rightLayout.addView(fourthAxisRightLayout);
                        rightLayout.addView(thirdAxisRightLayout);
                        rightLayout.addView(secondAxisRightLayout);

                    }

                    if(schemeId==13){

                        LinearLayout thirdAxisLeftLayout = new LinearLayout(context);
                        thirdAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisLeftLayout.setLayoutParams(thirdAxisLeftParams);
                        thirdAxisLeftParams.setMargins(0,0,0,bottomThirdMargin);

                        LinearLayout thirdAxisRightLayout = new LinearLayout(context);
                        thirdAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams thirdAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        thirdAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        thirdAxisRightLayout.setLayoutParams(thirdAxisRightParams);
                        thirdAxisRightParams.setMargins(0,0,0,bottomThirdMargin);

                        LinearLayout fourthAxisLeftLayout = new LinearLayout(context);
                        fourthAxisLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisLeftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisLeftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisLeftLayout.setLayoutParams(fourthAxisLeftParams);
                        fourthAxisLeftParams.setMargins(0,0,0,bottomSecondMargin);

                        LinearLayout fourthAxisRightLayout = new LinearLayout(context);
                        fourthAxisRightLayout.setOrientation(LinearLayout.HORIZONTAL);
                        RelativeLayout.LayoutParams fourthAxisRightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                        fourthAxisRightParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fourthAxisRightLayout.setLayoutParams(fourthAxisRightParams);
                        fourthAxisRightParams.setMargins(0,0,0,bottomSecondMargin);




                        for(int i=0; i<protectorItems.size(); i++) {
                            ProtectorItem item = protectorItems.get(i);
                            TextView editText = createEditText(isEditable,item,i);
                            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,etHeight);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0,etHeight);
                            linearParams.weight = 1;

                            switch (item.getCode()) {
                                case "shin_axis11":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis12":
                                    relativeParams.setMargins(margin, topMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis21":
                                    relativeParams.setMargins(margin, bottomSecondMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis22":
                                    relativeParams.setMargins(margin, bottomSecondMargin, margin, 0);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;
                                case "shin_axis31":
                                case "shin_axis32":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis33":
                                case "shin_axis34":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    thirdAxisRightLayout.addView(editText);
                                    break;
                                case "shin_axis41":
                                case "shin_axis42":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    fourthAxisLeftLayout.addView(editText);
                                    break;
                                case "shin_axis43":
                                case "shin_axis44":
                                    linearParams.setMargins(margin, 0, margin,0);
                                    editText.setLayoutParams(linearParams);
                                    fourthAxisRightLayout.addView(editText);
                                    break;
                                case "shin_axis51":
                                    relativeParams.setMargins(margin, 0, margin, topMargin);
                                    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(relativeParams);
                                    leftLayout.addView(editText);
                                    break;
                                case "shin_axis52":
                                    relativeParams.setMargins(margin, 0, margin, topMargin);
                                    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    editText.setLayoutParams(relativeParams);
                                    rightLayout.addView(editText);
                                    break;



                            }

                        }
                        leftLayout.addView(fourthAxisLeftLayout);
                        leftLayout.addView(thirdAxisLeftLayout);

                        rightLayout.addView(fourthAxisRightLayout);
                        rightLayout.addView(thirdAxisRightLayout);

                    }

                }else {
                    v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
                }
            }else {
                v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
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
