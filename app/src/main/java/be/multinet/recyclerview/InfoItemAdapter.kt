package be.multinet.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.multinet.R
import be.multinet.model.Info
import be.multinet.model.InfoCategory
import kotlinx.android.synthetic.main.subcategory_info_item.view.*

class InfoItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Info> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InfoListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.subcategory_info_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is InfoListItemViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun submitList(infoList: List<Info>){
        items = infoList
    }

    class InfoListItemViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val infoTitle = itemView.info_Title
        val infoDescritpion = itemView.info_Description

        fun bind(info: Info){
            infoTitle.setText(info.getTitle())
            infoDescritpion.setText(info.getDescription())
        }
    }
}