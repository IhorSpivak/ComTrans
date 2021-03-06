package ru.comtrans.views;

import android.app.ProgressDialog;
import android.content.Context;

import ru.comtrans.R;

/**
 * Created by Artco on 08.06.2016.
 */
public class ConnectionProgressDialog extends ProgressDialog {

    public ConnectionProgressDialog(Context context) {
        super(context);
        setIndeterminate(true);
        setMessage(context.getString(R.string.loading));
        setCancelable(false);
    }

    public ConnectionProgressDialog(Context context,String message) {
        super(context);
        setIndeterminate(true);
        setMessage(message);
        setCancelable(false);
    }

    public ConnectionProgressDialog(Context context,int messageResId) {
        super(context);
        setIndeterminate(true);
        setMessage(context.getString(messageResId));
        setCancelable(false);
    }
}
