package com.billy.android.preloader;

import android.os.Handler;
import android.os.Looper;

import com.billy.android.preloader.interfaces.DataListener;
import com.billy.android.preloader.interfaces.DataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.billy.android.preloader.PreLoader.logger;


/**
 * pre-load data <br>
 * @author billy.qi <a href="mailto:qiyilike@163.com">Contact me.</a>
 */
class Worker<T> implements Runnable {

    private static final ThreadFactory FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("pre-loader-pool-" + thread.getId());
            return thread;
        }
    };

    private static ExecutorService defaultThreadPoolExecutor = new ThreadPoolExecutor(2, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), FACTORY);

    private ExecutorService threadPoolExecutor;

    private T loadedData;
    private final List<DataListener<T>> dataListeners = new CopyOnWriteArrayList<>();
    private DataLoader<T> dataLoader;
    private volatile State state;

    Worker(DataLoader<T> loader, DataListener<T> listener) {
        init(loader);
        if (listener != null) {
            this.dataListeners.add(listener);
        }
    }

    Worker(DataLoader<T> loader, List<DataListener<T>> listeners) {
        init(loader);
        if (listeners != null) {
            this.dataListeners.addAll(listeners);
        }
    }

    private void init(DataLoader<T> loader) {
        this.dataLoader = loader;
        setState(new StatusInitialed(this));
    }

    static void setDefaultThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        if (threadPoolExecutor != null) {
            defaultThreadPoolExecutor = threadPoolExecutor;
        }
    }

    void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        if (threadPoolExecutor != null) {
            this.threadPoolExecutor = threadPoolExecutor;
        }
    }

    /**
     * start to load data
     */
    boolean preLoad() {
        return state.startLoad();
    }

    boolean doStartLoadWork() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.execute(this);
        } else {
            defaultThreadPoolExecutor.execute(this);
        }
        setState(new StateLoading(this));
        return true;
    }

    boolean refresh() {
        return state.refresh();
    }

    boolean listenData(DataListener<T> dataListener) {
        if (dataListener != null) {
            this.dataListeners.add(dataListener);
            return state.listenData(dataListener);
        } else {
            return false;
        }
    }

    boolean listenData() {
        return state.listenData();
    }

    boolean doDataLoadFinishWork() {
        setState(new StateLoadCompleted(this));
        return true;
    }

    boolean doSendLoadedDataToListenerWork() {
        return doSendLoadedDataToListenerWork(dataListeners);
    }

    boolean doSendLoadedDataToListenerWork(DataListener<T> listener) {
        List<DataListener<T>> listeners = new ArrayList<>(1);
        listeners.add(listener);
        return doSendLoadedDataToListenerWork(listeners);
    }
    private boolean doSendLoadedDataToListenerWork(final List<DataListener<T>> listeners) {
        if (!(state instanceof StateDone)) {
            setState(new StateDone(this));
        }
        if (!listeners.isEmpty()) {
            if (isMainThread()) {
                safeListenData(listeners, loadedData);
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        safeListenData(listeners, loadedData);
                    }
                });
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * waiting for {@link DataLoader#loadData()} finish
     * @return false if no {@link DataListener}, true otherwise
     */
    boolean doWaitForDataLoaderWork() {
        if (dataListeners.isEmpty()) {
            return false;
        }
        // change current state to StateListening
        setState(new StateListening(this));
        return true;
    }

    boolean destroy() {
        return state.destroy();
    }

    boolean doDestroyWork() {
        setState(new StateDestroyed(this));
        dataListeners.clear();
        dataLoader = null;
        threadPoolExecutor = null;
        return true;
    }

    /**
     * load data on HandlerThread
     * if state is {@link StateListening} : send data to {@link DataListener}
     * if state is {@link StateLoading} : change state to {@link StateLoadCompleted}
     * add worker to MessageQueue as IdleHandler
     */
    @Override
    public void run() {
        try {
            //load data (from network or local i/o)
            loadedData = dataLoader.loadData();
        } catch(Exception e) {
            logger.throwable(e);
        }
        state.dataLoadFinished();
    }

    private void safeListenData(List<DataListener<T>> listeners, T t) {
        for (DataListener<T> listener : listeners) {
            try {
                listener.onDataArrived(t);
            } catch(Exception e) {
                logger.throwable(e);
            }
        }
    }

    private boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private void setState(State state) {
        if (state != null) {
            this.state = state;
            logger.info("set state to:" + state.name());
        }
    }
}
