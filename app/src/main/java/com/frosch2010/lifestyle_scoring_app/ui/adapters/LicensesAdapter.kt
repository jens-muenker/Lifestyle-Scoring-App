package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemLicenseBinding
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemLicenseFirstBinding
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemLicenseLastBinding
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemLicenseMiddleBinding
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry

/**
 * Renders OSS attribution rows with shape-specific layouts so grouped lists keep correct corner radii.
 *
 * View types distinguish first/middle/last/single items to match stacked card visuals in the licenses screen.
 */
class LicensesAdapter :
    ListAdapter<LicenseEntry, RecyclerView.ViewHolder>(DiffCallback()) {

    /**
     * @param position Adapter index used to derive stacked-card segment type.
     * @return One of [VIEW_TYPE_SINGLE], [VIEW_TYPE_FIRST], [VIEW_TYPE_MIDDLE], or [VIEW_TYPE_LAST].
     */
    override fun getItemViewType(position: Int): Int {
        return when {
            itemCount == 1 -> VIEW_TYPE_SINGLE
            position == 0 -> VIEW_TYPE_FIRST
            position == itemCount - 1 -> VIEW_TYPE_LAST
            else -> VIEW_TYPE_MIDDLE
        }
    }

    /**
     * @param parent RecyclerView inflating this adapter.
     * @param viewType Segment discriminator from [getItemViewType].
     * @return Concrete [BaseHolder] subclass with the matching layout inflation.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SINGLE -> SingleHolder(LstItemLicenseBinding.inflate(inflater, parent, false))
            VIEW_TYPE_FIRST -> FirstHolder(LstItemLicenseFirstBinding.inflate(inflater, parent, false))
            VIEW_TYPE_LAST -> LastHolder(LstItemLicenseLastBinding.inflate(inflater, parent, false))
            else -> MiddleHolder(LstItemLicenseMiddleBinding.inflate(inflater, parent, false))
        }
    }

    /**
     * @param holder Segment-specific holder produced by [onCreateViewHolder].
     * @param position Index of the [LicenseEntry] to bind.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entry = getItem(position)
        when (holder) {
            is SingleHolder -> holder.bind(entry)
            is FirstHolder -> holder.bind(entry)
            is MiddleHolder -> holder.bind(entry)
            is LastHolder -> holder.bind(entry)
        }
    }

    /**
     * Shared binding logic for project title, license name, and optional linkified URLs.
     */
    private abstract class BaseHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        /**
         * Populates row text and visibility based on [entry].
         *
         * @param entry Parsed dependency metadata row.
         */
        abstract fun bind(entry: LicenseEntry)

        /**
         * Writes common fields into the four text views shared across stacked row layouts.
         *
         * @param txtProject Primary title line (project name plus optional version suffix).
         * @param txtLicense Secondary line naming the detected license identifier.
         * @param txtUrl Optional project URL line; hidden when blank.
         * @param txtLicenseUrl Optional license text URL line; hidden when blank.
         * @param entry Domain row supplying raw strings before localization templates apply.
         */
        protected fun bindCommon(
            txtProject: android.widget.TextView,
            txtLicense: android.widget.TextView,
            txtUrl: android.widget.TextView,
            txtLicenseUrl: android.widget.TextView,
            entry: LicenseEntry
        ) {
            txtProject.text = if (entry.version.isNotBlank()) {
                "${entry.projectName} (${entry.version})"
            } else {
                entry.projectName
            }

            val context = itemView.context
            txtLicense.text = context.getString(R.string.licenses_line_license, entry.licenseName)

            if (entry.projectUrl.isNotBlank()) {
                txtUrl.text = context.getString(R.string.licenses_line_url, entry.projectUrl)
                txtUrl.movementMethod = LinkMovementMethod.getInstance()
                txtUrl.visibility = View.VISIBLE
            } else {
                txtUrl.visibility = View.GONE
            }

            if (entry.licenseUrl.isNotBlank()) {
                txtLicenseUrl.text = context.getString(R.string.licenses_line_license_url, entry.licenseUrl)
                txtLicenseUrl.movementMethod = LinkMovementMethod.getInstance()
                txtLicenseUrl.visibility = View.VISIBLE
            } else {
                txtLicenseUrl.visibility = View.GONE
            }
        }
    }

    /**
     * Top segment of a multi-row stack with rounded upper corners only.
     *
     * @param binding First-row view binding.
     */
    private class FirstHolder(
        private val binding: LstItemLicenseFirstBinding
    ) : BaseHolder(binding.root) {
        override fun bind(entry: LicenseEntry) {
            bindCommon(
                txtProject = binding.txtLicenseProject,
                txtLicense = binding.txtLicenseName,
                txtUrl = binding.txtLicenseUrl,
                txtLicenseUrl = binding.txtLicenseLicenseUrl,
                entry = entry
            )
        }
    }

    /**
     * Standalone card when the dependency list contains exactly one entry.
     *
     * @param binding Single-row view binding.
     */
    private class SingleHolder(
        private val binding: LstItemLicenseBinding
    ) : BaseHolder(binding.root) {
        override fun bind(entry: LicenseEntry) {
            bindCommon(
                txtProject = binding.txtLicenseProject,
                txtLicense = binding.txtLicenseName,
                txtUrl = binding.txtLicenseUrl,
                txtLicenseUrl = binding.txtLicenseLicenseUrl,
                entry = entry
            )
        }
    }

    /**
     * Interior stack segment with square vertical joins.
     *
     * @param binding Middle-row view binding.
     */
    private class MiddleHolder(
        private val binding: LstItemLicenseMiddleBinding
    ) : BaseHolder(binding.root) {
        override fun bind(entry: LicenseEntry) {
            bindCommon(
                txtProject = binding.txtLicenseProject,
                txtLicense = binding.txtLicenseName,
                txtUrl = binding.txtLicenseUrl,
                txtLicenseUrl = binding.txtLicenseLicenseUrl,
                entry = entry
            )
        }
    }

    /**
     * Bottom segment with rounded lower corners only.
     *
     * @param binding Last-row view binding.
     */
    private class LastHolder(
        private val binding: LstItemLicenseLastBinding
    ) : BaseHolder(binding.root) {
        override fun bind(entry: LicenseEntry) {
            bindCommon(
                txtProject = binding.txtLicenseProject,
                txtLicense = binding.txtLicenseName,
                txtUrl = binding.txtLicenseUrl,
                txtLicenseUrl = binding.txtLicenseLicenseUrl,
                entry = entry
            )
        }
    }

    /**
     * Stable identity uses project name, version, and license name triple to survive resorting.
     */
    private class DiffCallback : DiffUtil.ItemCallback<LicenseEntry>() {
        override fun areItemsTheSame(oldItem: LicenseEntry, newItem: LicenseEntry): Boolean {
            return oldItem.projectName == newItem.projectName &&
                oldItem.version == newItem.version &&
                oldItem.licenseName == newItem.licenseName
        }

        override fun areContentsTheSame(oldItem: LicenseEntry, newItem: LicenseEntry): Boolean {
            return oldItem == newItem
        }
    }

    private companion object {
        /** Single-row layout when the adapter hosts one dependency only. */
        private const val VIEW_TYPE_SINGLE = 0

        /** First row in a multi-item stack. */
        private const val VIEW_TYPE_FIRST = 1

        /** Middle row in a multi-item stack. */
        private const val VIEW_TYPE_MIDDLE = 2

        /** Last row in a multi-item stack. */
        private const val VIEW_TYPE_LAST = 3
    }
}
