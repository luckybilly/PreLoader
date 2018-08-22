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
class Worker<T> implements Runnable, IWorker {

    private static final ThreadFactory FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("pre-loader-pool-" + thread.getId());
            return thread;
        }
    };

    private static ExecutorService defaultThreadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), FACTORY);

    private ExecutorService threadPoolExecutor;

    private T loadedData;
    private final List<DataListener<T>> dataListeners = new CopyOnWriteArrayList<>();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    DataLoader<T> dataLoader;
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

    @Override
    public void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        if (threadPoolExecutor != null) {
            this.threadPoolExecutor = threadPoolExecutor;
        }
    }

    /**
     * start to load data
     */
    @Override
    public boolean preLoad() {
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

    @Override
    public boolean refresh() {
        return state.refresh();
    }

    @Override
    public boolean listenData(DataListener dataListener) {
        return state.listenData(dataListener);
    }

    @Override
    public boolean listenData() {
        return state.listenData();
    }

    @Override
    public boolean removeListener(DataListener listener) {
        return state.removeListener(listener);
    }

    boolean doRemoveListenerWork(DataListener<T> listener) {
        return dataListeners.remove(listener);
    }

    boolean doDataLoadFinishWork() {
        setState(new StateLoadCompleted(this));
        return true;
    }

    boolean doSendLoadedDataToListenerWork() {
        return doSendLoadedDataToListenerWork(dataListeners);
    }

    boolean doAddListenerWork(DataListener<T> listener) {
        if (listener != null) {
            if (!this.dataListeners.contains(listener)) {
                this.dataListeners.add(listener);
            }
            return true;
        }
        return false;
    }

    boolean doSendLoadedDataToListenerWork(DataListener<T> listener) {
        doAddListenerWork(listener);
        List<DataListener<T>> listeners = null;
        if (listener != null) {
            listeners = new ArrayList<>(1);
            listeners.add(listener);
        }
        return doSendLoadedDataToListenerWork(listeners);
    }
    private boolean doSendLoadedDataToListenerWork(final List<DataListener<T>> listeners) {
        if (!(state instanceof StateDone)) {
            setState(new StateDone(this));
        }
        if (listeners != null && !listeners.isEmpty()) {
            if (isMainThread()) {
                safeListenData(listeners, loadedData);
            } else {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        safeListenData(listeners, loadedData);
                    }
                });
            }
        }
        return true;
    }

    boolean doWaitForDataLoaderWork(DataListener<T> listener) {
        if (listener != null) {
            dataListeners.add(listener);
        }
        return doWaitForDataLoaderWork();
    }
    /**
     * waiting for {@link DataLoader#loadData()} finish
     * @return false if no {@link DataListener}, true otherwise
     */
    boolean doWaitForDataLoaderWork() {
        // change current state to StateListening
        setState(new StateListening(this));
        return true;
    }

    @Override
    public boolean destroy() {
        return state.destroy();
    }

    boolean doDestroyWork() {
        setState(new StateDestroyed(this));
        mainThreadHandler.removeCallbacksAndMessages(null);
        dataListeners.clear();
        dataLoader = null;
        threadPoolExecutor = null;
        return true;
    }

    /**
     * load data in thread-pool
     * if state is {@link StateListening} : send data to {@link DataListener}
     * if state is {@link StateLoading} : change state to {@link StateLoadCompleted}
     */
    @Override
    public void run() {
        try {
            loadedData = null;
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
            if (this.state != null) {
                if (this.state.getClass() == state.getClass()) {
                    return;
                }
            }
            this.state = state;
            logger.info("set state to:" + state.name());
        }
    }
}
