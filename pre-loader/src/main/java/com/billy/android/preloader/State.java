package com.billy.android.preloader;


import com.billy.android.preloader.interfaces.DataListener;

/**
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
     * 开始监听加载的数据（如果已经有数据则立即开始处理数据）
     * @return
     */
    boolean listenData();

    /**
     *
     * @param listener
     * @return
     */
    boolean listenData(DataListener listener);

    /**
     * data load finished by HandlerThread
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
