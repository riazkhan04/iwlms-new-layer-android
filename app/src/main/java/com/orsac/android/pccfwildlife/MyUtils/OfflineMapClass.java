package com.orsac.android.pccfwildlife.MyUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;

public class OfflineMapClass implements TileProvider {

    public  int TILE_SIZE_DP = 256;

    public float mScaleFactor;

    public Bitmap mBorderTile;


    public OfflineMapClass(Context context) {
        /* Scale factor based on density, with a 0.6 multiplier to increase tile generation
         * speed */
        mScaleFactor = context.getResources().getDisplayMetrics().density * 0.6f;
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        mBorderTile = Bitmap.createBitmap((int) (TILE_SIZE_DP * mScaleFactor),
                (int) (TILE_SIZE_DP * mScaleFactor), android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBorderTile);
        canvas.drawRect(0, 0, TILE_SIZE_DP * mScaleFactor, TILE_SIZE_DP * mScaleFactor,
                borderPaint);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        Bitmap coordTile = drawTileCoords(x, y, zoom);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        coordTile.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] bitmapData = stream.toByteArray();
        return new Tile((int) (TILE_SIZE_DP * mScaleFactor),
                (int) (TILE_SIZE_DP * mScaleFactor), bitmapData);
    }

    private Bitmap drawTileCoords(int x, int y, int zoom) {
        // Synchronize copying the bitmap to avoid a race condition in some devices.
        Bitmap copy = null;
        synchronized (mBorderTile) {
            copy = mBorderTile.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
        }
        return copy;
    }

    //call for offline map from activity or class as below
//            TileProvider coordTileProvider = new OfflineMapClass(context);
//            map.addTileOverlay(new TileOverlayOptions().tileProvider(coordTileProvider));
}
