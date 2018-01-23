package com.billy.android.preloader;


import com.billy.android.preloader.interfaces.DataListener;
import com.billy.android.preloader.interfaces.DataLoader;
import com.billy.android.preloader.interfaces.GroupedDataListener;
import com.billy.android.preloader.interfaces.GroupedDataLoader;
import com.billy.android.preloader.util.ILogger;
import com.billy.android.preloader.util.PreLoaderLogger;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Entrance for pre-load data
 *
 * @author billy.qi <a href="mailto:qiyilike@163.com">Contact me.</a>
 */
public class PreLoader {
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
     * you can handle the {@link PreLoaderWrapper} object to do whatever you want
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

    /**
     * set a custom thread pool for all pre-load tasks
     * @param threadPoolExecutor thread pool
     */
    public static void setDefaultThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        Worker.setDefaultThreadPoolExecutor(threadPoolExecutor);
    }

    /**
     * provide a entrance for pre-load data in singleton {@link PreLoaderPool} <br>
     * @param loader data loader
     * @param listeners listeners start work after both of loader.loadData() completed and PreLoader.listenData(id) called
     * @return id for this loader
     */
    public static <E> int preLoad(DataLoader<E> loader, List<DataListener<E>> listeners) {
        return PreLoaderPool.getDefault().preLoad(loader, listeners);
    }

    public static <E> int preLoad(DataLoader<E> loader, DataListener<E> listener) {
        return PreLoaderPool.getDefault().preLoad(loader, listener);
    }

    /**
     * provide a entrance for pre-load data in singleton {@link PreLoaderPool} <br>
     * @param loader data loader
     * @return id for this loader
     */
    public static <E> int preLoad(DataLoader<E> loader) {
        return PreLoaderPool.getDefault().preLoad(loader);
    }

    /**
     * pre-load data with a group loaders in only one pre-load-id
     * eg. pre-load data in an activity with lots of {@link DataLoader}
     * @param loaders
     * @return
     */
    public static int preLoad(GroupedDataLoader... loaders) {
        return PreLoaderPool.getDefault().preLoadGroup(loaders);
    }

    /**
     * provide a entrance for create a new {@link PreLoaderPool} object
     * @return a new object of {@link PreLoaderPool}
     */
    public static PreLoaderPool newPool() {
        return new PreLoaderPool();
    }

    /**
     * start to listen data by id<br>
     * 1. if pre-load task starts via {@link #preLoad(DataLoader, DataListener)} or {@link #preLoad(DataLoader, List)}
     * the listeners will be called when {@link DataLoader#loadData()} completed<br>
     * 2. if pre-load task starts via {@link #preLoad(DataLoader)}, 
     *  and call this method without any {@link DataListener}, 
     *  the pre-load only change the state to {@link StateDone},
     *  you can get data later by {@link #preLoad(DataLoader, DataListener)} and {@link #preLoad(DataLoader, List)}
     * @param id the id returns by {@link #preLoad(DataLoader)}
     * @return success or not
     */
    public static boolean listenData(int id) {
        return PreLoaderPool.getDefault().listenData(id);
    }

    /**
     * start to listen data with {@link DataListener} by id<br>
     * @see #listenData(int)  PreLoader.listenData(int) for more details
     * @param id the id returns by {@link #preLoad(DataLoader)}
     * @param dataListener the dataListener.onDataArrived(data) will be called if data load is completed
     * @return success or not
     */
    public static  <T> boolean listenData(int id, DataListener<T> dataListener) {
        return PreLoaderPool.getDefault().listenData(id, dataListener);
    }

    public static boolean listenData(int id, GroupedDataListener... listeners) {
        return PreLoaderPool.getDefault().listenData(id, listeners);
    }

    /**
     * remove a specified {@link DataListener} for the pre-load task by id
     * @param id the id returns by {@link #preLoad(DataLoader)}
     * @param dataListener the listener to remove
     * @return success or not
     */
    public static <T> boolean removeListener(int id, DataListener<T> dataListener) {
        return PreLoaderPool.getDefault().removeListener(id, dataListener);
    }

    /**
     * check the pre-load task is exists in singleton {@link PreLoaderPool}
     * @param id the id returns by {@link #preLoad(DataLoader)}
     * @return exists or not
     */
    public static boolean exists(int id) {
        return PreLoaderPool.getDefault().exists(id);
    }

    /**
     * re-load data for all listeners
     * @return success
     */
    public static boolean refresh(int id) {
        return PreLoaderPool.getDefault().refresh(id);
    }

    /**
     * destroy the pre-load task by id.
     * call this method for remove loader and all listeners, and will not accept any listeners
     * @param id the id returns by {@link #preLoad(DataLoader)}
     * @return success or not
     */
    public static boolean destroy(int id) {
        return PreLoaderPool.getDefault().destroy(id);
    }

    /**
     * destroy all pre-load tasks in singleton {@link PreLoaderPool}
     * @return success or not
     */
    public static boolean destroyAll() {
        return PreLoaderPool.getDefault().destroyAll();
    }
}
