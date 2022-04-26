package com.oz.unitel.econtract;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import oz.toto.framework.OZTotoError;
import oz.toto.framework.OZTotoEvent;
import oz.toto.framework.OZTotoEventHandler;
import oz.toto.framework.OZTotoRuntime;
import oz.toto.framework.OZTotoWebView;
import oz.toto.framework.OZTotoWebViewListener;

//import static oz.toto.framework.OZTotoWebView.NAVIGATOR_VISIBLE;

public class MainActivity extends AppCompatActivity {

    FrameLayout parentView = null;
    OZTotoWebView toto = null;
    OZTotoRuntime toto_runtime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestAppPermission(this);

        parentView = new FrameLayout(this);
        toto = new OZTotoWebView(this);
        OZTotoWebView.setDebugMode(true);
        parentView.addView(toto, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(parentView);
        OZTotoWebView.setDebugMode(true);
        WebView.setWebContentsDebuggingEnabled(true);
        String url = "http://eform.uni/eCONTRACT/";
        String pageName = "";
        String param = "";

        toto.setTotoWebViewListener(new OZTotoWebViewListener() {
            @Override
            public void onPageLoad(final OZTotoRuntime runtime) {
                runtime.getFramework().addEventListener("_exitApp_", OZTotoFrameworkEvent);
                toto_runtime = runtime;
            }
            @Override
            public void onPageUnload(OZTotoRuntime runtime) {
                toto_runtime = null;
            }
            @Override
            public void onSelectedMenuButton() {
            }
            @Override
            public void onPageLoadFailure(OZTotoError ozTotoError) {
                Log.e("error", ozTotoError.toString());
                home();
            }
            OZTotoEventHandler OZTotoFrameworkEvent = new OZTotoEventHandler() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onEvent(OZTotoEvent e) {
                    if (e.eventName.equals("_exitApp_")) {
                        exit();
                    }
                }
            };
        });
        toto.run(url, pageName, param);
    }

    private void exit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
            }
        });
    }

    private void home() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Cannot connect to the server.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (toto.canGoBack()) {
                toto.goBack();
            } else exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    static boolean requestAppPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 28) {
            String[] need = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE};
            for (int i = 0; i < need.length; i++) {
                if (context.checkSelfPermission(need[i]) != PackageManager.PERMISSION_GRANTED) {
                    if (context instanceof Activity) {
                        ((Activity) context).requestPermissions(need, 1);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    static void onRequestPermissionsResultCustom(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && Build.VERSION.SDK_INT >= 28) {
            boolean isallgranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isallgranted = false;
                }
            }
            if (isallgranted) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResultCustom(requestCode, permissions, grantResults);
    }

}