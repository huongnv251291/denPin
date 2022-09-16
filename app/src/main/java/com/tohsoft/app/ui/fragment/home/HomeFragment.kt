package com.tohsoft.app.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.tohsoft.app.databinding.FragmentBrightnessBinding
import com.tohsoft.app.databinding.FragmentHomeBinding
import com.tohsoft.base.mvp.ui.BaseFragment

class HomeFragment : BaseFragment() {
    private lateinit var mBinding: FragmentBrightnessBinding
    private lateinit var mViewModel: HomeFragmentViewModel
    private var mLoopingLayoutManager: LoopingLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[HomeFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentBrightnessBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mLoopingLayoutManager =
//            LoopingLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        mBinding.recycler.layoutManager = mLoopingLayoutManager
//        mBinding.recycler.adapter = AdapterSettingLight(mBinding.recycler)

    }
}