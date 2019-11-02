package be.multinet.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import be.multinet.R
import be.multinet.model.Info
import be.multinet.model.InfoCategory
import be.multinet.recyclerview.InfoAdapter
import kotlinx.android.synthetic.main.fragment_info.*

/**
 * This [Fragment] represents the info page.
 */
class InfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //TODO use a DataBinding class (InfoFragmentBinding) to inflate and setup lifecycleowner + viewmodel etc
        return inflater.inflate(R.layout.fragment_info, container, false)
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
        toolbar.title = getString(R.string.info_title)
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        val adapter = InfoAdapter(activity!!.applicationContext)
        val layoutManager  = LinearLayoutManager(activity)

        //mockdata
        val list =listOf<InfoCategory>(
            InfoCategory("Activiteit", listOf<Info>(
                Info("Lopen","Loop een 5 tal km per week"),
                Info("Fitness","Ga 2-3 keer per week naar de fitness")
                )
            ),
            InfoCategory("Recept", listOf<Info>(
                Info("Spagetti", "Maak spagetti met lekker veel groenten in"),
                Info("Slaatje", "Maak 1 keer per week een gezond slaatje")
            )),
            InfoCategory("ExtraInfo", listOf<Info>(
                Info("Extra","Extra info test")
            ))
        )
        adapter.submitList(list)
        infoRecyclerView.adapter = adapter
        infoRecyclerView.layoutManager = layoutManager
    }
}
