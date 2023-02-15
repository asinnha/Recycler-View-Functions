package com.example.recyclerviewtask

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewtask.databinding.RecyclerViewItemBinding
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.NonDisposableHandle.parent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomAdapter(private val mList:ArrayList<InformationModal>, private val context: Context): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    lateinit var listener: ClickListener

    fun itemFunction(listener: ClickListener){
        this.listener = listener
    }

    class ViewHolder(val binding: RecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root) {

        val drag = binding.dragHandle
        val text = binding.textView
        val time = binding.time


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context),
        parent,false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = mList[position].title
        holder.time.text = mList[position].time
        holder.drag.setOnClickListener {
            FancyToast.makeText(context,"Long Press To Move Position",
                FancyToast.LENGTH_LONG, FancyToast.INFO,true).show()
        }
        holder.binding.root.setOnClickListener{
            listener.onItemClick(position)
        }
    }
}