package com.billy.android.preloader;

import android.text.TextUtils;

import com.billy.android.preloader.interfaces.DataListener;
import com.billy.android.preloader.interfaces.GroupedDataListener;
import com.billy.android.preloader.interfaces.GroupedDataLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import static com.billy.android.preloader.PreLoader.logger;

/**
 * works with a group of {@link GroupedDataLoader}
 * @author billy.qi
 * @since 18/1/23 14:32
 */
class WorkerGroup implements IWorker {
    private Collection<Worker> workers;

    WorkerGroup(GroupedDataLoader[] loaders) {
        if (loaders != null) {
            HashMap<String, Worker> map = new HashMap<>(loaders.length);
            for (GroupedDataLoader loader : loaders) {
                String key = loader.keyInGroup();
                if (TextUtils.isEmpty(key)) {
                    logger.warning("GroupedDataLoader with no key:"
                            + loader.getClass().getName());
                }
                Worker old = map.put(key, new Worker(loader, (DataListener) null));
                if (old != null) {
                    logger.error("More than 1 loaders with same key:("
                            + loader.getClass().getName()
                            + ", " + old.getClass().getName()
                            + "). " + old.getClass().getName() + " will be skipped."
                    );
                }
            }
            this.workers = map.values();
        } else {
            this.workers = new ArrayList<>();
        }
    }

    @Override
    public void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        for (Worker worker : workers) {
            worker.setThreadPoolExecutor(threadPoolExecutor);
        }
    }

    @Override
    public boolean preLoad() {
        boolean success = true;
        for (Worker worker : workers) {
            success &= worker.preLoad();
        }
        return success;
    }

    @Override
    public boolean refresh() {
        boolean success = true;
        for (Worker worker : workers) {
            success &= worker.refresh();
        }
        return success;
    }

    @Override
    public boolean listenData(DataListener dataListener) {
        boolean success = true;
        String key = null;
        if (dataListener != null && dataListener instanceof GroupedDataListener) {
            key = ((GroupedDataListener)dataListener).keyInGroup();
        }
        for (Worker worker : workers) {
            if (!TextUtils.isEmpty(key) && worker.dataLoader instanceof GroupedDataLoader) {
                GroupedDataLoader loader = (GroupedDataLoader) worker.dataLoader;
                if (key.equals(loader.keyInGroup())) {
                    success &= worker.listenData(dataListener);
                }
            }
        }
        return success;
    }

    @Override
    public boolean listenData() {
        boolean success = true;
        for (Worker worker : workers) {
            success &= worker.listenData();
        }
        return success;
    }

    @Override
    public boolean removeListener(DataListener listener) {
        boolean success = true;
        for (Worker worker : workers) {
            success &= worker.removeListener(listener);
        }
        return success;
    }

    @Override
    public boolean destroy() {
        boolean success = true;
        for (Worker worker : workers) {
            success &= worker.destroy();
        }
        workers.clear();
        return success;
    }
}
