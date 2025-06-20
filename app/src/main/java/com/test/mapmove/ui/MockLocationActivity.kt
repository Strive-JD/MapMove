package com.test.mapmove.ui

import android.content.Intent
import android.graphics.Rect
import android.os.*
import android.view.View
import android.widget.Toast
import com.baidu.mapapi.map.*
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ConvertUtils
import com.castiel.common.base.BaseActivity
import com.castiel.common.base.BaseViewModel
import com.test.mapmove.R
import com.test.mapmove.databinding.ActivityNaviBinding
import com.test.mapmove.manager.FollowMode
import com.test.mapmove.manager.MapLocationManager
import com.test.mapmove.manager.utils.MapDrawUtils
import com.test.mapmove.manager.SearchManager
import com.test.mapmove.model.MockMessageModel
import com.test.mapmove.model.NaviType
import com.test.mapmove.server.GpsService
import com.test.mapmove.utils.Utils
import kotlinx.android.synthetic.main.activity_navi.*


/**
 * @author yujiangdong
 */
class MockLocationActivity : BaseActivity<ActivityNaviBinding, BaseViewModel>(),
    View.OnClickListener {
    private lateinit var mBaiduMap: BaiduMap
    private var mNaviType: Int = NaviType.LOCATION
    private val mPadding: Int = ConvertUtils.dp2px(50f)
    private var mapLocationManager: MapLocationManager? = null

    override fun initViewModel(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.activity_navi
    }

    override fun initView() {
        ClickUtils.applySingleDebouncing(iv_back, this)

        mBaiduMap = mapview.map
        mapview.showScaleControl(false)
        mapview.showZoomControls(false)
        mBaiduMap.uiSettings?.isCompassEnabled = false

        mBaiduMap.setOnMapLoadedCallback {
            startMock()
        }
    }

    override fun initData() {}

    private fun startMock() {
        val model = intent.getParcelableExtra<MockMessageModel>("model")
        if (model == null) {
            pickPoiError()
            return
        }
        with(model) {
            this@MockLocationActivity.mNaviType = naviType
            //开启定位小蓝点展示
            mapLocationManager = MapLocationManager(
                this@MockLocationActivity,
                mBaiduMap,
                if (mNaviType == NaviType.LOCATION) FollowMode.MODE_PERSISTENT else FollowMode.MODE_NONE
            )
            when (naviType) {
                NaviType.LOCATION -> {
                    locationModel?.run {
                        startMockServer(model)
                    } ?: {
                        pickPoiError()
                    }
                }

                NaviType.NAVI, NaviType.NAVI_FILE -> {
                    SearchManager.INSTANCE.polylineList.let {
                        if (it.isEmpty()) {
                            pickPoiError()
                            return
                        }
                        mBaiduMap.clear()
                        startNavi?.latLng?.let { start ->
                            MapDrawUtils.drawMarkerToMap(mBaiduMap, start, "marker_start.png")
                        } ?: run {
                            MapDrawUtils.drawMarkerToMap(mBaiduMap, it[0], "marker_start.png")
                        }
                        wayNaviList?.map {
                            it.latLng?.let { latLng ->
                                MapDrawUtils.drawMarkerToMap(mBaiduMap, latLng, "marker_way.png")
                            }
                        }
                        endNavi?.latLng?.let { end ->
                            MapDrawUtils.drawMarkerToMap(mBaiduMap, end, "marker_end.png")
                        } ?: run {
                            MapDrawUtils.drawMarkerToMap(
                                mBaiduMap,
                                it[it.size - 1],
                                "marker_end.png"
                            )
                        }
                        MapDrawUtils.drawLineToMap(
                            mBaiduMap,
                            it,
                            Rect(mPadding, mPadding, mPadding, mPadding)
                        )
                        startMockServer(model)
                    }
                }

                else -> {
                }
            }
        }
    }

    override fun initObserver() {

    }

    private fun pickPoiError() {
        Toast.makeText(this, "选址数据异常，请重新选择地址再重试", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun startMockServer(parcelable: Parcelable?) {
        //判断  为null先启动服务  悬浮窗需要
        parcelable?.run {
            if (!Utils.isAllowMockLocation(this@MockLocationActivity)) {
                Toast.makeText(
                    this@MockLocationActivity,
                    "将本应用设置为\"模拟位置信息应用\"，否则无法正常使用",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }
        //启动服务  定位以及悬浮窗
        startService(Intent(this, GpsService::class.java).apply {
            parcelable?.let {
                putExtras(
                    Bundle().apply {
                        putParcelable("info", it)
                    })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapview.onPause()
        if (isFinishing) {
            destroy()
        }
    }

    private fun destroy() {
        mapLocationManager?.onDestroy()
        mapview.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v) {
            iv_back -> {
                finish()
            }

            else -> {
            }
        }
    }
}