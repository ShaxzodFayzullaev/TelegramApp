package shakha.telegram.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import shakha.telegram.databinding.ActivityMainBinding
import shakha.telegram.databinding.ItemRvBinding
import shakha.telegram.models.Profile
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class RvAdapter(var list: List<Profile> = emptyList(), val rvEvent: RvEvent) :
    RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(var itemRvBinding: ItemRvBinding) : RecyclerView.ViewHolder(itemRvBinding.root) {
        @SuppressLint("SimpleDateFormat")
        @RequiresApi(Build.VERSION_CODES.O)
        fun onBind(info: Profile, position: Int) {
            itemRvBinding.txtName.text = info.name
            itemRvBinding.txtIzoh.text = info.info
            itemRvBinding.image.setImageURI(Uri.parse(info.img))
            itemRvBinding.more.setOnClickListener {
                rvEvent.menuClick(info, itemRvBinding.more)
            }
            itemRvBinding.txtDate.text = info.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

}