package com.coddle.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.coddle.adapters.BlogAdapter
import com.coddle.databinding.ActivityBlogBinding
import com.coddle.repository.BlogRepository
import com.coddle.ui.viewModels.BlogProvider
import com.coddle.ui.viewModels.BlogViewModel

class BlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogBinding
    private lateinit var viewModel: BlogViewModel
    private lateinit var provider: BlogProvider
    private val blogAdapter by lazy { BlogAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = BlogProvider(BlogRepository())
        viewModel = ViewModelProvider(this, provider)[BlogViewModel::class.java]


        viewModel.getBlogs.observe(this, {
            binding.progressCircular.visibility = GONE
            binding.rvBlog.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@BlogActivity)
                adapter = blogAdapter
            }
            blogAdapter.setData(it)
        })

        viewModel.getBlogs()


        blogAdapter.setOnClickListener {
            startActivity(
                Intent(this, ViewBlogActivity::class.java)
                    .putExtra("link", it)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        binding.imgBack.setOnClickListener { onBackPressed() }

    }
}