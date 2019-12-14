package be.multinet.ui.fragment

import AppDialogBuilder
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.multinet.R
import be.multinet.databinding.FragmentCompleteChallengeBinding
import be.multinet.model.Challenge
import be.multinet.viewmodel.CompleteChallengeViewModel
import be.multinet.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_complete_challenge.*
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CompleteChallengeFragment : Fragment() {
    /**
     * viewmodel of this fragment
     */
    val viewmodel: CompleteChallengeViewModel by sharedViewModel()
    val userVM: UserViewModel by sharedViewModel()

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
        viewmodel.getCompletedOn().observe(viewLifecycleOwner, Observer {
            if(it != null && viewmodel.getRequestError().value == null){
                findNavController().navigateUp()
            }
        })
        viewmodel.getRequestError().observe(viewLifecycleOwner, Observer {
            if(it != null){
                when (it) {
                    viewmodel.offline -> {
                        AppDialogBuilder.buildIsOfflineDialog(context!!,getString(R.string.offline),
                            R.string.complete_challenge_offline_description,
                            DialogInterface.OnClickListener { _, _ ->  },R.string.dialog_ok).show()
                    }
                    viewmodel.dailyChallenge -> {
                        //Navigate up, but keep request error as daily challenge
                        //this triggers the snackbar in challenges fragment, since completeViewModel is shared
                        findNavController().navigateUp()
                    }
                    else -> {
                        Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    /**
     * setup the onclick listeners
     */
    private fun onClickListener(){
        challengeStar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { p0, p1, _ ->
                ChallengeRatingScale.text = p1.toString()
                val rating = p0!!.rating.toInt()
                when(rating){
                    1 -> {
                        ChallengeRatingScale.text = getString(R.string.ratingVeryBad)
                        viewmodel.setRating(rating)
                    }
                    2 -> {
                        ChallengeRatingScale.text = getString(R.string.ratingBad)
                        viewmodel.setRating(rating)
                    }
                    3 -> {
                        ChallengeRatingScale.text = getString(R.string.ratingGood)
                        viewmodel.setRating(rating)
                    }
                    4 ->{
                        ChallengeRatingScale.text = getString(R.string.ratingVeryGood)
                        viewmodel.setRating(rating)
                    }
                    5 -> {
                        ChallengeRatingScale.text = getString(R.string.ratingPerfect)
                        viewmodel.setRating(rating)
                    }
                }
            }

        btnSubmit.setOnClickListener {
            val user = userVM.getUser().value!!
            viewmodel.setFeedback(ChallengeFeedback.text.toString())
            viewmodel.completeChallenge(user, user.getToken())
        }
    }
}
