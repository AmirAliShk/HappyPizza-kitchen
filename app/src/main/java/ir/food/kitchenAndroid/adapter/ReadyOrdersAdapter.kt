package ir.food.kitchenAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.CallDialog
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ProductModel
import ir.food.kitchenAndroid.model.ReadyOrdersModel

class ReadyOrdersAdapter(list: ArrayList<ReadyOrdersModel>) :
    RecyclerView.Adapter<ReadyOrdersAdapter.ViewHolder>() {

    private val models = list

    var productModels: ArrayList<ProductModel> = ArrayList()
    var adapter: ProductsAdapter = ProductsAdapter(productModels)

    class ViewHolder(val binding: ItemReadyOrdersBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReadyOrdersBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtCustomerName.text = model.customerFamily
        holder.binding.txtDate.text = DateHelper.parseFormat(model.createdAt)
        holder.binding.txtAddress.text = model.address

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

        holder.binding.imgCall.setOnClickListener { CallDialog().show(model.customerMobile) }
    }

    override fun getItemCount(): Int {
        return models.size
    }

}