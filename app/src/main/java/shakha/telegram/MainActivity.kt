package shakha.telegram

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import shakha.telegram.adapters.RvAdapter
import shakha.telegram.adapters.RvEvent
import shakha.telegram.databinding.ActivityMainBinding
import shakha.telegram.databinding.ItemDialogBinding
import shakha.telegram.db.MyDBHelper
import shakha.telegram.db.MyDBHelper.Companion.ASC
import shakha.telegram.db.MyDBHelper.Companion.DESC
import shakha.telegram.db.MyDBHelper.Companion.key_data
import shakha.telegram.db.MyDBHelper.Companion.key_name
import shakha.telegram.models.Profile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), RvEvent {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myDBHelper: MyDBHelper
    private lateinit var rvAdapter: RvAdapter
    private lateinit var itemDialogBinding: ItemDialogBinding
    private lateinit var absolutePath: String
    private lateinit var button: ImageButton
    private var list = ArrayList<Profile>()
    var type = 0

    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        absolutePath = ""
        button = findViewById(R.id.more)
        myDBHelper = MyDBHelper(this)
        myDBHelper.getAllPro().forEach {
            list.add(it)
        }
        rvAdapter = RvAdapter(list, this)
        registerForContextMenu(binding.more)
        binding.apply {
            more.setOnClickListener {
                val popupMenu1 = PopupMenu(this@MainActivity, button)
                popupMenu1.menuInflater.inflate(R.menu.my_menu2, popupMenu1.menu)
                popupMenu1.setOnMenuItemClickListener {
                    Toast.makeText(
                        this@MainActivity,
                        "Type changed to " + it.title,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    when (it.itemId) {
                        R.id.date_up -> {
                            list.clear()
                            myDBHelper.getItems(order = ASC, date = key_data).forEach {
                                list.add(it)
                            }
                            rvAdapter.list = list
                            rvAdapter.notifyDataSetChanged()
                        }
                        R.id.date_down -> {
                            list.clear()
                            myDBHelper.getItems(order = DESC, date = key_data).forEach {
                                list.add(it)
                            }
                            rvAdapter.list = list
                            rvAdapter.notifyDataSetChanged()
                        }
                        R.id.ZdanAgacha -> {
                            list.clear()
                            myDBHelper.getItems(order = DESC, date = key_name).forEach {
                                list.add(it)
                            }
                            rvAdapter.list = list
                            rvAdapter.notifyDataSetChanged()
                        }
                        R.id.AdanZgacha -> {
                            list.clear()
                            myDBHelper.getItems(order = ASC, date = key_name).forEach {
                                list.add(it)
                            }
                            rvAdapter.list = list
                            rvAdapter.notifyDataSetChanged()
                        }

                    }
                    true
                }
                popupMenu1.show()
            }
            rv.adapter = rvAdapter
            btnAdd.setOnClickListener {
                val dialog = BottomSheetDialog(binding.root.context, R.style.NewDialog)
                itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
                dialog.setContentView(itemDialogBinding.root)
                itemDialogBinding.apply {
                    itemDialogBinding.image.setOnClickListener {
                        getImageContent.launch("image/*")
                    }
                    btnSave.setOnClickListener {
                        if (first.text!!.isNotBlank() && second.text!!.isNotBlank()) {
                            val info = Profile(
                                null,
                                first.text.toString().trim(),
                                second.text.toString().trim(),
                                absolutePath,
                                SimpleDateFormat("HH:mm dd-MM-yyyy").format(Calendar.getInstance().time)
                            )

                            Toast.makeText(
                                this@MainActivity,
                                "${first.text.toString()}  Saved",
                                Toast.LENGTH_SHORT
                            ).show()

                            dialog.dismiss()
                            myDBHelper.addPro(info)
                            list.add(info)
                            rvAdapter.list = list
                            rvAdapter.notifyDataSetChanged()
                            absolutePath = ""
                        } else if (first.text!!.isBlank() && second.text!!.isNotBlank()) {
                            Toast.makeText(
                                this@MainActivity, "Ismni kiriting", Toast.LENGTH_SHORT
                            ).show()
                        } else if (second.text!!.isBlank() && first.text!!.isNotBlank()) {
                            Toast.makeText(
                                this@MainActivity,
                                "Ma'lumotni kiriting",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else if (first.text!!.isNotBlank() && second.text!!.isNotBlank()) {
                            Toast.makeText(this@MainActivity, "Rasmni tanlang", Toast.LENGTH_SHORT)
                                .show()
                        } else if (first.text!!.isBlank() && second.text!!.isBlank()) {
                            Toast.makeText(
                                this@MainActivity,
                                "Barcha bosh qatorlarni toldiring",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                dialog.show()
            }
        }
    }

//    private fun sortList() {

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    override fun menuClick(info: Profile, view: ImageView) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.my_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    myDBHelper.deletePro(info)
                    list.forEachIndexed { index, it ->
                        it.id == info.id
                        list.set(index, info)
                    }
                    if (type == 0) {
                        list.clear()
                        myDBHelper.getItems(order = ASC, date = key_name).forEach {
                            list.add(it)
                        }
                        rvAdapter.list = list
                        rvAdapter.notifyDataSetChanged()
                    } else if (type == 1) {
                        list.clear()
                        myDBHelper.getItems(order = DESC, date = key_name).forEach {
                            list.add(it)
                        }
                        rvAdapter.list = list
                        rvAdapter.notifyDataSetChanged()
                    } else if (type == 2) {
                        list.clear()
                        myDBHelper.getItems(order = ASC, date = key_data).forEach {
                            list.add(it)
                        }
                        rvAdapter.list = list
                        rvAdapter.notifyDataSetChanged()
                    } else if (type == 3) {
                        list.clear()
                        myDBHelper.getItems(order = DESC, date = key_data).forEach {

                            list.add(it)
                        }
                        rvAdapter.list = list
                        rvAdapter.notifyDataSetChanged()
                    }
                    list.remove(info)
                    rvAdapter.list = list
                    rvAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "${info.name} O'chirildi", Toast.LENGTH_SHORT).show()
                }
                R.id.edit -> {
                    val dialog = BottomSheetDialog(
                        binding.root.context,
                        R.style.NewDialog
                    )
                    itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(itemDialogBinding.root)
                    itemDialogBinding.first.setText(info.name)
                    itemDialogBinding.second.setText(info.info)
                    itemDialogBinding.image.setImageURI(Uri.parse(info.img))
                    absolutePath = info.img.toString()

                    itemDialogBinding.image.setOnClickListener {
                        getImageContent.launch("image/*")
                    }
                    itemDialogBinding.btnSave.setOnClickListener {
                        if (itemDialogBinding.first.text!!.isNotBlank() && itemDialogBinding.second.text!!.isNotBlank()) {
                            info.name = itemDialogBinding.first.text.toString().trim()
                            info.info = itemDialogBinding.second.text.toString().trim()
                            info.img = absolutePath
                            info.date = info.date
                            myDBHelper.editPro(info)
                            list.forEachIndexed { index, it ->
                                it.id == info.id
                                list.set(index, info)
                            }



                            if (type == 0) {
                                list.clear()
                                myDBHelper.getItems(order = ASC, date = key_name).forEach {
                                    list.add(it)
                                }
                                rvAdapter.list = list
                                rvAdapter.notifyDataSetChanged()
                            } else if (type == 2) {

                                list.clear()
                                myDBHelper.getItems(order = DESC, date = key_name).forEach {
                                    list.add(it)
                                }
                                rvAdapter.list = list
                                rvAdapter.notifyDataSetChanged()
                            } else if (type == 1) {
                                list.clear()
                                myDBHelper.getItems(order = ASC, date = key_data).forEach {
                                    list.add(it)
                                }
                                rvAdapter.list = list
                                rvAdapter.notifyDataSetChanged()

                            } else if (type == 3) {
                                list.clear()
                                myDBHelper.getItems(order = DESC, date = key_data).forEach {
                                    list.add(it)
                                }
                                rvAdapter.list = list
                                rvAdapter.notifyDataSetChanged()
                            }
                            dialog.dismiss()
                            rvAdapter.list = list
                            rvAdapter.notifyDataSetChanged()
                            Toast.makeText(this, "${info.name} Taxrirlandi", Toast.LENGTH_SHORT)
                                .show()
                            absolutePath = ""
                        } else {
                            Toast.makeText(
                                this,
                                "Barcha bo'sh qatorlarni toldiring",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    dialog.show()
                }
            }
            true
        }

        popupMenu.show()
    }

    @SuppressLint("SimpleDateFormat")
    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult
        itemDialogBinding.image.setImageURI(it)
        val inputStream = contentResolver.openInputStream(it)
        val title = SimpleDateFormat("HH:mm dd-MM-yyyy").format(Date())
        val file = File(filesDir, "$title.jpg")
        val fileOutputStream = FileOutputStream(file)
        inputStream?.copyTo(fileOutputStream)
        inputStream?.close()
        fileOutputStream.close()
        absolutePath = file.absolutePath
    }

}

