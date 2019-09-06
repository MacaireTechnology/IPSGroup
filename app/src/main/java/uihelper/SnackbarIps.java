package uihelper;

import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class SnackbarIps {

    public SnackbarIps(View view, String msg){

        android.support.design.widget.Snackbar snackbar = android.support.design.widget.Snackbar
                .make(view, ""+msg, android.support.design.widget.Snackbar.LENGTH_LONG)
                .setAction("", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);
        snackbar.show();
    }
    public SnackbarIps(View view,String msg,int duration){

        android.support.design.widget.Snackbar snackbar = android.support.design.widget.Snackbar
                .make(view, ""+msg, android.support.design.widget.Snackbar.LENGTH_LONG)
                .setAction("", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.GREEN);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        } else {
//            textView.setGravity(Gravity.CENTER_HORIZONTAL);
//        }
        snackbar.setDuration(duration);
        snackbar.show();
    }
}

