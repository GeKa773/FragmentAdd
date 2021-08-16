package com.gekaradchenko.fragmentadd

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.gekaradchenko.fragmentadd.databinding.FragmentCountBinding


class CountFragment(private val number: Int) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentCountBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_count, container, false
        )
        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding.countTextView.text = "$number"
        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedViewModel = sharedViewModel

        binding.notificationTextView.setOnClickListener {
            sharedViewModel.onPushNotification(number)
        }


        return binding.root
    }

}