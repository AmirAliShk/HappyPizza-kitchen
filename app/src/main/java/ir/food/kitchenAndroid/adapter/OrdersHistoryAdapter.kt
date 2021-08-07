package ir.food.kitchenAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemOrdersHistoryBinding
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrderHistoryModel
import ir.food.kitchenAndroid.model.ProductModel

class OrdersHistoryAdapter(list: ArrayList<OrderHistoryModel>) :
    RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder>() {

    private val models = list

    var productModels: ArrayList<ProductModel> = ArrayList()
    var adapter: ProductsAdapter = ProductsAdapter(productModels)

    class ViewHolder(val binding: ItemOrdersHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrdersHistoryBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtCustomerName.text = model.customerFamily
        holder.binding.txtDate.text = DateHelper.parseFormat(model.date)
        holder.binding.txtAddress.text = model.address
        holder.binding.txtDescription.text = model.description

        for (i in 0 until model.products.length()) {
            val productsDetail = model.products.getJSONObject(i)
            var model = ProductModel(
                productsDetail.getString("name"),
                productsDetail.getInt("quantity"),
                productsDetail.getString("size")
            )
            productModels.add(model)
        }
        holder.binding.productList.adapter = adapter
    }

    override fun getItemCount(): Int {
        return models.size
    }

}