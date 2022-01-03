package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.databinding.FragmentCheckoutDeliBinding
import ir.food.kitchenAndroid.databinding.FragmentReadyOrdersBinding

class CheckoutDeliFragment : Fragment() {
    lateinit var binding: FragmentCheckoutDeliBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutDeliBinding.inflate(layoutInflater)

        return binding.root
    }

}