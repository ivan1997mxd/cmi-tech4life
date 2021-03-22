package com.estethapp.media.util;

import com.estethapp.media.R;

/**
 * Created by Irfan Ali on 1/3/2017.
 */
public enum ModelObject {

    RED(R.layout.front_health_pager_row),
    BLUE( R.layout.back_health_pager_row);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int layoutResId) {
        mLayoutResId = layoutResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
