package com.billy.android.preloader;


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
    public String name() {
        return "StateListening";
    }
}
