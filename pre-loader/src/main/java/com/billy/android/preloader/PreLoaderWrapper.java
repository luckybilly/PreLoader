package com.billy.android.preloader;

import com.billy.android.preloader.interfaces.DataListener;
import com.billy.android.preloader.interfaces.DataLoader;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * single pre-loader wrapper
 * @author billy.qi
 */
public class PreLoaderWrapper<T> {

    private Worker<T> worker;

    PreLoaderWrapper(DataLoader<T> loader, DataListener<T> listener) {
        this.worker = new Worker<>(loader, listener);
    }
    PreLoaderWrapper(DataLoader<T> loader, List<DataListener<T>> listeners) {
        this.worker = new Worker<>(loader, listeners);
    }

    boolean preLoad() {
        return this.worker.preLoad();
    }

    public void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        worker.setThreadPoolExecutor(threadPoolExecutor);
    }

    /**
     * start to listen data with this dataListener
     * @return true: {@link DataListener#onDataArrived(Object)} will be called<br>
     *          false: there is no {@link DataListener}
     */
    public boolean listenData() {
        return worker.listenData();
    }

    /**
     * start to listen data with this dataListener
     *
     * @param dataListener listener
     * @return true: {@link DataListener#onDataArrived(Object)} will be called<br>
     *          false: there is no {@link DataListener}
     */
    public boolean listenData(DataListener<T> dataListener) {
        return worker.listenData(dataListener);
    }

    public boolean removeListener(DataListener<T> dataListener) {
        return worker.removeListener(dataListener);
    }

    /**
     * re-load data for all listeners
     * @return success
     */
    public boolean refresh() {
        return worker.refresh();
    }

    public boolean destroy() {
        return worker.destroy();
    }
}
