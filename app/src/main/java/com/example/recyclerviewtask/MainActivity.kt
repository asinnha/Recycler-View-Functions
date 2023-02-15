package com.example.recyclerviewtask

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewtask.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shashank.sony.fancytoastlib.FancyToast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    var mList = ArrayList<InformationModal>()
    lateinit var adapter: CustomAdapter
    lateinit var rv:RecyclerView
    private val gson = Gson()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = this.getSharedPreferences("array_list",Context.MODE_PRIVATE)

        binding.addTaskButton.setOnClickListener {
            insertAlert()
        }

        loadList()

        rv = binding.rv
        itemTouchHelper.attachToRecyclerView(rv)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = CustomAdapter(mList, this)
        rv.adapter = adapter
        adapter.itemFunction(object : ClickListener {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onItemClick(position: Int) {
                updatePrompt(position, this@MainActivity)
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat")
    private fun insertAlert() {

        val alertDialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.alert_dialog_view,null)
        alertDialog.setView(view)
            .setTitle("Add Name")
            .setIcon(R.drawable.baseline_person_add_alt_1_24)
            .setPositiveButton("ADD") { dialog, _ ->
                val title: EditText = view.findViewById(R.id.titleText)
                val titleText = title.text.toString().trim()
                val time = Calendar.getInstance().time
                val format = SimpleDateFormat("dd-MM-yyyy HH:mm")
                val currentTime = format.format(time).toString()
                if(titleText!=null){ insertInfo(titleText, currentTime) }
                else{FancyToast.makeText(this@MainActivity,"Check Field, might be empty !",
                FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show()}
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL"){ dialog, _ ->
                FancyToast.makeText(this,"Function Cancelled",
                    FancyToast.LENGTH_LONG,FancyToast.DEFAULT,true).show()
                dialog.dismiss()
            }
            .create()
            .show()

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun insertInfo(titleText: String, currentTime: String) {
        mList.add(0,(InformationModal(titleText, currentTime)))
        adapter.notifyDataSetChanged()

        FancyToast.makeText(this,"Item Added",
            FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show()
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.R)
    fun updatePrompt(position: Int, context: Context) {
        val oldTitle = mList[position].title
        val alertDialog = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.alert_dialog_view,null,false)
        alertDialog.setView(view)
            .setTitle("Update $oldTitle to...")
            .setIcon(R.drawable.baseline_person_24)
            .setPositiveButton("UPDATE") { dialog, _ ->
                val title: EditText = view.findViewById(R.id.titleText)
                val titleText = title.text.toString()
                val time = Calendar.getInstance().time
                val format = SimpleDateFormat("dd-MM-yyyy HH:mm")
                val currentTime = format.format(time).toString()
                if(oldTitle!=titleText){
                    if(titleText!=null) updateInfo(titleText, currentTime, position)
                    else FancyToast.makeText(this@MainActivity,"Check Field, might be empty !",
                        FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show()
                }else{
                    FancyToast.makeText(this,"entered name is same",
                        FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL"){ dialog, _ ->
                FancyToast.makeText(context,"Function Cancelled",
                    FancyToast.LENGTH_LONG,FancyToast.DEFAULT,true).show()
                dialog.dismiss()
            }
            .create()
            .show()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateInfo(titleText: String, currentTime: String, position: Int) {

        mList[position] = InformationModal(titleText,currentTime)
        adapter.notifyDataSetChanged()
        FancyToast.makeText(this,"Item Updated",
            FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show()

    }

    private val itemTouchHelperCallBack = object: Callback(){
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(UP or DOWN ,END )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val dragIndex:Int = viewHolder.adapterPosition
            val targetIndex:Int = target.adapterPosition
            Collections.swap(mList,dragIndex,targetIndex)
            adapter.notifyItemMoved(dragIndex,targetIndex)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            when(direction){
                END-> {
                    mList.removeAt(viewHolder.adapterPosition)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    rv.adapter = adapter
                    FancyToast.makeText(this@MainActivity,"item removed...",
                    FancyToast.LENGTH_SHORT,FancyToast.WARNING,true).show()
                }
            }
        }

    }
    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)

    override fun onPause() {
        super.onPause()
        saveList()
    }

    private fun saveList() {
        val listJson = gson.toJson(mList)
        val editor = sharedPref.edit()
        editor.putString("mList",listJson)
        editor.apply()
    }

    private fun loadList() {
        val json = sharedPref.getString("mList",null)
        if(json!= null && json.isNotEmpty()){
            val type = object : TypeToken<ArrayList<InformationModal>>() {}.type
            mList = gson.fromJson(json, type)
        }

    }

}