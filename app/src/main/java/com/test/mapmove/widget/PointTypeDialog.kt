package com.test.mapmove.widget

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.test.mapmove.R
import com.test.mapmove.databinding.DialogPointTypeBinding
import com.test.mapmove.utils.LocationUtils

/**
 * @author jiayu.liu
 */
class PointTypeDialog(context: Context) : Dialog(context) {
    private var binding: DialogPointTypeBinding? = null
    var listener: PointTypeDialogListener? = null


    init {
        DataBindingUtil.bind<DialogPointTypeBinding>(
            LayoutInflater.from(context)
                .inflate(R.layout.dialog_point_type, null, false)
        )?.let {
            binding = it
            setContentView(it.root)
            window?.run {
                setBackgroundDrawableResource(R.color.transparent);
                val lp = attributes;
                lp.width = ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(20f)
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                attributes = lp
            }
            ClickUtils.applySingleDebouncing(it.btnConfirm) {
                binding?.rgType?.checkedRadioButtonId?.let { id ->
                    var type = ""
                    when (id) {
                        R.id.rb_gcj02 -> {
                            type = LocationUtils.gcj02
                        }
                        R.id.rb_bd09 -> {
                            type = LocationUtils.bd09
                        }
                        R.id.rb_gps84 -> {
                            type = LocationUtils.gps84
                        }
                        else -> {}
                    }
                    if (!TextUtils.isEmpty(type)) {
                        listener?.onDismiss(type)
                    }
                }
                dismiss()
            }
        }
    }

    fun show(type: String) {
        if (TextUtils.isEmpty(type)) {
            return
        }
        when (type) {
            LocationUtils.gcj02 -> {
                binding?.checkId = R.id.rb_gcj02
            }
            LocationUtils.bd09 -> {
                binding?.checkId = R.id.rb_bd09
            }
            LocationUtils.gps84 -> {
                binding?.checkId = R.id.rb_gps84
            }
            else -> {}
        }

        super.show()
    }

    interface PointTypeDialogListener {
        fun onDismiss(type: String)
    }
}