package be.multinet.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.multinet.model.InfoCategory
import kotlinx.android.synthetic.main.info_list.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import be.multinet.databinding.InfoListBinding
import be.multinet.viewmodel.InfoViewModel



class InfoAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<InfoCategory> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = InfoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InfoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InfoViewHolder -> {
                holder.binding.infoViewModel = InfoViewModel()
                holder.binding.subCategoryItemTitle.setOnClickListener {
                    holder.binding.infoViewModel?.onClickTitle()
                }
                holder.bind(items[position], context)
            }
        }
    }

    fun submitList(infoList: List<InfoCategory>){
        items = infoList
    }

    class InfoViewHolder (
        val binding: InfoListBinding
    ) : RecyclerView.ViewHolder(binding.root){
        val infoTitle = itemView.subCategoryItemTitle
        val infoItemList = itemView.subCategoryItemList

        fun bind(info: InfoCategory, context: Context){
            infoTitle.setText(info.getSubCategoryTitle())
            //set items in the recyclerview
            val adapter = InfoItemAdapter()
            val layoutManager  = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                infoItemList.getContext(),
                layoutManager.orientation
            )

            adapter.submitList(info.getCategoryItems())
            infoItemList.layoutManager = layoutManager
            infoItemList.adapter = adapter
            infoItemList.addItemDecoration(dividerItemDecoration)
        }
    }
}