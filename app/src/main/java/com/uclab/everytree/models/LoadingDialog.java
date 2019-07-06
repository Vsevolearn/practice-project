package com.uclab.everytree.models;

import android.app.ProgressDialog;
import android.content.Context;

import com.uclab.everytree.R;

public class LoadingDialog {
    private ProgressDialog mProgressDialog;

    public LoadingDialog(Context _cxt) {
        mProgressDialog = new ProgressDialog(_cxt);
        mProgressDialog.setMessage(_cxt.getString(R.string.loadMsg));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
    }

    public void show() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    public void hide() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
