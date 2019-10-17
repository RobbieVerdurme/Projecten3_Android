package be.multinet.ui.fragment

import android.graphics.Color
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
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * This [Fragment] represents the home page.
 */
class HomeFragment : Fragment() {

    /**
     * The [HomeViewModel] for this fragment.
     */
    lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
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
        setupChart()
    }

    /**
     * Setup the progression chart
     */
    private fun setupChart(){
        chart.description.isEnabled = false
        chart.xAxis.setDrawLabels(false)
        //Set the data
        val points: LineDataSet
        //If there is a dataset, update it
        if(chart.data != null && chart.data.dataSetCount > 0){
            points = chart.data.getDataSetByIndex(0) as LineDataSet
            points.values = viewModel.getChartData()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        }
        else{
            //Otherwise create a new one
            points = LineDataSet(viewModel.getChartData(),viewModel.chartLabel)
            points.setDrawValues(false)
            points.color = Color.BLUE
            points.lineWidth = 2f
            points.formLineWidth = 1f
            points.formSize = 8f
            points.fillColor = Color.LTGRAY

            val dataset = ArrayList<ILineDataSet>()
            dataset.add(points)
            val lineData = LineData(dataset)
            chart.data = lineData
        }
    }


}
