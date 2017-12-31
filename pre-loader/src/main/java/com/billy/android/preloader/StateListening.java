package com.billy.android.preloader;


/**
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
