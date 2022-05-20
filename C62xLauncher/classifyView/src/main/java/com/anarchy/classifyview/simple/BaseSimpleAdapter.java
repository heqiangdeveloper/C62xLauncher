package com.anarchy.classifyview.simple;

import com.anarchy.classifyview.adapter.BaseMainAdapter;
import com.anarchy.classifyview.adapter.BaseSubAdapter;

public interface BaseSimpleAdapter {
    BaseMainAdapter getMainAdapter();
    BaseSubAdapter getSubAdapter();
}
