package com.billy.android.preloader;


import com.billy.android.preloader.interfaces.DataListener;

/**
 * data load finished, waiting for {@link DataListener}
 * @author billy.qi
 */
class StateLoadCompleted extends StateBase {
    StateLoadCompleted(Worker<?> worker) {
        super(worker);
    }

    @Override
    public boolean refresh() {
        super.refresh();
        return worker.doStartLoadWork();
    }

    @Override
    public boolean listenData() {
        super.listenData();
        return worker.doSendLoadedDataToListenerWork();
    }

    @Override
    public boolean listenData(DataListener listener) {
        super.listenData(listener);
        return worker.doSendLoadedDataToListenerWork(listener);
    }
    @Override
    public String name() {
        return "StateLoadCompleted";
    }
}
