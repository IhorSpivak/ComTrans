package ru.comtrans.adapters;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.items.ProtectorItem;
import ru.comtrans.singlets.InfoBlockHelper;

/**
 * Created by Artco on 10.08.2016.
 */
public class ProtectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ProtectorItem> items;
    private Context context;
    private InfoBlockHelper helper;
    private int page,adapterPosition;
    private boolean isEditable;

    public ProtectorAdapter(ArrayList<ProtectorItem> items, Context context, int page, int position,boolean isEditable){
        this.items = items;
        this.context = context;
        this.page = page;
        this.adapterPosition = position;
        this.isEditable = isEditable;
        helper = InfoBlockHelper.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    private static class ProtectorViewHolder extends RecyclerView.ViewHolder{
        public TextInputEditText editText;
        public TextInputLayout textInputLayout;
        public InfoBlockTextWatcher textWatcher;


        public ProtectorViewHolder(View itemView,InfoBlockTextWatcher textWatcher) {
            super(itemView);
            textInputLayout = (TextInputLayout)itemView.findViewById(R.id.text_input_layout);
            editText = (TextInputEditText) itemView.findViewById(R.id.edit_text);
            this.textWatcher = textWatcher;
            this.editText.addTextChangedListener(textWatcher);
        }

    }

    private static class NonEditableViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView tvText;

        public NonEditableViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_title);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);

        }

    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        public TextView tvHeader;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.tv_header);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case ProtectorItem.TYPE_PROTECTOR:
                if(isEditable) {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.edit_text_item, parent, false);

                    return new ProtectorViewHolder(v, new InfoBlockTextWatcher());
                }else {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.non_editable_item, parent, false);

                    return new NonEditableViewHolder(v);
                }
            case ProtectorItem.TYPE_HEADER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);

                return new HeaderViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);

                return new HeaderViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ProtectorItem item = getItem(position);


        switch (getItemViewType(position)){
            case ProtectorItem.TYPE_PROTECTOR:
                if(isEditable) {
                    ProtectorViewHolder protectorViewHolder = ((ProtectorViewHolder) holder);
                    protectorViewHolder.editText.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
                    protectorViewHolder.textInputLayout.setHint(item.getTitle());
                    protectorViewHolder.textWatcher.updatePosition(holder.getAdapterPosition());
                    protectorViewHolder.editText.setText(item.getValue());
                }else {
                    NonEditableViewHolder nonEditableViewHolder = ((NonEditableViewHolder) holder);
                    nonEditableViewHolder.title.setText(item.getTitle());
                    if (item.getValue() != null)
                        nonEditableViewHolder.tvText.setText(item.getValue());
                }
                break;
            case ProtectorItem.TYPE_HEADER:
                HeaderViewHolder headerViewHolder = ((HeaderViewHolder) holder);
                headerViewHolder.tvHeader.setText(item.getTitle());
                break;

        }
    }

    public ProtectorItem getItem(int position){
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
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
}
