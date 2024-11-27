package com.example.soroban;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Generates QR code images as {@link Bitmap} objects.
 * Uses the ZXing library for QR code generation.
 *
 * @author Edwin Manalastas
 * @see Bitmap
 * @see com.google.zxing.qrcode.QRCodeWriter
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code bitmap from a given text.
     *
     * @param text the text to encode into the QR code.
     * @return a {@link Bitmap} containing the QR code, or null if an error occurs.
     * @see com.google.zxing.qrcode.QRCodeWriter
     * @see com.google.zxing.common.BitMatrix
     */
    // Method to generate a QR Code bitmap
    public static Bitmap generateQRCode(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
