package be.multinet.ui.fragment

import be.multinet.databinding.FragmentHomeBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

import be.multinet.R
import be.multinet.viewmodel.HomeViewModel

/**
 * This [Fragment] represents the home page.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val binding = FragmentHomeBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.home_title)
    }


}
