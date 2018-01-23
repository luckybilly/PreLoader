package com.billy.android.preloader.interfaces;

/**
 * @author billy.qi
 * @since 18/1/23 14:25
 */
public interface GroupedDataListener<DATA> extends DataListener<DATA> {
    /**
     *
     * @return
     */
    String keyInGroup();
}
