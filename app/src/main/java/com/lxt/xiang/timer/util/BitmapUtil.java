package com.lxt.xiang.timer.util;


import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v8.renderscript.RenderScript;
import android.widget.ImageView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.loader.ArtistLoader;
import com.lxt.xiang.timer.loader.PlaylistLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BitmapUtil {

    public static Uri queryArtById(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    public static void loadBitmap(final ImageView imageView, final long albumId) {
        loadBitmapWithPalette(imageView, albumId, null);
    }

    public static void loadBitmap(final ImageView imageView, final long albumId, final Palette.PaletteAsyncListener listener) {
        loadBitmapWithPalette(imageView, albumId, listener);
    }

    public static void loadBitmapByArtistId(final ImageView albumArt, final long artistId) {
        Observable.just(ArtistLoader.getAlbumIdByArtist(albumArt.getContext(), artistId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long albumId) {
                        loadBitmapWithPalette(albumArt, albumId, null);
                    }
                });
    }

    public static void loadBitmapByArtistId(final ImageView albumArt, final long artistId, final Palette.PaletteAsyncListener listener) {
        Observable.just(ArtistLoader.getAlbumIdByArtist(albumArt.getContext(), artistId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long albumId) {
                        loadBitmapWithPalette(albumArt, albumId, listener);
                    }
                });
    }

    public static void loadBitmapByPlaylist(final ImageView albumArt, final long playlistId, final Palette.PaletteAsyncListener listener) {
        Observable.just(PlaylistLoader.getAlbumIdFromPlayList(albumArt.getContext(), playlistId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long albumId) {
                        loadBitmapWithPalette(albumArt, albumId, listener);
                    }
                });
    }

    public static void loadBlurBitmap(final ImageView albumArt, long albumId, final Palette.PaletteAsyncListener listener) {
        loadBlurBitmapWithPalette(albumArt, albumId, listener);
    }

    public static Bitmap createBlurBitmap(Bitmap bitmap, Context context, int inSampleSize) {

        RenderScript rs = RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final android.support.v8.renderscript.Allocation input = android.support.v8.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
        final android.support.v8.renderscript.Allocation output = android.support.v8.renderscript.Allocation.createTyped(rs, input.getType());
        final android.support.v8.renderscript.ScriptIntrinsicBlur script = android.support.v8.renderscript.ScriptIntrinsicBlur.create(rs, android.support.v8.renderscript.Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

        return blurTemplate;
    }

    public static void loadBlurBitmapByPlaylist(final ImageView albumArt, long playlistId, final Palette.PaletteAsyncListener listener) {
        Observable.just(PlaylistLoader.getAlbumIdFromPlayList(albumArt.getContext(), playlistId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long albumId) {
                        loadBlurBitmapWithPalette(albumArt, albumId, listener);
                    }
                });
    }

    public static int getContrastColor(int rgb) {
        int red = 256 - Color.red(rgb);
        int green = 256 - Color.green(rgb);
        int blue = 256 - Color.blue(rgb);
        return Color.rgb(red, green, blue);
    }

    public static int getDarkColor(int rgb) {
        int red = Math.max(0, Color.red(rgb)-24);
        int green = Math.max(0, Color.green(rgb)-24);
        int blue = Math.max(0, Color.blue(rgb)-24);
        return Color.rgb(red, green, blue);
    }

    private static void loadBitmapWithPalette(final ImageView imageView, final long albumId, final Palette.PaletteAsyncListener listener){
        Uri uri = queryArtById(albumId);
        Picasso.with(imageView.getContext()).load(uri).placeholder(R.drawable.header_placeholder)
                .fit().centerCrop()
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        if(listener!=null){
                            Palette.from(source).generate(listener);
                        }
                        return source;
                    }

                    @Override
                    public String key() {
                        return "Palette";
                    }
                })
                .into(imageView);
    }

    private static void loadBlurBitmapWithPalette(final ImageView albumArt, final long albumId, final Palette.PaletteAsyncListener listener){
        Uri uri = queryArtById(albumId);
        Picasso.with(albumArt.getContext()).load(uri).placeholder(R.drawable.header_placeholder)
                .fit().centerCrop()
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        if(listener!=null){
                            Palette.from(source).generate(listener);
                        }
                        Bitmap result = createBlurBitmap(source, albumArt.getContext(), 4);
                        source.recycle();
                        source=null;
                        return result;
                    }

                    @Override
                    public String key() {
                        return "Palette";
                    }
                })
                .into(albumArt);
    }

}
