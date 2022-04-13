package com.bitcoin.myapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bitcoin.myapplication.R
import com.bitcoin.myapplication.data.BitCoinRangeRate
import com.bitcoin.myapplication.data.Status
import com.bitcoin.myapplication.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = MainFragmentBinding.inflate(inflater)
        viewModel.getBitCoin()

        viewModel.uiStatus.observe(viewLifecycleOwner) {

            when (it.status) {
                Status.SUCCESS -> {
                    updateUI(it.data,null)
                }
                Status.ERROR -> updateUI(it.data,it.message)
                Status.LOADING -> showMessage(getString(R.string.loading))
            }
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

    }


    private fun updateUI(bitCoinRangeRate: BitCoinRangeRate?, errorMessage: String?) {
        bitCoinRangeRate?.let {
            showViews(true)
            with(binding) {
                currentValue.text = bitCoinRangeRate.currentRate.toString()
                highestValue.text = bitCoinRangeRate.maxRate.toString()
                lowestValue.text = bitCoinRangeRate.minRate.toString()
                if(errorMessage != null) {
                    error.text = errorMessage
                    error.visibility = View.VISIBLE
                }else{
                    error.visibility = View.GONE
                }

            }
        }

    }

    private fun showMessage(message: String) {

        showViews(false)
            binding.error.text = message

    }
    private fun showViews (show: Boolean){
        val showVisibility = if(show) View.VISIBLE else View.GONE
        with(binding){
            currentValue.visibility = showVisibility
            currentLabel.visibility = showVisibility
            highestValue.visibility = showVisibility
            highestLabel.visibility = showVisibility
            lowestValue.visibility = showVisibility
            lowestLabel.visibility = showVisibility
        }
    }

}