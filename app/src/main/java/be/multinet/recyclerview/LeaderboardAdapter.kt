package be.multinet.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.multinet.R
import be.multinet.model.LeaderboardUser
import kotlinx.android.synthetic.main.leaderboard_item.view.*

class LeaderboardAdapter(private val myDataset: List<LeaderboardUser>): RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_item, parent, false)
        )
    }

    override fun getItemCount() = myDataset.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = myDataset[position]
        holder.view.leaderboard_place.text = (position+1).toString()
        holder.view.leaderboard_name.text = user.getName()
        holder.view.leaderboard_score.text = user.getScore().toString()
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


}