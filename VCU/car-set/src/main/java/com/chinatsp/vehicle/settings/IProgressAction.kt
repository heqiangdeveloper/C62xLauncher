package com.chinatsp.vehicle.settings

import android.view.View
import com.chinatsp.settinglib.optios.Progress
import timber.log.Timber

interface IProgressAction : IAction {
    fun findProgressByNode(node: Progress): View?

    fun obtainDependByNode(node: Progress): Boolean {
        return true
    }

    fun obtainActiveByNode(node: Progress): Boolean {
        return true
    }
    fun updateProgressEnable(node: Progress) {
        findProgressByNode(node)?.let {
            val selfActive = obtainActiveByNode(node)
            val dependActive = obtainDependByNode(node)
            Timber.d("updateSwitchEnable $node, selfActive:$selfActive, dependActive:$dependActive")
            updateEnable(it, selfActive, dependActive)
        }
    }
}