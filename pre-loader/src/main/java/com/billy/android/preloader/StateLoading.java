package com.billy.android.preloader;


import com.billy.android.preloader.interfaces.DataListener;

/**
 * state for loading data
 * @author billy.qi
 */
class StateLoading extends StateBase {
    StateLoading(Worker<?> worker) {
        super(worker);
    }

    /**
     * ready for get data
     * @return true: {@link DataListener#onDataArrived(Object)} will be called
     *          false: {@link Worker} has no {@link DataListener}
     */
    @Override
    public boolean listenData() {
        super.listenData();
        return worker.doWaitForDataLoaderWork();
    }

    @Override
    public boolean listenData(DataListener listener) {
        super.listenData(listener);
        return worker.doWaitForDataLoaderWork(listener);
    }

    /**
     * data has loaded, waiting for {@link DataListener}
     * @return true
     */
    @Override
    public boolean dataLoadFinished() {
        super.dataLoadFinished();
        return worker.doDataLoadFinishWork();
    }
    @Override
    public String name() {
        return "StateLoading";
    }
}
