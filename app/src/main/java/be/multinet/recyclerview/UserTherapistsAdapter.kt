package be.multinet.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.multinet.R
import be.multinet.model.Therapist
import kotlinx.android.synthetic.main.profile_therapists_list.view.*

class UserTherapistsAdapter(private val dataset: List<Therapist>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TherapistViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.profile_therapists_list, parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is TherapistViewHolder ->{
                holder.bind(dataset[position])
            }
        }
    }


    class TherapistViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){

        val therapistName = itemView.therapist_name
        val therapistMail = itemView.therapis_mail

        fun bind(therapist: Therapist){
            therapistName.setText(therapist.getName())
            therapistMail.setText(therapist.getMail())
        }
    }

}