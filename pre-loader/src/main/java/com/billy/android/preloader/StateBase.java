package com.billy.android.preloader;

import com.billy.android.preloader.interfaces.DataListener;

import static com.billy.android.preloader.PreLoader.logger;


/**
 * basic state
 *
 * @author billy.qi
 */
abstract class StateBase implements State {

    protected Worker<?> worker;

    StateBase(Worker<?> worker) {
        this.worker = worker;
    }

    @Override
    public boolean destroy() {
        log("destroy");
        if (this instanceof StateDestroyed) {
            return false;
        } else {
            return worker.doDestroyWork();
        }
    }

    @Override
    public boolean startLoad() {
        log("startLoad()");
        return false;
    }

    @Override
    public boolean listenData() {
        log("listenData()");
        return false;
    }

    @Override
    public boolean listenData(DataListener listener) {
        log("listenData(listener)");
        return false;
    }

    @Override
    public boolean removeListener(DataListener listener) {
        log("removeListener");
        return worker.doRemoveListenerWork(listener);
    }

    @Override
    public boolean dataLoadFinished() {
        log("dataLoadFinished()");
        return false;
    }

    @Override
    public boolean refresh() {
        log("refresh()");
        return false;
    }

    private void log(String str) {
        logger.info(name() + "--->>> " + str);
    }
}
