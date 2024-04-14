package com.coddle.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.coddle.databinding.GroomItemBinding
import com.coddle.model.Grooming
import com.coddle.model.Medical
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MedicalAdapter : RecyclerView.Adapter<MedicalAdapter.ViewHolder>() {

    inner class MyDiffUtil(
        private val oldList: List<Medical>,
        private val newList: List<Medical>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    private val medical: ArrayList<Medical> = arrayListOf()

    class ViewHolder(val binding: GroomItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GroomItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            txtDescription.text = medical[position].description
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            txtDate.text = sdf.format(Date(medical[position].date.toLong()))

        }
    }

    override fun getItemCount(): Int {
        return medical.size
    }

    override fun getItemViewType(position: Int): Int {
        return (position)
    }

    fun setData(newList: List<Medical>){
        val diffResult = DiffUtil.calculateDiff(MyDiffUtil(medical,newList))
        medical.clear()
        medical.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

}