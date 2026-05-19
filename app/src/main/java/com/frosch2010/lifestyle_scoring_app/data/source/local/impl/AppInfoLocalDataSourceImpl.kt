package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import com.frosch2010.lifestyle_scoring_app.data.model.AppBuildInfoData
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.AppInfoLocalDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Reads `PackageManager` metadata for this application and normalizes version codes across API levels.
 *
 * @param context Application [Context] whose package name identifies the install being queried.
 */
class AppInfoLocalDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AppInfoLocalDataSource {
    /**
     * @return [AppBuildInfoData] built from the resolved [android.content.pm.PackageInfo] for this app.
     */
    override fun getBuildInfo(): AppBuildInfoData {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return AppBuildInfoData(
            versionName = packageInfo.versionName.orEmpty(),
            versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
        )
    }
}
