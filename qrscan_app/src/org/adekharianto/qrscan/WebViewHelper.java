package org.adekharianto.qrscan;

import android.app.Activity;
import android.webkit.WebChromeClient;
import android.webkit.PermissionRequest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

public class WebViewHelper {
    
    private static final String TAG = "WebViewHelper";
    private Activity activity;
    
    public WebViewHelper(Activity activity) {
        this.activity = activity;
    }
    
    /**
     * WebChromeClient untuk handle media permissions (camera, microphone)
     */
    public static class MediaWebChromeClient extends WebChromeClient {
        private Activity activity;
        
        public MediaWebChromeClient(Activity activity) {
            this.activity = activity;
        }
        
        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (request != null) {
                    String[] resources = request.getResources();
                    for (String resource : resources) {
                        if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(resource)) {
                            if (ContextCompat.checkSelfPermission(
                                    activity,
                                    android.Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(
                                        activity,
                                        new String[]{android.Manifest.permission.CAMERA},
                                        1);
                            }
                        }
                    }
                    request.grant(request.getResources());
                }
            }
        }
    }
    
    /**
     * Simpan file XLSX dari base64 ke folder Download
     */
    public boolean saveXlsxFile(String base64Data, String filename) {
        try {
            byte[] decodedData;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                decodedData = Base64.getDecoder().decode(base64Data);
            } else {
                decodedData = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
            }
            
            File downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            
            File file = new File(downloadsDir, filename);
            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedData);
                fos.flush();
            }
            
            Log.i(TAG, "File saved: " + file.getAbsolutePath());
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Minta permission untuk camera
     */
    public boolean requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{android.Manifest.permission.CAMERA},
                        1);
                return false;
            }
        }
        return true;
    }
}
