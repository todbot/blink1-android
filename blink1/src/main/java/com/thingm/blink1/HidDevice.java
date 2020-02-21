package com.thingm.blink1;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.io.IOException;

// HidDevice from: https://stackoverflow.com/questions/8413260/android-usb-host-3-2-hid-report-get-set

public class HidDevice {
    /** GET_REPORT request code */
    public static final int REQUEST_GET_REPORT = 0x01;
    /** SET_REPORT request code */
    public static final int REQUEST_SET_REPORT = 0x09;
    /** INPUT report type */
    public static final int REPORT_TYPE_INPUT = 0x0100;
    /** OUTPUT report type */
    public static final int REPORT_TYPE_OUTPUT = 0x0200;
    /** FEATURE report type */
    public static final int REPORT_TYPE_FEATURE = 0x0300;

    private Context context;
    private UsbManager manager;
    private UsbDevice device;
    private UsbInterface ifHid = null;
    private UsbEndpoint epIn = null;
    private UsbDeviceConnection connection;

    public HidDevice(Context context, UsbDevice device) throws IOException {
        this.context = context;
        this.device = device;

        for (int i = 0; (this.ifHid == null) && (i < device.getInterfaceCount()); i++)
            if (device.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_HID) {
                this.ifHid = device.getInterface(i);
                for (int j = 0; j < ifHid.getEndpointCount(); j++) {
                    UsbEndpoint ep = ifHid.getEndpoint(j);
                    if ((ep.getDirection() == UsbConstants.USB_DIR_IN) && (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_INT))
                        epIn = ep;
                }
            }
        if (this.ifHid == null)
            throw new IllegalArgumentException("Device has no HID interface");
        else if (this.epIn == null)
            throw new IllegalArgumentException("Device has no INTERRUPT IN endpoint (type USB_ENDPOINT_XFER_INT, direction USB_DIR_IN");

        this.manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.connection = manager.openDevice(device);
        if (!connection.claimInterface(ifHid, true))
            throw new IOException("Failed to claim HID interface");
    }

    public int getFeatureReport(int reportId, byte[] data, int length) {
        if ((reportId & 0xFF) != reportId)
            throw new IllegalArgumentException("reportId may only set the lowest 8 bits");
        return connection.controlTransfer(
                UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_CLASS | UsbConstants.USB_INTERFACE_SUBCLASS_BOOT,
                REQUEST_GET_REPORT,
                reportId | REPORT_TYPE_OUTPUT,
                ifHid.getId(), data, length, 0);
    }

    public int read(byte[] data, int length) {
        return connection.bulkTransfer(epIn, data, length, 0);
    }

    public int sendFeatureReport(int reportId, byte[] data, int length) {
        if ((reportId & 0xFF) != reportId)
            throw new IllegalArgumentException("reportId may only set the lowest 8 bits");
        return connection.controlTransfer(
                UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_CLASS | UsbConstants.USB_INTERFACE_SUBCLASS_BOOT,
                REQUEST_SET_REPORT,
                reportId | REPORT_TYPE_INPUT,
                ifHid.getId(), data, length, 0);
    }
}