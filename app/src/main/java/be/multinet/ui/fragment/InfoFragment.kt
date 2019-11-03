package be.multinet.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import be.multinet.R
import be.multinet.databinding.FragmentInfoBinding
import be.multinet.model.Info
import be.multinet.model.InfoCategory
import be.multinet.recyclerview.InfoAdapter
import be.multinet.viewmodel.InfoViewModel
import kotlinx.android.synthetic.main.fragment_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This [Fragment] represents the info page.
 */
class InfoFragment : Fragment() {

    val viewmodel: InfoViewModel by viewModel()

    /**
     * The InfoAdapter for this fragment
     */
    private lateinit var infoAdapter: InfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.infoViewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
        loadInfoViewModelData()
        initRecyclerView()
        addInfo()
    }

    private fun loadInfoViewModelData() {
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
        viewmodel.setInfoCategoryList(list)
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.info_title)
    }

    /**
     * give the list of [InfoCategory] to the adapter
     */
    private fun addInfo(){
        val info = viewmodel.getInfoCategoryList().value
        if (info != null){
            infoAdapter.submitList(info)
        }
    }

    /**
     * Setup recyclerView(s) for this fragment
     */
    private fun initRecyclerView(){
        infoRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            infoAdapter = InfoAdapter(activity!!.applicationContext, viewmodel)
            adapter = infoAdapter
        }
    }
}
