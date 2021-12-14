package ir.food.kitchenAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemProductsBinding
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ProductModel

class ProductsAdapter(list: ArrayList<ProductModel>) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private val models = list

    class ViewHolder(val binding: ItemProductsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductsBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        holder.binding.txtType.text=model.typeName
        holder.binding.txtName.text=model.name
        holder.binding.txtQuantity.text=model.supply.toString()
        holder.binding.txtEditDate.text= StringHelper.toPersianDigits(DateHelper.parseFormatToStringNoDay(model.updatedAt)) + "  " + StringHelper.toPersianDigits(DateHelper.parseFormat(model.updatedAt))
        holder.binding.txtDesc.text=model.description

    }

    override fun getItemCount(): Int {
        return models.size
    }

}