package com.rednovo.libs.net.okhttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.okhttp.Response;


public abstract class BitmapCallback extends Callback<Bitmap> {
    @Override
    public Bitmap parseNetworkResponse(Response response) throws Exception {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }

}
