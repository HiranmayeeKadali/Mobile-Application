package com.coddle.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.coddle.databinding.BlogItemBinding
import com.coddle.model.Blog
import com.coddle.util.AppUtil.Companion.getTimeAgo

class BlogAdapter : RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    inner class MyDiffUtil(
        private val oldList: List<Blog>,
        private val newList: List<Blog>
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

    private val blog: ArrayList<Blog> = arrayListOf()
    private var clickListener:((String)->Unit)? = null

    class ViewHolder(val binding: BlogItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            BlogItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            txtTitle.text = blog[position].title
            txtLink.text = blog[position].link
            txtDate.text = getTimeAgo(blog[position].timestamp.toDate().time)

            txtLink.setOnClickListener {
                clickListener?.invoke(blog[position].link)
            }

        }
    }

    override fun getItemCount(): Int {
        return blog.size
    }

    override fun getItemViewType(position: Int): Int {
        return (position)
    }

    fun setData(newList: List<Blog>){
        val diffResult = DiffUtil.calculateDiff(MyDiffUtil(blog,newList))
        blog.clear()
        blog.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnClickListener(listener:(String)->Unit){
        clickListener = listener
    }

}