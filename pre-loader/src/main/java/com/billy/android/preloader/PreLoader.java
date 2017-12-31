package com.billy.android.preloader;


import com.billy.android.preloader.interfaces.DataListener;
import com.billy.android.preloader.interfaces.DataLoader;
import com.billy.android.preloader.util.ILogger;
import com.billy.android.preloader.util.PreLoaderLogger;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Entrance for pre-load data
 *
 * @author billy.qi <a href="mailto:qiyilike@163.com">Contact me.</a>
 */
public class PreLoader<T> {
    static ILogger logger = new PreLoaderLogger();

    /**
     * set a custom logger
     * @param logger custom logger
     */
    public static void setLogger(ILogger logger) {
        if (logger != null) {
            PreLoader.logger = logger;
        }
    }

    /**
     * enable/disable logger
     * @param show true:enable logger, false:disable logger
     */
    public static void showLog(boolean show) {
        if (logger != null) {
            logger.showLog(show);
        }
    }

    /**
     * start a pre-loader and listen data in the future
     */
    public static <E> PreLoaderWrapper<E> just(DataLoader<E> loader) {
        return just(loader, (DataListener<E>)null);
    }

    /**
     * start a pre-loader for {@link DataListener}
     * @param loader data loader
     * @param listener data listener
     * @return {@link PreLoaderWrapper}
     */
    public static <E> PreLoaderWrapper<E> just(DataLoader<E> loader, DataListener<E> listener) {
        PreLoaderWrapper<E> preLoader = new PreLoaderWrapper<>(loader, listener);
        preLoader.preLoad();
        return preLoader;
    }

    public static <E> PreLoaderWrapper<E> just(DataLoader<E> loader, List<DataListener<E>> listeners) {
        PreLoaderWrapper<E> preLoader = new PreLoaderWrapper<>(loader, listeners);
        preLoader.preLoad();
        return preLoader;
    }

    public static void setDefaultThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        Worker.setDefaultThreadPoolExecutor(threadPoolExecutor);
    }

    /**
     * provide a entrance for singleton {@link PreLoaderPool} <br>
     * this method always returns the same object
     * @return the singleton object of {@link PreLoaderPool}
     */
    public static <E> int preLoad(DataLoader<E> loader, List<DataListener<E>> listeners) {
        return PreLoaderPool.getDefault().preLoad(loader, listeners);
    }

    public static <E> int preLoad(DataLoader<E> loader, DataListener<E> listener) {
        return PreLoaderPool.getDefault().preLoad(loader, listener);
    }

    public static <E> int preLoad(DataLoader<E> loader) {
        return PreLoaderPool.getDefault().preLoad(loader);
    }

    /**
     * provide a entrance for create a new {@link PreLoaderPool} object
     * @return a new object of {@link PreLoaderPool}
     */
    public static PreLoaderPool newPool() {
        return new PreLoaderPool();
    }

    public static boolean listenData(int id) {
        return PreLoaderPool.getDefault().listenData(id);
    }

    public static  <T> boolean listenData(int id, DataListener<T> dataListener) {
        return PreLoaderPool.getDefault().listenData(id, dataListener);
    }

    /**
     * re-load data for all listeners
     * @return success
     */
    public static boolean refresh(int id) {
        return PreLoaderPool.getDefault().refresh(id);
    }

    public static boolean destroy() {
        return PreLoaderPool.getDefault().destroy();
    }
}
