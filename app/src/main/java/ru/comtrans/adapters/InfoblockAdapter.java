package ru.comtrans.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.PartialRegexInputFilter;
import ru.comtrans.helpers.PhoneTextWatcher;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.items.ProtectorItem;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.views.DividerItemDecoration;
import ru.comtrans.views.ProtectorView;


public class InfoBlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MainItem> items;
    private Context context;
    private final OnItemClickListener listener;
    private  LinearLayoutManager layoutManager;
    private int page;
    private int totalPages;
    private boolean isEditable;
    private DividerItemDecoration decoration;
    private InfoBlockHelper infoBlockHelper;


    /**
     * Interface, that allows us to have OnItemClickListener for recyclerView
     */
    public interface OnItemClickListener {
        void onItemClick(MainItem item, View view, int position);

        void saveState();
    }

    public InfoBlockAdapter(Context context, ArrayList<MainItem> items, int page, int totalPages, boolean isEditable, OnItemClickListener listener,LinearLayoutManager manager) {
        this.context = context;
        this.layoutManager = manager;
        this.items = items;
        this.listener = listener;
        this.page = page;
        this.isEditable = isEditable;
        this.totalPages = totalPages;
        infoBlockHelper = InfoBlockHelper.getInstance();
    }

    public ArrayList<MainItem> getItems() {
        return items;
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder {

        public CustomViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final MainItem item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, v, getAdapterPosition());
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case MainItem.TYPE_LIST:
                if (isEditable) {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item, parent, false);

                    return new ListViewHolder(v);
                } else {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.non_editable_item, parent, false);

                    return new NonEditableViewHolder(v);
                }
            case MainItem.TYPE_EMAIL:
            case MainItem.TYPE_PHONE:
            case MainItem.TYPE_STRING:
            case MainItem.TYPE_NUMBER:
                if (isEditable) {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.edit_text_item, parent, false);

                    return new EditTextViewHolder(v, new InfoBlockTextWatcher());
                } else {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.non_editable_item, parent, false);

                    return new NonEditableViewHolder(v);
                }
            case MainItem.TYPE_HEADER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);

                return new HeaderViewHolder(v);

            case MainItem.TYPE_FLAG:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.flag_item, parent, false);

                return new FlagViewHolder(v);


            case MainItem.TYPE_PHOTO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_container_recycler_view, parent, false);

                return new PhotoViewHolder(v);

            case MainItem.TYPE_VIDEO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.video_container_recycler_view, parent, false);

                return new PhotoViewHolder(v);

            case MainItem.TYPE_TIRE_SCHEME:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tire_scheme_item, parent, false);

                return new TireSchemeViewHolder(v);

            case MainItem.TYPE_BOTTOM_BAR:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bottom_bar_list_item, parent, false);

                return new BottomBarViewHolder(v);

            case MainItem.TYPE_CALENDAR:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.calendar_item, parent, false);

                return new CalendarViewHolder(v);


            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);

                return new HeaderViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final MainItem item = getItem(position);
        final PhotoContainerAdapter adapter;
        PhotoContainerAdapter defectsAdapter;

        int count;

        switch (getItemViewType(position)) {
            case MainItem.TYPE_LIST:
                if (isEditable) {
                    ListViewHolder listViewHolder = ((ListViewHolder) viewHolder);
                    if (item.isRequired())
                        listViewHolder.title.setText(item.getName() + "*");
                    else
                        listViewHolder.title.setText(item.getName());
                    if (item.getListValue() != null)
                        listViewHolder.tvList.setText(item.getListValue().getName());
                } else {
                    NonEditableViewHolder nonEditableViewHolder = ((NonEditableViewHolder) viewHolder);
                    nonEditableViewHolder.title.setText(item.getName());
                    if (item.getListValue() != null)
                        nonEditableViewHolder.tvText.setText(item.getListValue().getName());
                }
                break;


            case MainItem.TYPE_CALENDAR:
                final CalendarViewHolder calendarViewHolder = (CalendarViewHolder) viewHolder;
                if (item.isRequired())
                    calendarViewHolder.title.setText(item.getName() + "*");
                else
                    calendarViewHolder.title.setText(item.getName());
                if (item.getValue() != null && !item.getValue().equals("")) {
                    calendarViewHolder.picker.setText(item.getValue());
                } else {
                    calendarViewHolder.picker.setText(R.string.choose_date);
                }
                calendarViewHolder.picker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(item, v, calendarViewHolder.getAdapterPosition());
                    }
                });
                break;

            case MainItem.TYPE_PHOTO:
                PhotoViewHolder photoViewHolder = ((PhotoViewHolder) viewHolder);
                ArrayList<PhotoItem> photoItems = new ArrayList<>();
                ArrayList<PhotoItem> defects = new ArrayList<>();

                for (PhotoItem photoItem : item.getPhotoItems()) {
                    if (!photoItem.isDefect()) {
                        photoItems.add(photoItem);
                    }
                }

                final LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                manager.setAutoMeasureEnabled(true);
                photoViewHolder.photoList.setLayoutManager(manager);
                adapter = new PhotoContainerAdapter(context, photoItems, new PhotoContainerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PhotoItem item, View view) {
                        MainItem mainItem = getItem(viewHolder.getAdapterPosition());
                        listener.onItemClick(mainItem, view, mainItem.getPhotoItems().indexOf(item));
                    }
                }, MainItem.TYPE_PHOTO);
                photoViewHolder.photoList.setAdapter(adapter);

                count = 0;
                for (PhotoItem photoItem : item.getPhotoItems()) {
                    if (photoItem.getImagePath() != null && !photoItem.isDefect())
                        count++;
                }
                if (count != 0) {
                    int percent = (int) ((count * 100.0f) / item.getPhotosCount());
                    if (percent == 100) {
                        photoViewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_green));
                    } else {
                        photoViewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
                    }

                    photoViewHolder.progressBar.setProgress(percent);
                } else {
                    photoViewHolder.progressBar.setProgress(0);
                }

                for (PhotoItem photoItem : item.getPhotoItems()) {
                    if (photoItem.isDefect()) {
                        defects.add(photoItem);
                    }
                }

                if (defects.size() > 0) {
                    LinearLayoutManager defectsManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    photoViewHolder.defectsList.setLayoutManager(defectsManager);
                    defectsAdapter = new PhotoContainerAdapter(context, defects, new PhotoContainerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(PhotoItem item, View view) {
                            MainItem mainItem = getItem(viewHolder.getAdapterPosition());
                            listener.onItemClick(mainItem, view, mainItem.getPhotoItems().indexOf(item));
                        }
                    }, MainItem.TYPE_PHOTO);
                    photoViewHolder.defectsList.setAdapter(defectsAdapter);
                }

                break;

            case MainItem.TYPE_VIDEO:
                PhotoViewHolder videoViewHolder = ((PhotoViewHolder) viewHolder);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
                videoViewHolder.photoList.setLayoutManager(gridLayoutManager);
                adapter = new PhotoContainerAdapter(context, item.getPhotoItems(), new PhotoContainerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PhotoItem item, View view) {
                        MainItem mainItem = getItem(viewHolder.getAdapterPosition());
                        listener.onItemClick(mainItem, view, mainItem.getPhotoItems().indexOf(item));
                    }
                }, MainItem.TYPE_VIDEO);
                videoViewHolder.photoList.setAdapter(adapter);
                if (decoration == null) {
                    decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
                    videoViewHolder.photoList.addItemDecoration(decoration);
                }

                count = 0;
                for (PhotoItem photoItem : item.getPhotoItems()) {
                    if (photoItem.getImagePath() != null && !photoItem.isDefect())
                        count++;
                }
                if (count != 0) {
                    int percent = (int) ((count * 100.0f) / item.getPhotosCount());
                    if (percent == 100) {
                        videoViewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_green));
                    } else {
                        videoViewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
                    }

                    videoViewHolder.progressBar.setProgress(percent);
                } else {
                    videoViewHolder.progressBar.setProgress(0);
                }
                break;

            case MainItem.TYPE_STRING:
                if (isEditable) {
                    final EditTextViewHolder editTextViewHolder = ((EditTextViewHolder) viewHolder);
                    editTextViewHolder.editText.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);



                    if (item.isRequired())
                        editTextViewHolder.textInputLayout.setHint(item.getName() + "*");
                    else
                        editTextViewHolder.textInputLayout.setHint(item.getName());
                    editTextViewHolder.textWatcher.updatePosition(viewHolder.getAdapterPosition());
                    editTextViewHolder.editText.setText(item.getValue());
                    editTextViewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                listener.saveState();
                            }
                        }
                    });
                } else {
                    NonEditableViewHolder nonEditableViewHolder = ((NonEditableViewHolder) viewHolder);
                    nonEditableViewHolder.title.setText(item.getName());
                    if (item.getValue() != null)
                        nonEditableViewHolder.tvText.setText(item.getValue());
                }
                break;

            case MainItem.TYPE_NUMBER:
                if (isEditable) {
                    final EditTextViewHolder editTextViewHolder = ((EditTextViewHolder) viewHolder);


                    if (item.isRequired())
                        editTextViewHolder.textInputLayout.setHint(item.getName() + "*");
                    else
                        editTextViewHolder.textInputLayout.setHint(item.getName());
                    editTextViewHolder.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editTextViewHolder.textWatcher.updatePosition(viewHolder.getAdapterPosition());
                    editTextViewHolder.editText.setText(item.getValue());
                    editTextViewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                listener.saveState();
                            }
                        }
                    });
                } else {
                    NonEditableViewHolder nonEditableViewHolder = ((NonEditableViewHolder) viewHolder);
                    nonEditableViewHolder.title.setText(item.getName());
                    if (item.getValue() != null)
                        nonEditableViewHolder.tvText.setText(item.getValue());
                }
                break;
            case MainItem.TYPE_EMAIL:
                if (isEditable) {
                    final EditTextViewHolder editTextViewHolder = ((EditTextViewHolder) viewHolder);
                    if (item.isRequired())
                        editTextViewHolder.textInputLayout.setHint(item.getName() + "*");
                    else
                        editTextViewHolder.textInputLayout.setHint(item.getName());
                    editTextViewHolder.editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    editTextViewHolder.textWatcher.updatePosition(viewHolder.getAdapterPosition());
                    editTextViewHolder.editText.setText(item.getValue());
                    editTextViewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                listener.saveState();
                            }
                        }
                    });
                    editTextViewHolder.editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (!Utility.isEmailValid(editable) && !editable.toString().trim().equals("")) {
                                editTextViewHolder.textInputLayout.setErrorEnabled(true);
                                editTextViewHolder.textInputLayout.setError("Некорректный e-mail");
                                // Для проверки на валидность
                                item.setChecked(false);
                            } else {
                                editTextViewHolder.textInputLayout.setErrorEnabled(false);
                                item.setChecked(true);
                            }

                        }
                    });
                } else {
                    NonEditableViewHolder nonEditableViewHolder = ((NonEditableViewHolder) viewHolder);
                    nonEditableViewHolder.title.setText(item.getName());
                    if (item.getValue() != null)
                        nonEditableViewHolder.tvText.setText(item.getValue());
                }
                break;

            case MainItem.TYPE_PHONE:
                if (isEditable) {
                    final EditTextViewHolder editTextViewHolder = ((EditTextViewHolder) viewHolder);
                    if (item.isRequired())
                        editTextViewHolder.textInputLayout.setHint(item.getName() + "*");
                    else
                        editTextViewHolder.textInputLayout.setHint(item.getName());
                    editTextViewHolder.editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    editTextViewHolder.textWatcher.updatePosition(viewHolder.getAdapterPosition());
                    editTextViewHolder.editText.setText(item.getValue());
                    editTextViewHolder.editText.setKeyListener(DigitsKeyListener.getInstance("0123456789+-()"));


                    editTextViewHolder.editText.setFilters(new InputFilter[]{new PartialRegexInputFilter(Const.phone_regex)});
                    editTextViewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                listener.saveState();
                            }
                            if (hasFocus && editTextViewHolder.editText.getText().toString().length() == 0) {
                                editTextViewHolder.editText.setText(context.getString(R.string.phone_prefix_bracket));
                                editTextViewHolder.editText.setSelection(editTextViewHolder.editText.getText().length());
                            }
                        }
                    });
                    editTextViewHolder.editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (!Utility.isFieldValid(Const.phone_regex, editable) && !editable.toString().trim().equals(context.getString(R.string.phone_prefix_bracket))) {
                                editTextViewHolder.textInputLayout.setErrorEnabled(true);
                                editTextViewHolder.textInputLayout.setError("Некорректный телефон");
                                // Для проверки на валидность
                                item.setChecked(false);
                            } else {
                                editTextViewHolder.textInputLayout.setErrorEnabled(false);
                                item.setChecked(true);
                            }

                        }
                    });

                    editTextViewHolder.editText.addTextChangedListener(new PhoneTextWatcher(editTextViewHolder.editText, context));
                } else {
                    NonEditableViewHolder nonEditableViewHolder = ((NonEditableViewHolder) viewHolder);
                    nonEditableViewHolder.title.setText(item.getName());
                    if (item.getValue() != null)
                        nonEditableViewHolder.tvText.setText(item.getValue());
                }
                break;


            case MainItem.TYPE_HEADER:
                HeaderViewHolder headerViewHolder = ((HeaderViewHolder) viewHolder);
                headerViewHolder.tvHeader.setText(item.getName());
                break;

            case MainItem.TYPE_FLAG:
                FlagViewHolder flagViewHolder = ((FlagViewHolder) viewHolder);
                flagViewHolder.checkBox.setOnCheckedChangeListener(null);
                if (item.isRequired())
                    flagViewHolder.checkBox.setText(item.getName() + "*");
                else
                    flagViewHolder.checkBox.setText(item.getName());
                flagViewHolder.checkBox.setChecked(item.isChecked());
                if (isEditable) {
                    flagViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            listener.saveState();
                            item.setChecked(b);
                        }
                    });
                    flagViewHolder.checkBox.setClickable(true);
                } else {
                    flagViewHolder.checkBox.setClickable(false);
                }
                break;

            case MainItem.TYPE_BOTTOM_BAR:
                BottomBarViewHolder bottomBarViewHolder = ((BottomBarViewHolder) viewHolder);
                if (page == 0) {
                    bottomBarViewHolder.previousLayout.setVisibility(View.GONE);
                } else if (page + 1 == totalPages) {
                    bottomBarViewHolder.btnNext.setText(R.string.save_infoblock);
                }

                bottomBarViewHolder.btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (isEditable) {
                            listener.saveState();
                            boolean isAllEntered = true;
                            boolean isMainEntered = true;


                            for (MainItem item :
                                    items) {

//                               if (item.getCode() != null && (item.getCode().equals("general_marka")
//                                       || item.getCode().equals("general_model")
//                                       || item.getCode().equals("general_number_gos")
//                                       || item.getCode().equals("general_year"))) {
                                if (item.isRequired()) {
                                    switch (item.getType()) {
                                        case MainItem.TYPE_LIST:
                                            if (item.getListValue().getId() == -1) {
                                                isMainEntered = false;
                                                break;
                                            }
                                            break;
                                        case MainItem.TYPE_CALENDAR:
                                            if (item.getValue() == null || item.getValue().equals(R.string.choose_date)) {
                                                isMainEntered = false;
                                            }
                                            break;
                                        case MainItem.TYPE_NUMBER:
                                        case MainItem.TYPE_STRING:
                                            if (item.getValue() == null || item.getValue().equals("")) {
                                                isMainEntered = false;
                                            }
                                            break;
                                        case MainItem.TYPE_PHONE:
                                        case MainItem.TYPE_EMAIL:
                                            if (item.getValue() == null || !item.isChecked()) {
                                                isMainEntered = false;
                                            }
                                            break;

                                    }

                                }

                                switch (item.getType()) {
                                    case MainItem.TYPE_CALENDAR:
                                        if (item.getValue() == null || item.getValue().equals(R.string.choose_date)) {
                                            isAllEntered = false;
                                        }
                                        break;
                                    case MainItem.TYPE_NUMBER:
                                    case MainItem.TYPE_STRING:
                                        if (item.getValue() == null || item.getValue().equals("")) {
                                            isAllEntered = false;
                                        }
                                        break;
                                    case MainItem.TYPE_PHONE:
                                    case MainItem.TYPE_EMAIL:
                                        if (item.getValue() == null || !item.isChecked()) {
                                            isAllEntered = false;
                                        }
                                        break;
                                    case MainItem.TYPE_LIST:
                                        if (item.getListValue().getId() == -1) {
                                            isAllEntered = false;
                                        }
                                        break;

                                }
                            }
                            if (!isMainEntered) {
                                new AlertDialog.Builder(context)
                                        .setCancelable(true)
                                        .setTitle(R.string.warning_title)
                                        .setMessage(R.string.required_warning_message)
                                        .setNeutralButton(R.string.btn_stay, null).show();
                            } else if (!isAllEntered && page + 1 != totalPages) {
                                new AlertDialog.Builder(context)
                                        .setCancelable(true)
                                        .setTitle(R.string.warning_title)
                                        .setMessage(R.string.warning_message)
                                        .setPositiveButton(R.string.btn_next, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                infoBlockHelper.saveScreen(items, page);
                                                listener.onItemClick(item, v, 2);
                                            }
                                        })
                                        .setNeutralButton(R.string.btn_stay, null).show();
                            } else {
                                infoBlockHelper.saveScreen(items, page);
                                listener.onItemClick(item, v, 2);
                            }
                        } else {
                            listener.onItemClick(item, v, 2);
                        }

                    }
                });
                bottomBarViewHolder.btnPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        infoBlockHelper.saveScreen(items, page);
                        listener.onItemClick(item, view, 1);
                    }
                });
                break;

            case MainItem.TYPE_TIRE_SCHEME:
                TireSchemeViewHolder tireSchemeViewHolder = ((TireSchemeViewHolder) viewHolder);
                Log.d("TAG","is null"+item.getProtectorItems());
                tireSchemeViewHolder.protectorView.setEditable(isEditable);
                tireSchemeViewHolder.protectorView.setItems(item.getProtectorItems());
                break;



        }
        ((CustomViewHolder) viewHolder).bind(item, listener);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public MainItem getItem(int position) {
        return items.get(position);
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
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    private static class ListViewHolder extends CustomViewHolder {
        public TextView title;
        public TextView tvList;

        public ListViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_title);
            tvList = (TextView) itemView.findViewById(R.id.tv_list);

        }

    }

    private static class TireSchemeViewHolder extends CustomViewHolder {
        public ProtectorView protectorView;


        public TireSchemeViewHolder(View itemView) {
            super(itemView);
            protectorView = (ProtectorView) itemView;


        }

    }

    private static class BottomBarViewHolder extends CustomViewHolder {
        public Button btnPrevious, btnNext;
        public RelativeLayout previousLayout, nextLayout;


        public BottomBarViewHolder(View itemView) {
            super(itemView);
            btnNext = (Button) itemView.findViewById(R.id.btn_next);
            btnPrevious = (Button) itemView.findViewById(R.id.btn_previous);
            previousLayout = (RelativeLayout) itemView.findViewById(R.id.previous_layout);
            nextLayout = (RelativeLayout) itemView.findViewById(R.id.next_layout);
        }

    }

    private static class EditTextViewHolder extends CustomViewHolder {
        public TextInputEditText editText;
        public TextInputLayout textInputLayout;
        public InfoBlockTextWatcher textWatcher;


        public EditTextViewHolder(View itemView, InfoBlockTextWatcher textWatcher) {
            super(itemView);
            textInputLayout = (TextInputLayout) itemView.findViewById(R.id.text_input_layout);
            editText = (TextInputEditText) itemView.findViewById(R.id.edit_text);
           editText.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                Utility.hideKeyboard(v.getContext(),v);
                                ((EditText)v).clearFocus();
                                return true;
                            }
                            return false;
                        }
                    });
            editText.setFilters( new InputFilter[] { new InputFilter.LengthFilter(itemView.getContext().getResources().getInteger(R.integer.max_length)) } );
            this.textWatcher = textWatcher;
            this.editText.addTextChangedListener(textWatcher);
        }

    }

    private static class HeaderViewHolder extends CustomViewHolder {
        public TextView tvHeader;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.tv_header);
        }

    }

    private static class FlagViewHolder extends CustomViewHolder {
        public CheckBox checkBox;


        public FlagViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);
        }

    }


    private static class NonEditableViewHolder extends CustomViewHolder {
        public TextView title;
        public TextView tvText;

        public NonEditableViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_title);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);

        }

    }

    private static class CalendarViewHolder extends CustomViewHolder {
        public Button picker;
        public TextView title;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            picker = (Button) itemView.findViewById(R.id.btn_picker);
            title = (TextView) itemView.findViewById(R.id.title);
        }

    }

    private static class PhotoViewHolder extends CustomViewHolder {
        public RecyclerView photoList;
        public RecyclerView defectsList;
        public ProgressBar progressBar;


        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoList = (RecyclerView) itemView.findViewById(R.id.list_photo);
            defectsList = (RecyclerView) itemView.findViewById(R.id.list_defects);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }


    }
}
