package ir.food.kitchenAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemOrdersBinding
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrdersModel
import ir.food.kitchenAndroid.model.ProductModel
import org.json.JSONException
import org.json.JSONObject

class OrdersAdapter(list: ArrayList<OrdersModel>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    private val models = list
    lateinit var adapter: ProductsAdapter
    private val productModels: ArrayList<ProductModel> = ArrayList()

    class ViewHolder(val binding: ItemOrdersBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrdersBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.customerName.text = model.customerFamily
        holder.binding.time.text = model.createdAt

        try {
            for (i in 0 until model.products.length()) {
                val dataObj: JSONObject = model.products.getJSONObject(i)

                var model = ProductModel(
                    dataObj.getString("name"),
                    dataObj.getInt("quantity")
                )

                productModels.add(model)
            }
            adapter = ProductsAdapter(productModels)
            holder.binding.productList.adapter = adapter;
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }
}