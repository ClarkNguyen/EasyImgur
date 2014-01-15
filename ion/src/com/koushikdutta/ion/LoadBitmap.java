package com.koushikdutta.ion;

import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.bitmap.BitmapInfo;
import com.koushikdutta.ion.gif.GifAction;
import com.koushikdutta.ion.gif.GifDecoder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class LoadBitmap extends BitmapCallback implements FutureCallback<ByteBufferList> {
    int resizeWidth;
    int resizeHeight;
    IonRequestBuilder.EmitterTransform<ByteBufferList> emitterTransform;
    boolean animateGif;

    public LoadBitmap(Ion ion, String urlKey, boolean put, int resizeWidth, int resizeHeight, boolean animateGif, IonRequestBuilder.EmitterTransform<ByteBufferList> emitterTransform) {
        super(ion, urlKey, put);
        this.resizeWidth = resizeWidth;
        this.resizeHeight = resizeHeight;
        this.animateGif = animateGif;
        this.emitterTransform = emitterTransform;
    }

    private boolean isGif() {
        if (emitterTransform == null)
            return false;
        if (emitterTransform.finalRequest != null) {
            URI uri = emitterTransform.finalRequest.getUri();
            if (uri != null && uri.toString().endsWith(".gif"))
                return true;
        }
        if (emitterTransform.headers == null)
            return false;
        return "image/gif".equals(emitterTransform.headers.get("Content-Type"));
    }

    @Override
    public void onCompleted(Exception e, final ByteBufferList result) {
        if (e != null) {
            report(e, null);
            return;
        }

        if (ion.bitmapsPending.tag(key) != this) {
//            Log.d("IonBitmapLoader", "Bitmap load cancelled (no longer needed)");
            result.recycle();
            return;
        }

        Ion.getBitmapLoadExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                ByteBuffer bb = result.getAll();
                try {
                    Bitmap[] bitmaps;
                    int[] delays;
                    if (!isGif()) {
                        Bitmap bitmap = ion.bitmapCache.loadBitmap(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining(), resizeWidth, resizeHeight);
                        if (bitmap == null)
                            throw new Exception("failed to transform bitmap");
                        bitmaps = new Bitmap[] { bitmap };
                        delays = null;
                    }
                    else {
                        GifDecoder decoder = new GifDecoder(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining(), new GifAction() {
                            @Override
                            public boolean parseOk(boolean parseStatus, int frameIndex) {
                                return animateGif;
                            }
                        });
                        decoder.run();
                        bitmaps = new Bitmap[decoder.getFrameCount()];
                        delays = decoder.getDelays();
                        for (int i = 0; i < decoder.getFrameCount(); i++) {
                            Bitmap bitmap = decoder.getFrameImage(i);
                            if (bitmap == null)
                                throw new Exception("failed to transform bitmap");
                            bitmaps[i] = bitmap;
                        }
                    }

                    BitmapInfo info = new BitmapInfo();
                    info.key = key;
                    info.bitmaps = bitmaps;
                    info.delays = delays;
                    if (emitterTransform != null)
                        info.loadedFrom = emitterTransform.loadedFrom();
                    else
                        info.loadedFrom = Loader.LoaderEmitter.LOADED_FROM_CACHE;

                    report(null, info);
                }
                catch (OutOfMemoryError e) {
                    report(new Exception(e), null);
                }
                catch (Exception e) {
                    report(e, null);
                }
                finally {
                    ByteBufferList.reclaim(bb);
                }
            }
        });
    }
}

    