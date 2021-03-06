/*
 * Copyright 2020 Tod E. Kurt / todbot.com
 *
 */

package com.thingm.blink1demo;


import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thingm.blink1.Blink1;
import com.thingm.blink1.Blink1Finder;

public class MainActivity extends AppCompatActivity

{
    private static final String TAG = "BLINK1DEMO";
    private static final String ACTION_USB_PERMISSION = "com.thingm.blink1demo.action.USB_PERMISSION";
    private PendingIntent permissionIntent;

    Blink1Finder blink1Finder;
    Blink1 blink1;
    SeekBar seekBarR;
    SeekBar seekBarG;
    SeekBar seekBarB;
    TextView statusText;
    TextView serialText;

    int r = 0;
    int g = 0;
    int b = 0;

    SeekBar.OnSeekBarChangeListener seekbarChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText=(TextView) findViewById(R.id.statusText);
        serialText=(TextView) findViewById(R.id.serialText);
        statusText.setText("looking for blink(1)");
        serialText.setText(" ");

        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        blink1Finder = new Blink1Finder();
        blink1Finder.setContext(this);
        blink1Finder.setPermissionIntent(permissionIntent);

        Log.d(TAG, "Looking for BLINK1");
        blink1 = blink1Finder.openFirst();

        if( blink1 != null ) {
            statusText.setText("blink(1) connected!");
            serialText.setText("serial:"+blink1.getSerialNumber()+", fw version:"+blink1.getVersion());
            blink1.off();
            Log.d(TAG,"blink1 serial:"+ blink1.getSerialNumber());
        }

        seekbarChange =  new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(      seekBar == seekBarR ) { r = progress; }
                else if( seekBar == seekBarG ) { g = progress; }
                else if( seekBar == seekBarB ) { b = progress; }
                Log.d(TAG, "onProgressChanged:"+progress+"  rgb:"+r+","+g+","+b);
                if( blink1!=null ) {
                    blink1.setRGB(r, g, b);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };

        seekBarR=(SeekBar)findViewById(R.id.seekBarR);
        seekBarR.setOnSeekBarChangeListener(seekbarChange);
        seekBarG = (SeekBar) findViewById(R.id.seekBarG);
        seekBarG.setOnSeekBarChangeListener(seekbarChange);
        seekBarB = (SeekBar) findViewById(R.id.seekBarB);
        seekBarB.setOnSeekBarChangeListener(seekbarChange);
    }


}
