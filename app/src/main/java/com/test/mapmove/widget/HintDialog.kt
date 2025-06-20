package com.test.mapmove.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.test.mapmove.R
import com.test.mapmove.databinding.DialogHintBinding

/**
 * @author yujiangdong
 */
class HintDialog(context: Context, title: String = "", msg: String = "") : Dialog(context) {

    init {
        DataBindingUtil.bind<DialogHintBinding>(
            LayoutInflater.from(context)
                .inflate(R.layout.dialog_hint, null, false)
        )?.let {
            setContentView(it.root)
            window?.run {
                setBackgroundDrawableResource(R.color.transparent);
                val lp = attributes;
                lp.width = ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(20f)
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                attributes = lp
            }
            it.tvTitle.text = title
            it.tvMsg.text = msg
            ClickUtils.applySingleDebouncing(it.btnConfirm) {
                dismiss()
            }
        }
    }
}