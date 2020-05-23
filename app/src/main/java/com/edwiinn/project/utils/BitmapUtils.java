package com.edwiinn.project.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public final class BitmapUtils {
    
    private BitmapUtils(){
        
    }

    public static Bitmap replaceWhiteColorToTransparent(Bitmap src) {
        if (src == null)
            return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < pixels.length; ++x) {
            if(pixels[x] == Color.WHITE) pixels[x] = Color.TRANSPARENT;
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap replaceTransparentToWhiteColor(Bitmap src) {
        if (src == null)
            return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < pixels.length; ++x) {
            if(pixels[x] == Color.TRANSPARENT) pixels[x] = Color.WHITE;
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap replaceTransparentToBlueSemiTransparant(Bitmap src) {
        if (src == null)
            return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < pixels.length; ++x) {
            if(pixels[x] == Color.TRANSPARENT) pixels[x] = Color.argb( 22, 33, 66, 88);
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }
}
