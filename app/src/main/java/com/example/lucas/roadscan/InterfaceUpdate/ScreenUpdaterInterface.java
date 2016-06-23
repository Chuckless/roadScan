package com.example.lucas.roadscan.InterfaceUpdate;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by lucas on 07/07/15.
 */
public interface ScreenUpdaterInterface {

    void Toaster(String string);

    void updateDistTV(String string);
    void updateSpeedTV(String string);

    void updateCountTV(String string);
    void updateUploadedTV(String string);

    void updateDevTV(String string);

    void updateGPSTV(String string);
    void updateRunning(String string);

    void updateDevMean(String string);
    void updateColor(int color);





}
