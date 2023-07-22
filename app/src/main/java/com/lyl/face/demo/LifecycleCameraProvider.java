package com.lyl.face.demo;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.UseCase;
import androidx.lifecycle.Lifecycle;

/**
 * 作者：梁亚龙
 * 时间：2023/3/28
 * 描述：
 **/
public interface LifecycleCameraProvider extends CameraProvider {
    /**
     * Returns true if the {@link UseCase} is bound to a lifecycle. Otherwise returns false.
     *
     * <p>After binding a use case, use cases remain bound until the lifecycle reaches a
     * {@link Lifecycle.State#DESTROYED} state or if is unbound by calls to
     * {@link #unbind(UseCase...)} or {@link #unbindAll()}.
     */
    boolean isBound(@NonNull UseCase useCase);

    /**
     * Unbinds all specified use cases from the lifecycle provider.
     *
     * <p>This will initiate a close of every open camera which has zero {@link UseCase}
     * associated with it at the end of this call.
     *
     * <p>If a use case in the argument list is not bound, then it is simply ignored.
     *
     * <p>After unbinding a UseCase, the UseCase can be bound to another {@link Lifecycle}
     * however listeners and settings should be reset by the application.
     *
     * @param useCases The collection of use cases to remove.
     * @throws IllegalStateException If not called on main thread.
     */
    void unbind(@NonNull UseCase... useCases);

    /**
     * Unbinds all use cases from the lifecycle provider and removes them from CameraX.
     *
     * <p>This will initiate a close of every currently open camera.
     *
     * @throws IllegalStateException If not called on main thread.
     */
    void unbindAll();
}
