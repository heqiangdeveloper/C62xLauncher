package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.chinatsp.vehicle.settings.R
import com.common.xui.utils.DensityUtils

class SteeringHeatingDialogFragment : DialogFragment() {
    private var steeringHeatingView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        steeringHeatingView = inflater.inflate(R.layout.steering_heating_dialog_fragment, null, false);
        return steeringHeatingView
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(DensityUtils.dp2px(960f), DensityUtils.dp2px(600f));
    }
}