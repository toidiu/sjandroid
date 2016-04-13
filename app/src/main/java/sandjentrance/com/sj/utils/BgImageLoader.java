package sandjentrance.com.sj.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by toidiu on 4/12/16.
 */
public class BgImageLoader implements Target {
    private Resources resources;
    private View layout;

    public BgImageLoader(Resources resources, View layout) {
        this.resources = resources;
        this.layout = layout;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        layout.setBackground(new BitmapDrawable(resources, bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.d("error-------", errorDrawable.toString());
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
