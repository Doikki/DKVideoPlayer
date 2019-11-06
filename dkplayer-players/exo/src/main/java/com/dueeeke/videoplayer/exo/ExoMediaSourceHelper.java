package com.dueeeke.videoplayer.exo;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.lang.reflect.Field;
import java.util.Map;

public final class ExoMediaSourceHelper {

    private final String mUserAgent;
    private DataSource.Factory mDataSourceFactory;
    private Context mAppContext;
    private HttpDataSource.Factory mHttpDataSourceFactory;

    public ExoMediaSourceHelper(Context context) {
        mAppContext = context.getApplicationContext();
        mUserAgent = Util.getUserAgent(mAppContext, mAppContext.getApplicationInfo().name);
    }

    public MediaSource getMediaSource(String uri) {
        return getMediaSource(uri, null);
    }

    public MediaSource getMediaSource(String uri, Map<String, String> headers) {
        Uri contentUri = Uri.parse(uri);
        if ("rtmp".equals(contentUri.getScheme())) {
            return new ProgressiveMediaSource.Factory(new RtmpDataSourceFactory(null))
                    .createMediaSource(contentUri);
        }
        int contentType = inferContentType(uri);
        if (mDataSourceFactory == null) {
            mDataSourceFactory = getDataSourceFactory();
            if (mHttpDataSourceFactory != null) {
                setHeaders(headers);
            }
        }
        switch (contentType) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(mDataSourceFactory).createMediaSource(contentUri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(mDataSourceFactory).createMediaSource(contentUri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mDataSourceFactory).createMediaSource(contentUri);
            default:
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(mDataSourceFactory).createMediaSource(contentUri);
        }
    }

    public int inferContentType(String fileName) {
        fileName = Util.toLowerInvariant(fileName);
        if (fileName.contains(".mpd")) {
            return C.TYPE_DASH;
        } else if (fileName.contains(".m3u8")) {
            return C.TYPE_HLS;
        } else if (fileName.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
            return C.TYPE_SS;
        } else {
            return C.TYPE_OTHER;
        }
    }

    /**
     * Returns a new DataSource factory.
     *
     * @return A new DataSource factory.
     */
    public DataSource.Factory getDataSourceFactory() {
        return new DefaultDataSourceFactory(mAppContext, getHttpDataSourceFactory());
    }

    /**
     * Returns a new HttpDataSource factory.
     *
     * @return A new HttpDataSource factory.
     */
    public DataSource.Factory getHttpDataSourceFactory() {
        if (mHttpDataSourceFactory == null) {
            mHttpDataSourceFactory = new DefaultHttpDataSourceFactory(
                    mUserAgent,
                    null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    //http->https重定向支持
                    true);
        }
        return mHttpDataSourceFactory;
    }

    private void setHeaders(Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                String key = header.getKey();
                String value = header.getValue();
                //如果发现用户通过header传递了UA，则强行将HttpDataSourceFactory里面的userAgent字段替换成用户的
                if (TextUtils.equals(key, "User-Agent")) {
                    if (!TextUtils.isEmpty(value)) {
                        try {
                            Field userAgentField = mHttpDataSourceFactory.getClass().getDeclaredField("userAgent");
                            userAgentField.setAccessible(true);
                            userAgentField.set(mHttpDataSourceFactory, value);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                } else {
                    mHttpDataSourceFactory.getDefaultRequestProperties().set(key, value);
                }
            }
        }
    }

    public void setDataSourceFactory(DataSource.Factory factory) {
        mDataSourceFactory = factory;
    }

    public void setHttpDataSourceFactory(HttpDataSource.Factory factory) {
        mHttpDataSourceFactory = factory;
    }
}
