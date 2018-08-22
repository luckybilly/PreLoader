package com.billy.android.preloader;


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
    public String name() {
        return "StatusInitialed";
    }
}
