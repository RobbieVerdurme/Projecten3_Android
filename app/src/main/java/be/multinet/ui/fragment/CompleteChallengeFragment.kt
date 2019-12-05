package be.multinet.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import be.multinet.R
import be.multinet.databinding.FragmentCompleteChallengeBinding
import be.multinet.model.Challenge
import be.multinet.viewmodel.CompleteChallengeViewModel
import be.multinet.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_complete_challenge.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CompleteChallengeFragment : Fragment() {
    /**
     * viewmodel of this fragment
     */
    val viewmodel: CompleteChallengeViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentCompleteChallengeBinding.inflate(inflater, container, false)
        binding.completeChallengeViewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
        onClickListener()
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.Complete_Challenge)
    }

    /**
     * setup the onclick listeners
     */
    private fun onClickListener(){
        challengeStar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
                ChallengeRatingScale.text = p1.toString()
                when(p0!!.rating.toInt()){
                    1 -> {
                        ChallengeRatingScale.text = "Heel slecht"
                    }
                    2 -> {
                        ChallengeRatingScale.text = "Heeft verbetering nodig"
                    }
                    3 -> {
                        ChallengeRatingScale.text = "Goed"
                    }
                    4 ->{
                        ChallengeRatingScale.text = "Heel goed"
                    }
                    5 -> {
                        ChallengeRatingScale.text = "Perfect"
                    }
                }
            }
        /**
         * redirect to homepage and say that the challenge has been completed
         */
        btnSubmit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val navController = findNavController()
                val uservm: UserViewModel by sharedViewModel()
                val user = uservm.getUser().value!!

                viewmodel.completeChalenge(user.getUserId().toInt(), user.getToken())
                user.setEXP(user.getEXP() + 1)
                navController.navigate(R.id.action_CompleteChallengeFragment_to_challengesCategoryFragment)
            }
        })
    }
}
