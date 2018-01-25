package com.billy.android.preloader;


import com.billy.android.preloader.interfaces.DataListener;

/**
 * DataListener is listening for data
 * @author billy.qi
 */
class StateListening extends StateBase {
    StateListening(Worker<?> worker) {
        super(worker);
    }

    @Override
    public boolean dataLoadFinished() {
        super.dataLoadFinished();
        return worker.doSendLoadedDataToListenerWork();
    }

    @Override
    public boolean listenData(DataListener listener) {
        super.listenData(listener);
        return worker.doAddListenerWork(listener);
    }

    @Override
    public String name() {
        return "StateListening";
    }
}
