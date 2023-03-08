package shakha.telegram.adapters

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import shakha.telegram.databinding.ActivityMainBinding
import shakha.telegram.databinding.ItemRvBinding
import shakha.telegram.models.Profile
import java.time.LocalDateTime
import java.util.*


class RvAdapter(var list: List<Profile> = emptyList(), val rvEvent: RvEvent) :
    RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(var itemRvBinding: ItemRvBinding) : RecyclerView.ViewHolder(itemRvBinding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun onBind(info: Profile, position: Int) {
            itemRvBinding.txtName.text = info.name
            itemRvBinding.txtIzoh.text = info.info
            itemRvBinding.image.setImageURI(Uri.parse(info.img))
            itemRvBinding.more.setOnClickListener {
                rvEvent.menuClick(info, itemRvBinding.more)
            }

            val calendar = Calendar.getInstance()

            val current = LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
            )
            itemRvBinding.txtDate.text = current.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

}