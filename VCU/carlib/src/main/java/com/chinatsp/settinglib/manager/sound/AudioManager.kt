package com.chinatsp.settinglib.manager.sound

import android.car.hardware.CarPropertyValue
import com.chinatsp.settinglib.ITabStore
import com.chinatsp.settinglib.manager.BaseManager
import com.chinatsp.settinglib.sign.Origin
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/20 14:08
 * @desc   :
 * @version: 1.0
 */


class AudioManager private constructor() : BaseManager(), ITabStore {

    override val tabSerial: AtomicInteger by lazy {
        AtomicInteger(-1)
    }

    companion object {
        val TAG: String = AudioManager::class.java.simpleName

        val instance: AudioManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioManager()
        }

        val managers: List<BaseManager> by lazy {
            ArrayList<BaseManager>().apply {
                add(VoiceManager.instance)
                add(EffectManager.instance)
            }
        }
    }

    private var followers: List<BaseManager>? = null

    val managers: List<BaseManager> by lazy {
        listOf(VoiceManager.instance, EffectManager.instance)
    }

    override fun onDispatchSignal(property: CarPropertyValue<*>, origin: Origin): Boolean {
        managers.forEach {
            it.onDispatchSignal(property, origin)
        }
        return true
    }

    override fun isCareSignal(signal: Int, origin: Origin): Boolean {
        val list = managers.filter { it.isCareSignal(signal, origin) }.toList()
        followers = list
        return list.isNotEmpty()
    }

    override fun getOriginSignal(origin: Origin): Set<Int> {
        val hashSet = HashSet<Int>()
        managers.forEach { manager ->
            manager.getOriginSignal(origin).let {
                if (it.isNotEmpty()) {
                    hashSet.addAll(it)
                }
            }
        }
        return hashSet
    }

    override val careSerials: Map<Origin, Set<Int>> by lazy {
        HashMap<Origin, Set<Int>>().apply {
            val keySet = managers.flatMap { it.careSerials.keys }.toSet()
            keySet.forEach { key ->
                val hashSet = HashSet<Int>()
                managers.forEach { manager ->
                    hashSet.addAll(manager.getOriginSignal(key))
                }
                put(key, hashSet)
            }
        }
    }


}