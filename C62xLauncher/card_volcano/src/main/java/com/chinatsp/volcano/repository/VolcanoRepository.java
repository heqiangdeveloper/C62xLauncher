package com.chinatsp.volcano.repository;

import android.content.Context;

public class VolcanoRepository {
    private Context mContext;

    public static VolcanoRepository getInstance() {
        return Holder.repository;
    }

    private VolcanoRepository() {

    }

    private static class Holder{
        private static VolcanoRepository repository = new VolcanoRepository();
    }

    private void init(Context context) {
        mContext = context;
    }
}
