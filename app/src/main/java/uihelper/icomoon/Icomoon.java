package uihelper.icomoon;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class Icomoon {

    public static final Icomoon imageLogo = new Icomoon("icomoon.ttf");

    private final String assetName;
    private volatile Typeface typeface;

    private Icomoon(String assetName) {
        this.assetName = assetName;
    }
    public void apply(Context context, TextView textView) {
        if (typeface == null) {
            synchronized (this) {
                if (typeface == null) {
                    typeface = Typeface.createFromAsset(context.getAssets(), assetName);
                }
            }
        }
        textView.setTypeface(typeface);
    }
}
