package ir.food.kitchenAndroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemProductsBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.dialog.ProductDialog
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ProductModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject

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
        holder.binding.txtType.text = model.typeName
        holder.binding.txtName.text = model.name
        holder.binding.txtQuantity.text = "${model.supply}  عدد  "
        holder.binding.txtEditDate.text =
            StringHelper.toPersianDigits(DateHelper.parseFormatToStringNoDay(model.updatedAt)) + "  " + StringHelper.toPersianDigits(
                DateHelper.parseFormat(model.updatedAt)
            )
        holder.binding.imgEdit.setOnClickListener { ProductDialog().show(model, 1) }
        if (model.description.trim().isEmpty()) {
            holder.binding.llDesc.visibility = View.GONE
        } else {
            holder.binding.llDesc.visibility = View.VISIBLE
            holder.binding.txtDesc.text = model.description
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

}