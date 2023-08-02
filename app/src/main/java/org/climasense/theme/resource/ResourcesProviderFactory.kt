package org.climasense.theme.resource

import android.content.Context
import org.climasense.climasense
import org.climasense.settings.SettingsManager
import org.climasense.theme.resource.providers.*

object ResourcesProviderFactory {
    val newInstance: ResourceProvider = getNewInstance(SettingsManager.getInstance(climasense.instance).iconProvider)

    fun getNewInstance(packageName: String?): ResourceProvider {
        val context: Context = climasense.instance
        val defaultProvider = DefaultResourceProvider()
        if (packageName == null || DefaultResourceProvider.isDefaultIconProvider(packageName)) {
            return defaultProvider
        }
        if (PixelResourcesProvider.isPixelIconProvider(packageName)) {
            return PixelResourcesProvider(defaultProvider)
        }
        if (IconPackResourcesProvider.isIconPackIconProvider(context, packageName)) {
            return IconPackResourcesProvider(context, packageName, defaultProvider)
        }
        return if (ChronusResourceProvider.isChronusIconProvider(context, packageName)) {
            ChronusResourceProvider(context, packageName, defaultProvider)
        } else IconPackResourcesProvider(
            context,
            packageName,
            defaultProvider
        )
    }

    fun getProviderList(context: Context): List<ResourceProvider> {
        val providerList: MutableList<ResourceProvider> = ArrayList()
        val defaultProvider = DefaultResourceProvider()
        providerList.add(defaultProvider)
        providerList.add(PixelResourcesProvider(defaultProvider))

        // geometric weather icon provider.
        providerList.addAll(IconPackResourcesProvider.getProviderList(context, defaultProvider))

        // chronus icon pack.
        val chronusIconPackList = ChronusResourceProvider.getProviderList(
            context, defaultProvider
        ).toMutableList()
        for (i in chronusIconPackList.indices.reversed()) {
            for (resourceProvider in providerList) {
                if (chronusIconPackList[i] == resourceProvider) {
                    chronusIconPackList.removeAt(i)
                    break
                }
            }
        }
        providerList.addAll(chronusIconPackList)
        return providerList
    }
}
