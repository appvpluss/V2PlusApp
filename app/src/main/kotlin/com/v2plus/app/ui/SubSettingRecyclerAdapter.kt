package com.v2plus.app.ui

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2plus.com.v2plus.app.R

import com.v2plus.app.extension.toast
import com.v2plus.app.util.MmkvManager
import com.v2plus.app.util.QRCodeDecoder
import com.v2plus.app.util.Utils
import com.v2plus.com.v2plus.app.databinding.ItemQrcodeBinding
import com.v2plus.com.v2plus.app.databinding.ItemRecyclerSubSettingBinding

class SubSettingRecyclerAdapter(val activity: SubSettingActivity) :
    RecyclerView.Adapter<SubSettingRecyclerAdapter.MainViewHolder>() {

    private var mActivity: SubSettingActivity = activity
    private val subStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SUB, MMKV.MULTI_PROCESS_MODE) }

    private val share_method: Array<out String> by lazy {
        mActivity.resources.getStringArray(R.array.share_sub_method)
    }

    override fun getItemCount() = mActivity.subscriptions.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val subId = mActivity.subscriptions[position].first
        val subItem = mActivity.subscriptions[position].second
        holder.itemSubSettingBinding.tvName.text = subItem.remarks
        holder.itemSubSettingBinding.tvUrl.text = subItem.url
        if (subItem.enabled) {
            holder.itemSubSettingBinding.chkEnable.setBackgroundResource(R.color.colorAccent)
        } else {
            holder.itemSubSettingBinding.chkEnable.setBackgroundResource(0)
        }
        holder.itemView.setBackgroundColor(Color.TRANSPARENT)

        holder.itemSubSettingBinding.layoutEdit.setOnClickListener {
            mActivity.startActivity(
                Intent(mActivity, SubEditActivity::class.java)
                    .putExtra("subId", subId)
            )
        }
        holder.itemSubSettingBinding.infoContainer.setOnClickListener {
            subItem.enabled = !subItem.enabled
            subStorage?.encode(subId, Gson().toJson(subItem))
            notifyItemChanged(position)
        }

        if (TextUtils.isEmpty(subItem.url)) {
            holder.itemSubSettingBinding.layoutShare.visibility = View.INVISIBLE
        } else {
            holder.itemSubSettingBinding.layoutShare.setOnClickListener {
                AlertDialog.Builder(mActivity)
                    .setItems(share_method.asList().toTypedArray()) { _, i ->
                        try {
                            when (i) {
                                0 -> {
                                    val ivBinding =
                                        ItemQrcodeBinding.inflate(LayoutInflater.from(mActivity))
                                    ivBinding.ivQcode.setImageBitmap(
                                        QRCodeDecoder.createQRCode(
                                            subItem.url

                                        )
                                    )
                                    AlertDialog.Builder(mActivity).setView(ivBinding.root).show()
                                }

                                1 -> {
                                    Utils.setClipboard(mActivity, subItem.url)
                                }

                                else -> mActivity.toast("else")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemRecyclerSubSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class MainViewHolder(val itemSubSettingBinding: ItemRecyclerSubSettingBinding) :
        RecyclerView.ViewHolder(itemSubSettingBinding.root)
}
