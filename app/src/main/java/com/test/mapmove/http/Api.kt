package com.test.mapmove.http

import com.castiel.common.base.BaseResponse
import com.test.mapmove.model.AppUpdateModel
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author yujiangdong
 */
interface Api {
    /**
     * 检测app版本
     */
    @POST("/apiv2/app/check")
    @FormUrlEncoded
    suspend fun checkAppUpdate(@FieldMap args: Map<String, String>): BaseResponse<AppUpdateModel>
}