/**
 * @author Karolis
 */

package com.example.csc2033_team19_stubank;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import de.taimos.totp.TOTP;

/* Author - Karolis Zilius
        This class is for the 2FA functionality using Google Authenticator.*/

public class TwoFactorAuthentication {

    // Generate the secret key for Google Authenticator App
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    // Convert the secret key into a barcode to scan in an URL format
    // Account - user it in the system (email address)
    // Secret key - the secret key user generated
    // Issuer - StuBank
    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    // Generate a QR code
    public static Bitmap createQRCode(String barCodeData, int height, int width)
            throws WriterException {

        // Create the barcode picture
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x=0; x<width; x++){
            for (int y=0; y<height; y++){

                bitmap.setPixel(x,y,matrix.get(x,y)? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    // Get the 6 digit code with the entered 6 digit key
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
}
