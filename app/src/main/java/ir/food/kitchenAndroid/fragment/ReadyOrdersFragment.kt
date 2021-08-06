package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentReadyOrdersBinding
import ir.food.kitchenAndroid.helper.TypefaceUtil

class ReadyOrdersFragment : Fragment() {

    lateinit var binding: FragmentReadyOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReadyOrdersBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        return binding.root
    }
}