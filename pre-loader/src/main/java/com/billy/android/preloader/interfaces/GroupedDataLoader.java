package com.billy.android.preloader.interfaces;

/**
 * grouped version of {@link DataLoader}
 *
 * @author billy.qi
 * @since 18/1/23 14:25
 */
public interface GroupedDataLoader<DATA> extends DataLoader<DATA> {
    /**
     * key of this data-loader in the group
     * should be unique
     * @return unique key in the group
     */
    String keyInGroup();
}
