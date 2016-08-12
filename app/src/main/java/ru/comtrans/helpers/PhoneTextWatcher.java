package ru.comtrans.helpers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import ru.comtrans.R;


/**
 * Created by Artco on 12.08.2016.
 */
public class PhoneTextWatcher implements TextWatcher {
    private EditText etTelephone;
    private Context context;
    private int posNum = 0;

    public PhoneTextWatcher(EditText editText, Context context) {
        this.etTelephone = editText;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String value = s.toString();
        String tmp;
        if (value.length() > posNum) {
            switch (value.length()) {
                case 6:
                    tmp = value + ")";
                    etTelephone.setText(tmp);
                    etTelephone.setSelection(etTelephone.getText().length());
                    break;
                case 7:
                    if (!value.substring(value.length() - 1).contains(")")) {
                        tmp = value.substring(0, value.length() - 1) + ")" +
                                value.substring(value.length() - 1);
                        etTelephone.setText(tmp);
                        etTelephone.setSelection(etTelephone.getText().length());
                    }
                    break;
                case 10:
                    tmp = value + "-";
                    etTelephone.setText(tmp);
                    etTelephone.setSelection(etTelephone.getText().length());
                    break;
                case 11:
                    if (!value.substring(value.length() - 1).contains("-")) {
                        tmp = value.substring(0, value.length() - 1) + "-" +
                                value.substring(value.length() - 1);
                        etTelephone.setText(tmp);
                        etTelephone.setSelection(etTelephone.getText().length());
                    }
                    break;
                case 13:
                    tmp = value + "-";
                    etTelephone.setText(tmp);
                    etTelephone.setSelection(etTelephone.getText().length());
                    break;
                case 14:
                    if (!value.substring(value.length() - 1).contains("-")) {
                        tmp = value.substring(0, value.length() - 1) + "-" +
                                value.substring(value.length() - 1);
                        etTelephone.setText(tmp);
                        etTelephone.setSelection(etTelephone.getText().length());
                    }
                    break;
            }
        }
        posNum = value.length();
        if (posNum < 3) {
            etTelephone.setText(String.format(context.getString(R.string.phone_prefix_bracket_value), value));
            etTelephone.setSelection(etTelephone.getText().length());
        }

    }
}
