package be.multinet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import be.multinet.databinding.ChallengeItemBinding
import be.multinet.model.Challenge

class ChallengeAdapter(private val clickListener: CompleteChallengeClickListener, private val dataset: List<Challenge>) : RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {

    class ChallengeViewHolder(val binding: ChallengeItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = dataset.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ChallengeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChallengeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val challenge = dataset[position]
        holder.binding.challenge = challenge
        holder.binding.completeChallengeBtn.setOnClickListener {
            clickListener.onItemClicked(challenge)
        }
        holder.binding.challengeImage.visibility = if(challenge.getDateCompleted() == null) View.GONE else View.VISIBLE
        holder.binding.completeChallengeBtn.visibility = if(challenge.getDateCompleted() == null) View.VISIBLE else View.GONE
        holder.binding.executePendingBindings()
    }


}