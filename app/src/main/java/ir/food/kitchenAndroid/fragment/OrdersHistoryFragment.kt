package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentOrdersHistoryBinding
import ir.food.kitchenAndroid.helper.TypefaceUtil

class OrdersHistoryFragment : Fragment() {

    lateinit var binding: FragmentOrdersHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersHistoryBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        return binding.root
    }
}