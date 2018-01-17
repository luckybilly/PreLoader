package com.billy.android.preloader;


import com.billy.android.preloader.interfaces.DataListener;

/**
 * state of the pre-loader
 * @author billy.qi
 */
interface State {

    /**
     * start to load data
     * @return
     */
    boolean startLoad();

    /**
     * 销毁
     * @return
     */
    boolean destroy();

    /**
     * start to listen data
     * @return
     */
    boolean listenData();

    /**
     * start to listen data with a listener
     * @param listener listener of data
     * @return true if success
     */
    boolean listenData(DataListener listener);

    /**
     * remove listener
     * @param listener listener of data
     * @return true
     */
    boolean removeListener(DataListener listener);

    /**
     * data load finished
     * @return
     */
    boolean dataLoadFinished();

    /**
     * re-load data for all {@link DataListener}
     * @return success
     */
    boolean refresh();

    /**
     * name of the state
     * @return state name
     */
    String name();
}
