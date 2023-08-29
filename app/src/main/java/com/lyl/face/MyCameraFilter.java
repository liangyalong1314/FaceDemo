package com.lyl.face;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraFilter;
import androidx.camera.core.CameraInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 作者：梁亚龙
 * 时间：2023/3/28
 * 描述：
 **/

public class MyCameraFilter implements CameraFilter {
        int i;
    public  MyCameraFilter(int i){
        this.i=i;
    }
    @NonNull
    @Override
    @SuppressLint("RestrictedApi")
    public List<CameraInfo> filter(@NonNull List<CameraInfo> cameras) {
        Log.i("TAG", "cameras size: " + cameras.size());
        CameraInfo camera = null;


        camera = cameras.get(i);

        String getImplementationType = camera.getImplementationType();
        Log.i("TAG", "getImplementationType: " + getImplementationType);
        List linkedHashSet = new ArrayList();
        linkedHashSet.add(camera);
        // 最后一个camera
        return linkedHashSet;
    }
}