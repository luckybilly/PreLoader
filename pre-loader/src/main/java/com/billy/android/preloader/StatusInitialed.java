package com.billy.android.preloader;


import com.billy.android.preloader.interfaces.DataListener;

/**
 * initial state
 * @author billy.qi
 */
class StatusInitialed extends StateBase {

    StatusInitialed(Worker<?> worker) {
        super(worker);
    }

    @Override
    public boolean startLoad() {
        super.startLoad();
        return worker.doStartLoadWork();
    }

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

    @Override
    public String name() {
        return "StatusInitialed";
    }
}
