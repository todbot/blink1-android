/*
 * Copyright 2020 Tod E. Kurt / todbot.com
 *
 */

package com.thingm.blink1;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

public class Blink1  {

    private HidDevice hidDevice;
    private UsbDevice usbDevice;


    public Blink1(Context context, UsbDevice device) throws IOException {

        this.usbDevice = device;
        this.hidDevice = new HidDevice(context,device);
    }

    /**
     * Return blink(1) serial number string
     * @return String of 8 hex digits as serial number
     */
    public String getSerialNumber() {
        return this.usbDevice.getSerialNumber();
    }

    /**
     * Fade blink(1) to RGB color over fadeMillis milliseconds.
     *
     * @param fadeMillis milliseconds to take to get to color
     * @param r          red component 0..255
     * @param g          green component 0..255
     * @param b          blue component 0..255
     *
     */
    public void fadeToRGB(int fadeMillis, int r, int g, int b ) {
        this.fadeToRGB( fadeMillis, r,g,b, 0);
    }

    /**
     * Fade blink(1) to RGB color over fadeMillis milliseconds.
     *
     * @param fadeMillis milliseconds to take to get to color
     * @param r          red component 0..255
     * @param g          green component 0..255
     * @param b          blue component 0..255
     * @param ledn       which LED to address (0=all)
     *
     */
    public void fadeToRGB(int fadeMillis, int r, int g, int b, int ledn ) {
        int dms = fadeMillis/10;

        byte[] buffer =  { 0x01, 0x63, (byte)r, (byte)g, (byte)b, (byte)(dms >> 8), (byte)(dms % 0xff), (byte)ledn, 0x00};
        Log.d("BLINK1", "fadeToRGB:"+buffer[0] +","+ buffer[1] +","+ buffer[2] +","+ buffer[3]+","+ buffer[4]);
        try {
            //                               reportId, buffer, length
            this.hidDevice.sendFeatureReport(buffer[0], buffer, buffer.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the color of the device with separate r, g and b byte values
     *
     * @param r red byte color value 0..255
     * @param g gree byte color value 0..255
     * @param b blue byte color value 0..255
     */
    public void setRGB(int r, int g, int b) {

        byte[] buffer =  { 0x01, (byte)'n', (byte)r, (byte)g, (byte)b, 0x00,0x01, 0x00,0x00};
        Log.d("BLINK1", "setRGB:"+buffer[0] +","+ buffer[1] +","+ buffer[2] +","+ buffer[3]+","+ buffer[4]);
        try {
            //                               reportId, buffer, length
            this.hidDevice.sendFeatureReport(buffer[0], buffer, buffer.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Turn blink(1) off
     */
    public void off() {
        this.setRGB(0,0,0);
    }

    /**
     * Return firmware version as number
     * e.g. v302 firmware is returned as in "302"
     * @return number representing firmware version or zero on error
     */
    public int getVersion() {
        byte [] buffer = { 1, 'v', 0,0,0,0,0,0,0 };
        try {
            this.hidDevice.sendFeatureReport(buffer[0], buffer, buffer.length);
            this.hidDevice.getFeatureReport(buffer[0], buffer, buffer.length);
            Log.d("BLINK1", "getVersion:"+Arrays.toString((buffer)));
            int vh = Character.getNumericValue(buffer[3]);
            int vl = Character.getNumericValue(buffer[4]);
            int ver = (vh*100) + vl;
            return ver;
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return 0;
    }

}
