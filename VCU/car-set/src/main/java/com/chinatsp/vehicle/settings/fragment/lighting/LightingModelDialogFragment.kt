package com.chinatsp.vehicle.settings.fragment.lighting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.chinatsp.vehicle.settings.R
import com.common.xui.utils.DensityUtils

class LightingModelDialogFragment : DialogFragment() {
    private var modeView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        modeView = inflater.inflate(R.layout.lighting_model_dialog_fragment, null, false);
        return modeView
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(DensityUtils.dp2px(1168f), DensityUtils.dp2px(600f));
    }
}