package com.q.argument;

/**
 * Created by paulachen on 6/6/17.
 */

import android.app.ActivityManager;
import android.app.Service;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Arrays;
import java.util.Locale;


public class GenerateQ {
    private static String openGLExtensions = "";

    private static String[] supportedOpenGLExtensions = {
            "GL_OES_compressed_ETC1_RGB8_texture", "GL_OES_compressed_paletted_texture",
            "GL_AMD_compressed_3DC_texture", "GL_AMD_compressed_ATC_texture",
            "GL_EXT_texture_compression_latc", "GL_EXT_texture_compression_dxt1",
            "GL_EXT_texture_compression_s3tc", "GL_ATI_texture_compression_atitc",
            "GL_IMG_texture_compression_pvrtc"
    };

    private enum size {notfound, small, normal, large, xlarge}


    //call this method to get the Q string base64
    public static String generateQ(Context context) {

        return Base64.encodeToString(getFilters(context).getBytes(), 0)
                .replace("=", "")
                .replace("/", "*")
                .replace("+", "_")
                .replace("\n", "");
    }


    public static String getFilters(Context context) {

        //openGLExtensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);

        /*
    For more information on how to set openGLExtensions, please read the documentation:
    https://developer.android.com/guide/topics/graphics/opengl.html
        */

        int minSdk = Build.VERSION.SDK_INT;
        String minScreen = size.values()
                [getScreenSizeInt(context)].name().toLowerCase(Locale.ENGLISH);
        String minGlEs = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion();
        final int density = getDensityDpi(context);
        String cpuAbi = getAbis();
        String filters = (Build.DEVICE.equals("alien_jolla_bionic") ? "apkdwn=myapp&" : "")
                + "maxSdk=" + minSdk + "&maxScreen=" + minScreen + "&maxGles=" + minGlEs
                + "&myCPU=" + cpuAbi + "&myDensity=" + density + "&leanback=" + (isTV(context) ? 1 : 0);
        filters = addOpenGLExtensions(filters);

        return filters;

    }


    private static String addOpenGLExtensions(String filters) {

        boolean extensionAdded = false;

        for (String extension : openGLExtensions.split(" ")) {
            if (Arrays.asList(supportedOpenGLExtensions).contains(extension)) {
                if (!extensionAdded) {
                    filters += "&myGLTex=" + extension;
                } else {
                    filters += "," + extension;
                }
                extensionAdded = true;
            }
        }
        return filters;

    }

    private static int getScreenSizeInt(Context context) {

        return context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;

    }

    private static String getAbis() {

        final String[] abis = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? Build.SUPPORTED_ABIS : new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < abis.length; i++) {
            builder.append(abis[i]);
            if (i < abis.length - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    private static int getDensityDpi(Context context) {

        DisplayMetrics metrics = new DisplayMetrics();

        ((WindowManager) context.getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        int dpi = metrics.densityDpi;

        if (dpi <= 120) {
            dpi = 120;
        } else if (dpi <= 160) {
            dpi = 160;
        } else if (dpi <= 213) {
            dpi = 213;
        } else if (dpi <= 240) {
            dpi = 240;
        } else if (dpi <= 320) {
            dpi = 320;
        } else if (dpi <= 480) {
            dpi = 480;
        } else {
            dpi = 640;
        }
        return dpi;
    }

    private static boolean isTV(Context context) {

        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }
}