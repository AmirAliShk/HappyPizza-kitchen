package ir.food.kitchenAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemProductBinding
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.CartModel

class CheckoutDeliAdapter(list: ArrayList<CartModel>) :
    RecyclerView.Adapter<CheckoutDeliAdapter.ViewHolder>() {

    private val models = list

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.productName.text = model.name

        when (model.size) {
            "medium" -> holder.binding.size.text = "۱ نفره"
            "large" -> holder.binding.size.text = "۲ نفره"
            else -> holder.binding.size.text = model.size
        }

        holder.binding.quantity.text = StringHelper.toPersianDigits(model.quantity.toString())
    }

    override fun getItemCount(): Int {
        return models.size
    }

}