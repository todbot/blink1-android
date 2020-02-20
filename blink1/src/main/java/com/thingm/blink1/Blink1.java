package com.thingm.blink1;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

public class Blink1  {

    private HidDevice hidDevice;


    public Blink1(Context context, UsbDevice device) throws IOException {

        this.hidDevice = new HidDevice(context,device);
    }



    /**
     * Set the color of the device with separate r, g and b byte values
     *
     * @param r red byte color value 0..255
     * @param g gree byte color value 0..255
     * @param b blue byte color value 0..255
     */
    public void setColor(byte r, byte g, byte b) {

        byte[] buffer =  { 0x01, 0x63, r, g, b, 0x00,0x20, 0x00,0x00};
        Log.d("BLINK1", "sending buffer "+buffer[0] +","+ buffer[1] +","+ buffer[2] +","+ buffer[3]+","+ buffer[4]);
        try {
            //                               reportId, buffer, length
            this.hidDevice.sendFeatureReport(buffer[0], buffer, buffer.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getVersion() {
        byte [] buffer = { 1, 'v', 0,0,0,0,0,0,0 };
        try {
            this.hidDevice.sendFeatureReport(buffer[0], buffer, buffer.length);
            this.hidDevice.getFeatureReport(buffer[0], buffer, buffer.length);
            Log.d("BLINK1", "getVersion:"+Arrays.toString((buffer)));
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return 0;
    }

}
