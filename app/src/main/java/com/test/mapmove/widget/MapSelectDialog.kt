package com.test.mapmove.widget

import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.Overlay
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.route.DrivingRouteLine
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.castiel.common.dialog.BaseDialog
import com.test.mapmove.R
import com.test.mapmove.manager.utils.MapConvertUtils
import com.test.mapmove.manager.utils.MapDrawUtils
import com.test.mapmove.model.PoiInfoModel
import kotlinx.android.synthetic.main.dialog_select_navi_map.*

/**
 * @author yujiangdong
 */
class MapSelectDialog(
    context: Context,
    private var routeLines: List<DrivingRouteLine>,
    private var start: LatLng?,
    private var end: LatLng?,
    wayList: MutableList<PoiInfoModel>?
) : BaseDialog(context) {

    private val mHorizontalPadding = ConvertUtils.dp2px(20f)
    private val mMapPadding = ConvertUtils.dp2px(30f)
    private val screenWidth = ScreenUtils.getScreenWidth()
    private val mOverlayList = ArrayList<Overlay>()
    private var mainIndex = 0
    var listener: MapSelectDialogListener? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        layoutInflater.inflate(R.layout.dialog_select_navi_map, null, false).apply {
            setContentView(this)
        }

        texture_mapview.onCreate(context, null)
        texture_mapview.showScaleControl(false)
        texture_mapview.showZoomControls(false)
        texture_mapview.getChildAt(1).visibility = View.GONE
        texture_mapview.map?.let {
            it.uiSettings?.isCompassEnabled = false
            it.uiSettings?.setAllGesturesEnabled(false)
            //渲染路线
            it.setOnMapLoadedCallback {
                start?.let { start ->
                    MapDrawUtils.drawMarkerToMap(it, start, "marker_start.png")
                }
                wayList?.map { model ->
                    model.latLng?.let { latLng ->
                        MapDrawUtils.drawMarkerToMap(it, latLng, "marker_way.png")
                    }
                }
                end?.let { end ->
                    MapDrawUtils.drawMarkerToMap(it, end, "marker_end.png")
                }
                drawLine(it)
            }
        }
        ClickUtils.applySingleDebouncing(btn_change) {
            mainIndex = ++mainIndex % routeLines.size
            texture_mapview.map?.let {
                drawLine(it)
            }
        }
        ClickUtils.applySingleDebouncing(btn_select) {
            listener?.onSelectLine(routeLines[mainIndex])
            dismiss()
        }

        window?.let {
            val lp: WindowManager.LayoutParams = it.attributes
            lp.width = screenWidth - mHorizontalPadding
            lp.height = screenWidth + ConvertUtils.dp2px(60f)
            lp.gravity = Gravity.CENTER
            lp.dimAmount = 0.5f
            it.attributes = lp
        }

    }

    private fun drawLine(baiduMap: BaiduMap) {
        mOverlayList.map {
            it.remove()
        }.also {
            mOverlayList.clear()
        }

        routeLines.mapIndexed { index, line ->
            MapDrawUtils.drawLineToMap(
                baiduMap,
                MapConvertUtils.convertLatLngList(line),
                Rect(mMapPadding, mMapPadding, mMapPadding, mMapPadding),
                index == mainIndex
            )?.let { overlay ->
                mOverlayList.add(overlay)
            }
        }
    }

    public fun onResume() {
        texture_mapview.onResume()
    }

    public fun onPause() {
        texture_mapview.onPause()
    }

    override fun dismiss() {
        texture_mapview.onDestroy()
        super.dismiss()
    }

    interface MapSelectDialogListener {
        fun onSelectLine(routeLine: DrivingRouteLine)
    }
}