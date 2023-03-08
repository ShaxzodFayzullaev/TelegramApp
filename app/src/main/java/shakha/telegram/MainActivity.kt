package shakha.telegram

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import shakha.telegram.models.Profile
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), RvEvent {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myDBHelper: MyDBHelper
    private lateinit var rvAdapter: RvAdapter
    private lateinit var itemDialogBinding: ItemDialogBinding
    private lateinit var absolutePath: String
    private var list = ArrayList<Profile>()
    var type = 0

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        absolutePath = ""
        setContentView(binding.root)
        myDBHelper = MyDBHelper(this)
        sortList()
        rvAdapter = RvAdapter(list, this)
        binding.apply {
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
                            val profile = Profile(
                                null,
                                first.text.toString().trim(),
                                second.text.toString().trim(),
                                absolutePath
                            )
                            myDBHelper.addPro(profile)
                            Toast.makeText(
                                this@MainActivity,
                                "${first.text.toString()}  Saved",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (type == 0) {
                                sortList()
                            } else if (type == 1) {
                                ReSortList()
                            } else if (type == 2) {
                                sortList()
                            } else {
                                ReSortList()
                            }
                            dialog.dismiss()
                            rvAdapter.notifyDataSetChanged()
                            rvAdapter.list = list
                            absolutePath = ""
                        } else {
                            Toast.makeText(
                                this@MainActivity, "Enter information", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                dialog.show()
            }
        }
    }

    private fun sortList() {
        list.clear()
        for (word in 'A'..'Z') {
            myDBHelper.getAllPro().forEach {
                if (it.name?.substring(0, 1)?.lowercase() == word.toString()
                        .lowercase() && it.name?.substring(0, 1)?.uppercase() == word.toString()
                        .uppercase()
                ) {
                    list.add(it)
                }
            }
        }
    }

    private fun ReSortList() {
        list.clear()
        for (word in 'Z' downTo 'A') {
            myDBHelper.getAllPro().forEach {
                if (it.name?.substring(0, 1)?.lowercase() == word.toString()
                        .lowercase() && it.name?.substring(0, 1)?.uppercase() == word.toString()
                        .uppercase()
                ) {
                    list.add(it)
                }
            }
        }
    }

    override fun menuClick(info: Profile, more: ImageView) {
        val popupMenu = PopupMenu(this@MainActivity, more)
        popupMenu.inflate(R.menu.my_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    myDBHelper.deletePro(info)
                    if (type == 0) {
                        sortList()
                    } else if (type == 1) {
                        ReSortList()
                    } else if (type == 2) {
                        sortList()
                    } else {
                        ReSortList()
                    }
                    rvAdapter.list = list
                    rvAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "${info.name} Deleted", Toast.LENGTH_SHORT).show()


                }
                R.id.edit -> {
                    val dialog = BottomSheetDialog(binding.root.context, R.style.NewDialog)
                    itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(itemDialogBinding.root)
                    itemDialogBinding.first.setText(info.name)
                    itemDialogBinding.second.setText(info.info)
                    itemDialogBinding.image.setImageURI(Uri.parse(info.img))
                    rvAdapter.list = list
                    absolutePath = info.img.toString()



                    itemDialogBinding.image.setOnClickListener {
                        getImageContent.launch("image/*")
                    }
                    itemDialogBinding.btnSave.setOnClickListener {
                        if (itemDialogBinding.first.text!!.isNotBlank() && itemDialogBinding.second.text!!.isNotBlank()) {
                            info.name = itemDialogBinding.first.text.toString().trim()
                            info.info = itemDialogBinding.second.text.toString().trim()
                            info.img = absolutePath
                            myDBHelper.editPro(info)
                            if (type == 0) {
                                sortList()
                            } else if (type == 1) {
                                ReSortList()
                            } else if (type == 2) {
                                sortList()
                            } else {
                                ReSortList()
                            }
                            dialog.dismiss()
                            rvAdapter.list = list
                            rvAdapter.notifyDataSetChanged()
                            Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show()
                            absolutePath = ""
                        } else {
                            Toast.makeText(this, "Enter new Information", Toast.LENGTH_SHORT).show()
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
        val title = SimpleDateFormat("yyyyMMdd_hhmm").format(Date())
        val file = File(filesDir, "$title.jpg")
        val fileOutputStream = FileOutputStream(file)
        inputStream?.copyTo(fileOutputStream)
        inputStream?.close()
        fileOutputStream.close()
        absolutePath = file.absolutePath
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        Toast.makeText(this, "Foydali bo'lganidan hursandman", Toast.LENGTH_SHORT).show()
        super.onOptionsMenuClosed(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.AdanZgacha -> {
                myDBHelper.getAllPro()
                sortList()
                rvAdapter.list = list
                rvAdapter.notifyDataSetChanged()
                type = 0

            }
            R.id.ZdanAgacha -> {
                myDBHelper.getAllPro()
                ReSortList()
                rvAdapter.list = list
                rvAdapter.notifyDataSetChanged()
                type = 1

            }
            R.id.osish -> {
                myDBHelper.getAllPro()
                sortList()
                rvAdapter.list = list
                rvAdapter.notifyDataSetChanged()
                type = 2

            }
            R.id.kamayish -> {
                myDBHelper.getAllPro()
                ReSortList()
                rvAdapter.list = list
                rvAdapter.notifyDataSetChanged()
                type = 3
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
