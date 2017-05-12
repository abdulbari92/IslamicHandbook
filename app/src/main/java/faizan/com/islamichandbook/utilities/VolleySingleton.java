package faizan.com.islamichandbook.utilities;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by buste on 4/16/2017.
 */
public class VolleySingleton {
    private static VolleySingleton volleySingleton = null;
    RequestQueue mRequestQueue;
    ImageLoader imageLoader;

    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        imageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024 / 8));
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static VolleySingleton getInstance(){
        if (volleySingleton == null) {
            volleySingleton = new VolleySingleton();
        }
        return volleySingleton;
    }

    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }
}