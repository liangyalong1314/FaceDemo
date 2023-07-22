package com.lyl.face.demo;

/**
 * 作者：梁亚龙
 * 时间：2023/3/28
 * 描述：
 **/

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.camera.core.internal.CameraUseCaseAdapter.CameraId;

import androidx.lifecycle.LifecycleOwner;

final class AutoValue_LifecycleCameraRepository_Key extends LifecycleCameraRepository.Key {
    private final LifecycleOwner lifecycleOwner;
    private final CameraId cameraId;

    AutoValue_LifecycleCameraRepository_Key(LifecycleOwner lifecycleOwner, CameraId cameraId) {
        if (lifecycleOwner == null) {
            throw new NullPointerException("Null lifecycleOwner");
        } else {
            this.lifecycleOwner = lifecycleOwner;
            if (cameraId == null) {
                throw new NullPointerException("Null cameraId");
            } else {
                this.cameraId = cameraId;
            }
        }
    }

    @Override
    @NonNull
    public LifecycleOwner getLifecycleOwner() {
        return this.lifecycleOwner;
    }

    @Override
    @NonNull
    public CameraId getCameraId() {
        return this.cameraId;
    }

    @Override
    public String toString() {
        return "Key{lifecycleOwner=" + this.lifecycleOwner + ", cameraId=" + this.cameraId + "}";
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof LifecycleCameraRepository.Key)) {
            return false;
        } else {
            LifecycleCameraRepository.Key that = (LifecycleCameraRepository.Key) o;
            return this.lifecycleOwner.equals(that.getLifecycleOwner()) && this.cameraId.equals(that.getCameraId());
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public int hashCode() {
        int h$ = 1;
         h$ = h$ * 1000003;
        h$ ^= this.lifecycleOwner.hashCode();
        h$ *= 1000003;
        h$ ^= this.cameraId.hashCode();
        return h$;
    }
}

