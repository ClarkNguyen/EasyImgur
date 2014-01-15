package com.koushikdutta.ion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.future.SimpleFuture;
import com.koushikdutta.async.future.TransformFuture;
import com.koushikdutta.async.http.ResponseCacheMiddleware;
import com.koushikdutta.async.http.libcore.DiskLruCache;
import com.koushikdutta.async.parser.ByteBufferListParser;
import com.koushikdutta.ion.bitmap.BitmapInfo;
import com.koushikdutta.ion.bitmap.Transform;
import com.koushikdutta.ion.builder.BitmapFutureBuilder;
import com.koushikdutta.ion.builder.Builders;
import com.koushikdutta.ion.builder.ImageViewBuilder;
import com.koushikdutta.ion.builder.ImageViewFutureBuilder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by koush on 5/23/13.
 */
class IonBitmapRequestBuilder implements Builders.ImageView.F, ImageViewFutureBuilder, BitmapFutureBuilder, Builders.Any.BF {
    private static final SimpleFuture<ImageView> FUTURE_IMAGEVIEW_NULL_URI = new SimpleFuture<ImageView>() {
        {
            setComplete(new NullPointerException("uri"));
        }
    };
    private static final SimpleFuture<Bitmap> FUTURE_BITMAP_NULL_URI = new SimpleFuture<Bitmap>() {
        {
            setComplete(new NullPointerException("uri"));
        }
    };

    IonRequestBuilder builder;
    Ion ion;
    WeakReference<ImageView> imageViewPostRef;
    ArrayList<Transform> transforms;
    Drawable placeholderDrawable;
    int placeholderResource;
    Drawable errorDrawable;
    int errorResource;
    Animation inAnimation;
    Animation loadAnimation;
    int loadAnimationResource;
    int inAnimationResource;
    ScaleMode scaleMode = ScaleMode.FitXY;
    int resizeWidth;
    int resizeHeight;
    boolean disableFadeIn;
    boolean animateGif = true;


    void reset() {
        placeholderDrawable = null;
        placeholderResource = 0;
        errorDrawable = null;
        errorResource = 0;
        ion = null;
        imageViewPostRef = null;
        transforms = null;
        inAnimation = null;
        inAnimationResource = 0;
        loadAnimation = null;
        loadAnimationResource = 0;
        scaleMode = ScaleMode.FitXY;
        resizeWidth = 0;
        resizeHeight = 0;
        disableFadeIn = false;
        animateGif = true;
        builder = null;
    }

    public IonBitmapRequestBuilder(IonRequestBuilder builder) {
        this.builder = builder;
        ion = builder.ion;
    }

    public IonBitmapRequestBuilder(Ion ion) {
        this.ion = ion;
    }

    static void doAnimation(ImageView imageView, Animation animation, int animationResource) {
        if (imageView == null)
            return;
        if (animation == null && animationResource != 0)
            animation = AnimationUtils.loadAnimation(imageView.getContext(), animationResource);
        if (animation == null) {
            imageView.setAnimation(null);
            return;
        }

        imageView.startAnimation(animation);
    }

    private IonRequestBuilder ensureBuilder() {
        if (builder == null)
            builder = new IonRequestBuilder(imageViewPostRef.get().getContext(), ion);
        return builder;
    }

    @Override
    public Future<ImageView> load(String uri) {
        ensureBuilder();
        builder.load(uri);
        return intoImageView(imageViewPostRef.get());
    }

    @Override
    public Future<ImageView> load(String method, String url) {
        ensureBuilder();
        builder.load(method, url);
        return intoImageView(imageViewPostRef.get());
    }

    IonBitmapRequestBuilder withImageView(ImageView imageView) {
        imageViewPostRef = new WeakReference<ImageView>(imageView);
        return this;
    }

    @Override
    public IonBitmapRequestBuilder transform(Transform transform) {
        if (transforms == null)
            transforms = new ArrayList<Transform>();
        transforms.add(transform);
        return this;
    }

    boolean fastLoad(final String downloadKey, boolean put) {
        for (Loader loader: ion.configure().getLoaders()) {
            Future<BitmapInfo> future = loader.loadBitmap(ion, builder.uri, resizeWidth, resizeHeight);
            if (future != null) {
                final BitmapCallback callback = new BitmapCallback(ion, downloadKey, put);
                future.setCallback(new FutureCallback<BitmapInfo>() {
                    @Override
                    public void onCompleted(Exception e, BitmapInfo result) {
                        if (result != null)
                            result.key = downloadKey;
                        callback.report(e, result);
                    }
                });
                return true;
            }
        }
        return false;
    }

    private String computeDownloadKey() {
        String downloadKey = builder.uri;
        // although a gif is always same download, the initial decode is different
        if (!animateGif)
            downloadKey += ":!animateGif";
        return ResponseCacheMiddleware.toKeyString(downloadKey);
    }

    Pair<String, BitmapInfo> execute() {
        final String downloadKey = computeDownloadKey();
        assert Thread.currentThread() == Looper.getMainLooper().getThread() || imageViewPostRef == null;
        assert downloadKey != null;

        if (resizeHeight > 0 || resizeWidth > 0) {
            transform(new DefaultTransform(resizeWidth, resizeHeight, scaleMode));
        }

        // determine the key for this bitmap after all transformations
        String bitmapKey = downloadKey;
        boolean hasTransforms = transforms != null && transforms.size() > 0;
        if (hasTransforms) {
            for (Transform transform : transforms) {
                bitmapKey += transform.key();
            }
            bitmapKey = ResponseCacheMiddleware.toKeyString(bitmapKey);
        }

        // see if this request can be fulfilled from the cache
        if (!builder.noCache) {
            BitmapInfo bitmap = builder.ion.bitmapCache.get(bitmapKey);
            if (bitmap != null)
                return new Pair<String, BitmapInfo>(bitmapKey, bitmap);
        }

        Pair<String, BitmapInfo> ret = new Pair<String, BitmapInfo>(bitmapKey, null);

        // bitmaps that were transformed are put into the DiskLruCache to prevent
        // subsequent retransformation. See if we can retrieve the bitmap from the disk cache.
        // See BitmapToBitmapInfo for where the cache is populated.
        DiskLruCache diskLruCache = ion.responseCache.getDiskLruCache();
        if (!builder.noCache && hasTransforms && diskLruCache.containsKey(bitmapKey)) {
            BitmapToBitmapInfo.getBitmapSnapshot(ion, bitmapKey);
            return ret;
        }

        // Perform a download as necessary.
        if (!ion.bitmapsPending.contains(downloadKey) && !fastLoad(downloadKey, !hasTransforms)) {
            builder.setHandler(null);
            // if we cancel, gotta remove any waiters.
            IonRequestBuilder.EmitterTransform<ByteBufferList> emitterTransform = builder.execute(new ByteBufferListParser(), new Runnable() {
                @Override
                public void run() {
                    AsyncServer.post(Ion.mainHandler, new Runnable() {
                        @Override
                        public void run() {
                            ion.bitmapsPending.remove(downloadKey);
                        }
                    });
                }
            });
            emitterTransform.setCallback(new LoadBitmap(ion, downloadKey, !hasTransforms, resizeWidth, resizeHeight, animateGif, emitterTransform));
        }

        // if there's a transform, do it
        if (!hasTransforms)
            return ret;

        // verify this transform isn't already pending
        // make sure that the parent download isn't cancelled (empty list)
        // and also make sure there are waiters for this transformed bitmap
        if (!ion.bitmapsPending.contains(downloadKey) || !ion.bitmapsPending.contains(bitmapKey)) {
            ion.bitmapsPending.add(downloadKey, new BitmapToBitmapInfo(ion, bitmapKey, downloadKey, transforms));
        }

        return ret;
    }

    private IonDrawable setIonDrawable(ImageView imageView, BitmapInfo info, int loadedFrom) {
        IonDrawable ret = IonDrawable.getOrCreateIonDrawable(imageView);
        ret.setBitmap(info, loadedFrom);
        ret.setSize(resizeWidth, resizeHeight);
        ret.setError(errorResource, errorDrawable);
        ret.setPlaceholder(placeholderResource, placeholderDrawable);
        ret.setInAnimation(inAnimation, inAnimationResource);
        ret.setDisableFadeIn(disableFadeIn);
        imageView.setImageDrawable(ret);
        return ret;
    }

    @Override
    public Future<ImageView> intoImageView(ImageView imageView) {
        if (imageView == null)
            throw new IllegalArgumentException("imageView");
        assert Thread.currentThread() == Looper.getMainLooper().getThread();

        // no uri? just set a placeholder and bail
        if (builder.uri == null) {
            setIonDrawable(imageView, null, 0).cancel();
            return FUTURE_IMAGEVIEW_NULL_URI;
        }

        // execute the request, see if we get a bitmap from cache.
        Pair<String, BitmapInfo> pair = execute();
        if (pair.second != null) {
            doAnimation(imageView, null, 0);
            IonDrawable drawable = setIonDrawable(imageView, pair.second, Loader.LoaderEmitter.LOADED_FROM_MEMORY);
            drawable.cancel();
            SimpleFuture<ImageView> imageViewFuture = drawable.getFuture();
            imageViewFuture.reset();
            imageViewFuture.setComplete(imageView);
            return imageViewFuture;
        }

        IonDrawable drawable = setIonDrawable(imageView, null, 0);
        doAnimation(imageView, loadAnimation, loadAnimationResource);
        SimpleFuture<ImageView> imageViewFuture = drawable.getFuture();
        imageViewFuture.reset();

        drawable.register(ion, pair.first);

        return imageViewFuture;
    }

    @Override
    public Future<Bitmap> asBitmap() {
        // no uri? just set a placeholder and bail
        if (builder.uri == null) {
            return FUTURE_BITMAP_NULL_URI;
        }

        // see if we get something back synchronously
        Pair<String, BitmapInfo> pair = execute();
        if (pair.second != null) {
            SimpleFuture<Bitmap> ret = new SimpleFuture<Bitmap>();
            Bitmap bitmap = pair.second.bitmaps == null ? null : pair.second.bitmaps[0];
            ret.setComplete(pair.second.exception, bitmap);
            return ret;
        }

        // we're loading, so let's register for the result.
        BitmapInfoToBitmap ret = new BitmapInfoToBitmap(builder.context);
        ion.bitmapsPending.add(pair.first, ret);
        return ret;
    }

    @Override
    public IonBitmapRequestBuilder placeholder(Drawable drawable) {
        placeholderDrawable = drawable;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder placeholder(int resourceId) {
        placeholderResource = resourceId;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder error(Drawable drawable) {
        errorDrawable = drawable;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder error(int resourceId) {
        errorResource = resourceId;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder animateIn(Animation in) {
        inAnimation = in;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder animateLoad(Animation load) {
        loadAnimation = load;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder animateLoad(int animationResource) {
        loadAnimationResource = animationResource;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder animateIn(int animationResource) {
        inAnimationResource = animationResource;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder centerCrop() {
        if (resizeWidth <= 0 || resizeHeight <= 0)
            throw new IllegalStateException("must call resize first");
        scaleMode = ScaleMode.CenterCrop;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder centerInside() {
        if (resizeWidth <= 0 || resizeHeight <= 0)
            throw new IllegalStateException("must call resize first");
        scaleMode = ScaleMode.CenterInside;
        return this;
    }

    @Override
    public IonBitmapRequestBuilder resize(int width, int height) {
        resizeWidth = width;
        resizeHeight = height;
        ensureBuilder().setHeader("X-Ion-Width", String.valueOf(width));
        ensureBuilder().setHeader("X-Ion-Height", String.valueOf(height));
        return this;
    }

    @Override
    public IonBitmapRequestBuilder disableFadeIn() {
        this.disableFadeIn = true;
        return this;
    }
	
	public IonBitmapRequestBuilder smartSize(boolean smartSize) {
        //don't want to disable device resize if user has already resized the Bitmap.
        if (resizeWidth > 0 || resizeHeight > 0)
            throw new IllegalStateException("Can't change smart size after resize has been called.");

        if (!smartSize) {
			resizeWidth = -1;
			resizeHeight = -1;
		}
        else {
            resizeWidth = 0;
            resizeHeight = 0;
        }
		return this;
	}

    @Override
    public IonBitmapRequestBuilder animateGif(boolean animateGif) {
        this.animateGif = animateGif;
        return this;
    }

    private static class BitmapInfoToBitmap extends TransformFuture<Bitmap, BitmapInfo> {
        WeakReference<Context> context;
        public BitmapInfoToBitmap(WeakReference<Context> context) {
            this.context = context;
        }

        @Override
        protected void transform(BitmapInfo result) throws Exception {
            if (!IonRequestBuilder.checkContext(context)) {
                cancel();
                return;
            }

            if (result.exception != null)
                setComplete(result.exception);
            else
                setComplete(result.bitmaps[0]);
        }
    }
}
