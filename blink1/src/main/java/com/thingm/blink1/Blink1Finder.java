/*
 * Copyright 2020 Tod E. Kurt / todbot.com
 *
 */

package com.thingm.blink1;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class Blink1Finder {

    public final static int VENDOR_ID = 0x27b8;
    public final static int PRODUCT_ID = 0x01ed;


    private Context context;
    private UsbManager usbManager;

    private PendingIntent permissionIntent;

    public void setContext(Context c) {
        context = c;
    }

    /**
     * Set permission intent object to be notified when user allows application to use blink(1) device.
     * @param intent    permission intent object
     */
    public void setPermissionIntent(PendingIntent intent) {
        this.permissionIntent = intent;
    }

    /**
     * Open a blink(1) based on a UsbDevice.
     * Requests permission
     * @param device
     * @return Blink1 objec or null if no blink(1) devices
     */
    public Blink1 openByUsbDevice(UsbDevice device) {
        usbManager.requestPermission(device, permissionIntent);
        try {
            Blink1 blink1 = new Blink1(context, device);
            return blink1;
        } catch (Exception e) {
            Log.d("BLINK1FINDER", "exception: " + e);
        }
        return null;
    }

    /**
     * Open first blink(1)
     *
     * @return Blink1 object or null if no blink(1)s are connected
     */
    public Blink1 openFirst() {
        UsbDevice[] devices = enumerate();
        if (devices.length > 0) {
            return openByUsbDevice((devices[0]));
        }
        return null;
    }

    /**
     * Open a blink(1) by its serial number (id)
     * @param id
     * @return Blink1 object or null if no matching blink(1)s
     */
    public Blink1 openById(String id) {
        UsbDevice[] devices = enumerate();
        for( int i=0; i< devices.length; i++) {
            UsbDevice device = devices[i];
            String serial = device.getSerialNumber();
            if( serial == id ) {
                return openByUsbDevice(device);
            }
        }
        return null;
    }

    /**
     * Find all blink(1) UsbDevice objects connected to the computer
     *
     * @return an array of UsbDevice objects with VID and PID matching blink(1)
     */
    private UsbDevice[] enumerate() {
        if (usbManager == null) {
            usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        }

        HashMap<String, UsbDevice> devlist = usbManager.getDeviceList();
        Iterator<UsbDevice> deviter = devlist.values().iterator();
        List<UsbDevice> devices = new ArrayList<UsbDevice>();

        while (deviter.hasNext()) {
            UsbDevice d = deviter.next();
            if (d.getVendorId() == VENDOR_ID && d.getProductId() == PRODUCT_ID) {
                devices.add(d);
            }
        }
        return devices.toArray(new UsbDevice[0]);
    }



}
