package be.multinet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import be.multinet.R
import be.multinet.model.Challenge
import be.multinet.recyclerview.CompleteChallengeClickListener

class ChallengeAdapter(private val clickListener: CompleteChallengeClickListener) : PagerAdapter(), IChallengeAdapter {
    val MAX_ELEVATION_FACTOR = 8
    private var items: ArrayList<Challenge> = ArrayList()
    private var mView: ArrayList<CardView> = ArrayList()
    private var mBaseElevation: Float = 0f


    fun addCardItems(item: List<Challenge>){
        items.addAll(item)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getBaseElevation(): Float {
        return mBaseElevation
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getCardViewAt(position: Int): CardView {
        return mView[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        container.removeView(`object` as View)
        mView.remove(`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View
        val item = items[position]

        if(item.isCompleted()){
            view = LayoutInflater.from(container.context).inflate(R.layout.challenge_item_completed, container, false)
        }else{
            view = LayoutInflater.from(container.context).inflate(R.layout.challenge_item_current, container, false)
        }
        container.addView(view)
        bind(item, view)
        val cardView: CardView = view.findViewById(R.id.cardView)
        if(mBaseElevation == 0f){
            mBaseElevation = cardView.cardElevation
        }
        cardView.maxCardElevation = mBaseElevation * MAX_ELEVATION_FACTOR
        mView.add(position,cardView)
        return view

    }

    private fun bind(challenge: Challenge, view: View){
        //ophalen van texviews
        val img: ImageView = view.findViewById(R.id.challengeImage)
        val title: TextView = view.findViewById(R.id.challengeTitle)
        val description: TextView = view.findViewById(R.id.challengeDescription)
        val complete: Button?= view.findViewById(R.id.challengeComplete)

        //set text on textview
        img.setImageResource(R.drawable.ic_multimed_background)
        title.setText(challenge.getTitle())
        description.setText(challenge.getDescription())
        //complete onclick?
        complete?.setOnClickListener {
            clickListener.onItemClicked(challenge)
        }
    }
}