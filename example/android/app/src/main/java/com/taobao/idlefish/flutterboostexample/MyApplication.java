package com.taobao.idlefish.flutterboostexample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.idlefish.flutterboost.NewFlutterBoost;
import com.idlefish.flutterboost.Platform;
import com.idlefish.flutterboost.interfaces.INativeRouter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.android.FlutterView;
import io.flutter.plugin.common.MethodChannel;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        INativeRouter router =new INativeRouter() {
            @Override
            public void openContainer(Context context, String url, Map<String, Object> urlParams, int requestCode, Map<String, Object> exts) {
               String  assembleUrl=assembleUrl(url,urlParams);
                PageRouter.openPageByUrl(context,assembleUrl, urlParams);
            }

        };

        NewFlutterBoost.BoostLifecycleListener lifecycleListener= new NewFlutterBoost.BoostLifecycleListener() {
            @Override
            public void onEngineCreated() {

            }

            @Override
            public void onPluginsRegistered() {
                MethodChannel mMethodChannel = new MethodChannel( NewFlutterBoost.instance().engineProvider().getDartExecutor(), "methodChannel");
                Log.e("MyApplication","MethodChannel create");
                TextPlatformViewPlugin.register(NewFlutterBoost.instance().getPluginRegistry().registrarFor("TextPlatformViewPlugin"));

            }

            @Override
            public void onEngineDestroy() {

            }
        };
        Platform platform= new NewFlutterBoost
                .ConfigBuilder(this,router)
                .isDebug(true)
                .whenEngineStart(NewFlutterBoost.ConfigBuilder.ANY_ACTIVITY_CREATED)
                .renderMode(FlutterView.RenderMode.texture)
                .lifecycleListener(lifecycleListener)
                .build();

        NewFlutterBoost.instance().init(platform);



    }

    public static String assembleUrl(String url, Map<String, Object> urlParams) {

        StringBuilder targetUrl = new StringBuilder(url);
        if (urlParams != null && !urlParams.isEmpty()) {
            if (!targetUrl.toString().contains("?")) {
                targetUrl.append("?");
            }

            for (Map.Entry entry : urlParams.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> params = (Map<String, Object>) entry.getValue();

                    for (Map.Entry param : params.entrySet()) {
                        String key = (String) param.getKey();
                        String value = null;
                        if (param.getValue() instanceof Map || param.getValue() instanceof List) {
                            try {
                                value = URLEncoder.encode(JSON.toJSONString(param.getValue()), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            value = (param.getValue() == null ? null : URLEncoder.encode(String.valueOf(param.getValue())));
                        }

                        if (value == null) {
                            continue;
                        }
                        if (targetUrl.toString().endsWith("?")) {
                            targetUrl.append(key).append("=").append(value);
                        } else {
                            targetUrl.append("&").append(key).append("=").append(value);
                        }

                    }
                }

            }

        }
        return targetUrl.toString();
    }


}
