package com.billy.android.preloader;

import com.billy.android.preloader.interfaces.DataListener;
import com.billy.android.preloader.interfaces.DataLoader;
import com.billy.android.preloader.interfaces.GroupedDataListener;
import com.billy.android.preloader.interfaces.GroupedDataLoader;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.billy.android.preloader.PreLoader.logger;

/**
 * pool for pre-load Worker
 * @author billy.qi
 */
public class PreLoaderPool {

    private static class Inner {
        static final PreLoaderPool INSTANCE = new PreLoaderPool();
    }

    public static PreLoaderPool getDefault() {
        return Inner.INSTANCE;
    }

    private final AtomicInteger idMaker = new AtomicInteger(0);

    private final ConcurrentHashMap<Integer, IWorker> workerMap = new ConcurrentHashMap<>();

    public <T> int preLoad(DataLoader<T> loader) {
        Worker<T> worker = new Worker<>(loader, (DataListener<T>)null);
        return preLoadWorker(worker);
    }

    public <T> int preLoad(DataLoader<T> loader, DataListener<T> listener) {
        Worker<T> worker = new Worker<>(loader, listener);
        return preLoadWorker(worker);
    }

    public <T> int preLoad(DataLoader<T> loader, List<DataListener<T>> listeners) {
        Worker<T> worker = new Worker<>(loader, listeners);
        return preLoadWorker(worker);
    }

    private <T> int preLoadWorker(Worker<T> worker) {
        int id = idMaker.incrementAndGet();
        workerMap.put(id, worker);
        worker.preLoad();
        return id;
    }

    public int preLoadGroup(GroupedDataLoader... loaders) {
        int id = idMaker.incrementAndGet();
        WorkerGroup group = new WorkerGroup(loaders);
        workerMap.put(id, group);
        group.preLoad();
        return id;
    }

    public boolean exists(int id) {
        return workerMap.containsKey(id);
    }

    public boolean listenData(int id) {
        IWorker worker = workerMap.get(id);
        return worker != null && worker.listenData();
    }

    public <T> boolean listenData(int id, DataListener<T> dataListener) {
        try {
            IWorker worker = workerMap.get(id);
            return worker != null && worker.listenData(dataListener);
        } catch(Exception e) {
            logger.throwable(e);
        }
        return false;
    }

    public boolean listenData(int id, GroupedDataListener... listeners) {
        try {
            IWorker worker = workerMap.get(id);
            if (worker != null) {
                for (GroupedDataListener listener : listeners) {
                    worker.listenData(listener);
                }
            }
        } catch(Exception e) {
            logger.throwable(e);
        }
        return false;
    }

    public <T> boolean removeListener(int id, DataListener<T> dataListener) {
        try {
            IWorker worker = workerMap.get(id);
            return worker != null && worker.removeListener(dataListener);
        } catch(Exception e) {
            logger.throwable(e);
        }
        return false;
    }

    public boolean refresh(int id) {
        IWorker worker = workerMap.get(id);
        return worker != null && worker.refresh();
    }

    public boolean destroy(int id) {
        IWorker worker = workerMap.remove(id);
        return worker != null && worker.destroy();
    }

    public boolean destroyAll() {
        for (IWorker worker : workerMap.values()) {
            if (worker != null) {
                try {
                    worker.destroy();
                } catch(Exception e) {
                    logger.throwable(e);
                }
            }
        }
        workerMap.clear();
        idMaker.set(0);
        return true;
    }

}
