package com.v2plus.app.tlg

import android.content.Context
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.v2plus.com.v2plus.app.R

class AppListAdapter(
    private val context: Context,
    private val appList: List<TelegramApp>, // نوع داده به TelegramApp تغییر کرد
    private val onAppSelected: (TelegramApp) -> Unit
) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appName: TextView = itemView.findViewById(R.id.appName)
        val appIcon: ImageView = itemView.findViewById(R.id.appIcon)

        fun bind(app: TelegramApp) {
            appName.text = app.appName
            appIcon.setImageDrawable(app.appIcon)

            itemView.setOnClickListener {
                onAppSelected(app)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app_choice, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appList[position]
        holder.bind(app)
    }

    override fun getItemCount(): Int {
        return appList.size
    }
}

