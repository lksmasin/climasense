package org.climasense.settings.adapters

import android.app.Activity
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import james.adaptiveicon.AdaptiveIcon
import james.adaptiveicon.AdaptiveIconView
import org.climasense.R
import org.climasense.common.extensions.isDarkMode
import org.climasense.common.utils.helpers.IntentHelper
import org.climasense.common.utils.helpers.load
import org.climasense.theme.resource.providers.ResourceProvider

class IconProviderAdapter // adapter.
    (
    private val mActivity: Activity,
    private val mProviderList: List<ResourceProvider>,
    private val mListener: OnItemClickedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // holder.
    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mContainer: RelativeLayout = itemView.findViewById(R.id.item_icon_provider_container)
        private val mIcon: AdaptiveIconView = itemView.findViewById(R.id.item_icon_provider_clearIcon)
        private val mTitle: TextView = itemView.findViewById(R.id.item_icon_provider_title)
        private val mPreviewButton: AppCompatImageButton = itemView.findViewById(R.id.item_icon_provider_previewButton)

        fun onBindView() {
            val provider = mProviderList[bindingAdapterPosition]
            mContainer.setOnClickListener { mListener.onItemClicked(provider, bindingAdapterPosition) }
            val drawable = provider.providerIcon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && drawable is AdaptiveIconDrawable
            ) {
                mIcon.icon = AdaptiveIcon(
                    drawable.foreground,
                    drawable.background,
                    0.5
                )
                mIcon.setPath(AdaptiveIconView.PATH_CIRCLE)
            } else {
                mIcon.icon = AdaptiveIcon(drawable, null, 1.0)
            }
            mTitle.text = provider.providerName
            mPreviewButton.setOnClickListener {
                IntentHelper.startPreviewIconActivity(
                    mActivity,
                    provider.packageName
                )
            }
        }
    }

    private inner class GetMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appStore: AppCompatImageView = itemView.findViewById(R.id.item_icon_provider_get_more_appStore)
        private val gitHub: AppCompatImageView = itemView.findViewById(R.id.item_icon_provider_get_more_gitHub)
        private val chronus: AppCompatImageView = itemView.findViewById(R.id.item_icon_provider_get_more_chronus)

        fun onBindView() {
            load(appStore, R.drawable.ic_play_store)
            appStore.setOnClickListener { mListener.onAppStoreItemClicked("Geometric Weather Icon") }
            load(
                gitHub,
                if (itemView.context.isDarkMode) R.drawable.ic_github_light else R.drawable.ic_github_dark
            )
            gitHub.setOnClickListener { mListener.onGitHubItemClicked("https://github.com/climasense/climasense-icon-packs/blob/main/README.md") }
            load(chronus, R.drawable.ic_chronus)
            chronus.setOnClickListener { mListener.onAppStoreItemClicked("Chronus Icon") }
        }
    }

    // interface.
    interface OnItemClickedListener {
        fun onItemClicked(provider: ResourceProvider, adapterPosition: Int)
        fun onAppStoreItemClicked(query: String)
        fun onGitHubItemClicked(query: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_icon_provider, parent, false)
            )
        } else {
            GetMoreViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_icon_provider_get_more, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GetMoreViewHolder) {
            holder.onBindView()
            return
        }
        (holder as ViewHolder).onBindView()
    }

    override fun getItemCount() = mProviderList.size + 1

    override fun getItemViewType(position: Int) = if (position < mProviderList.size) 1 else -1
}
