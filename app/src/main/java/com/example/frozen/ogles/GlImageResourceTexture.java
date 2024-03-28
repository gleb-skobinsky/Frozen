package com.example.frozen.ogles;

import static android.opengl.GLES20.GL_MAX_TEXTURE_SIZE;
import static android.opengl.GLES20.glGetIntegerv;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.frozen.Utils.BitmapFactoryUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class GlImageResourceTexture extends GlImageTexture {

    private InputStream mImageStream;

    private final boolean mAutoClose;

    public GlImageResourceTexture(final Resources res, final int resId) {
        this(res.openRawResource(resId), true);
    }

    public GlImageResourceTexture(final String filename) throws FileNotFoundException {
        this(new FileInputStream(new File(filename)), true);
    }

    public GlImageResourceTexture(final File file) throws FileNotFoundException {
        this(new FileInputStream(file), true);
    }

    public GlImageResourceTexture(final InputStream is) {
        this(is, true);
    }

    public GlImageResourceTexture(final InputStream is, final boolean autoClose) {
        if (is == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        mImageStream = is;
        mAutoClose = autoClose;
    }

    public boolean isAutoClose() {
        return mAutoClose;
    }
    @Override
    public void setup() {
        final int[] args = new int[1];
        glGetIntegerv(GL_MAX_TEXTURE_SIZE, args, 0);
        final int maxTextureSize = args[0];

        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(mImageStream, null, opts);

        final int size = Math.max(opts.outWidth, opts.outHeight);
        if (size > maxTextureSize) {
            opts.inSampleSize = size / maxTextureSize;
        }

        opts.inJustDecodeBounds = false;
        opts.inDither = true;
        final Bitmap bitmap = BitmapFactoryUtils.decodeStream(mImageStream, opts.inSampleSize, 0, 2);
        try {
            attachToTexture(bitmap);
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mAutoClose) {
                dispose();
            }
        } finally {
            super.finalize();
        }
    }

    public void dispose() {
        if (mImageStream != null) {
            try {
                mImageStream.close();
            } catch (final IOException e) {}	// 無視する
        }
        mImageStream = null;
    }

}
