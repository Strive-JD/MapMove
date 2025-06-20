package com.test.mapmove.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.baidu.mapapi.model.LatLng
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.castiel.common.base.BaseActivity
import com.castiel.common.base.BaseViewModel
import com.test.mapmove.R
import com.test.mapmove.databinding.ActivityFileBinding
import com.test.mapmove.manager.SearchManager
import com.test.mapmove.model.MockMessageModel
import com.test.mapmove.model.NaviType
import com.test.mapmove.utils.WarnDialogUtils
import com.test.mapmove.utils.LocationUtils
import com.test.mapmove.utils.MMKVUtils
import com.test.mapmove.utils.Utils
import com.test.mapmove.widget.HintDialog
import com.test.mapmove.widget.NaviPathDialog
import com.test.mapmove.widget.NaviPopupWindow
import com.test.mapmove.widget.PointTypeDialog

/**
 * @author yujiangdong
 */
class FileMockActivity : BaseActivity<ActivityFileBinding, BaseViewModel>(), View.OnClickListener {
    private var mPointType = LocationUtils.gcj02
    override fun initViewModel(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.activity_file
    }

    override fun initView() {
        KeyboardUtils.clickBlankArea2HideSoftInput()


        ClickUtils.applySingleDebouncing(dataBinding.ivNaviSetting, this)
        ClickUtils.applySingleDebouncing(dataBinding.ivWarning, this)
        ClickUtils.applySingleDebouncing(dataBinding.btnFile, this)
        ClickUtils.applySingleDebouncing(dataBinding.btnStartNavi, this)
        ClickUtils.applySingleDebouncing(dataBinding.btnCreatePath, this)
        ClickUtils.applySingleDebouncing(dataBinding.btnPointType, this)

        dataBinding.pointType = "经纬度类型：$mPointType"
    }

    override fun initData() {

    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v) {
            dataBinding.ivWarning -> {
                HintDialog(
                    this@FileMockActivity,
                    "数据格式要求",
                    getString(R.string.file_navi_hint)
                ).show()
            }

            dataBinding.btnCreatePath -> {
                //生成路径文件
                startActivity(Intent(this, CalculateRouteActivity::class.java))
            }

            dataBinding.ivNaviSetting -> {
                NaviPopupWindow(this).apply {
                    show(dataBinding.ivNaviSetting)
                }
            }

            dataBinding.btnFile -> {
                NaviPathDialog(this).run {
                    listener = object : NaviPathDialog.NaviPathListener {
                        override fun onItemClick(path: String) {
                            if (TextUtils.isEmpty(path)) {
                                return
                            }
                            dataBinding.edFile.setText(path)
                        }
                    }
                    show()
                }
            }

            dataBinding.btnStartNavi -> {
                val text = dataBinding.edFile.text
                if (TextUtils.isEmpty(text)) {
                    ToastUtils.showShort("路径不能为null")
                    return
                }
                Utils.checkFloatWindow(this).let { it ->
                    if (!it) {
                        WarnDialogUtils.setFloatWindowDialog(this@FileMockActivity)
                        return
                    }
                    val polylineList = arrayListOf<LatLng>()
                    FileIOUtils.readFile2String(text.toString())?.run {
                        split(";").run {
                            if (isNotEmpty()) {
                                map {
                                    it.split(",").run {
                                        if (size == 2) {
                                            val lat = get(1).toDouble()
                                            val lng = get(0).toDouble()

                                            var gcj02 = doubleArrayOf(lng, lat)
                                            //将路线转换成gcj02
                                            when (mPointType) {
                                                LocationUtils.gps84 -> {
                                                    gcj02 = LocationUtils.wgs84ToGcj02(
                                                        lng,
                                                        lat
                                                    )
                                                }

                                                LocationUtils.bd09 -> {
                                                    gcj02 = LocationUtils.bd09ToGcj02(
                                                        lng,
                                                        lat
                                                    )
                                                }

                                                else -> {}
                                            }
                                            polylineList.add(
                                                LatLng(
                                                    gcj02[1],
                                                    gcj02[0]
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        //文件数据导航替换
                        SearchManager.INSTANCE.polylineList = polylineList
                    }
                    if (polylineList.isEmpty()) {
                        ToastUtils.showShort("文件数据解析失败,无法启动导航")
                        return
                    }
                    val model = MockMessageModel(
                        naviType = NaviType.NAVI_FILE,
                        speed = MMKVUtils.getSpeed(),
                    )
                    val intent = Intent(this, MockLocationActivity::class.java)
                    intent.putExtra("model", model)
                    startActivity(intent)
                }
            }

            dataBinding.btnPointType -> {
                PointTypeDialog(this).apply {
                    listener = object : PointTypeDialog.PointTypeDialogListener {
                        override fun onDismiss(type: String) {
                            mPointType = type
                            dataBinding.pointType = "经纬度类型：$mPointType"
                        }
                    }
                }.show(mPointType)
            }

            else -> {
            }
        }
    }

}