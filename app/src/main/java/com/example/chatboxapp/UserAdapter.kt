package com.example.chatboxapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatboxapp.databinding.UserItemBinding

class UserAdapter(private val users: List<Users>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println(users.size.toString())
        val item = users[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = users.size

    class ViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvUser = binding.tvUser
        private val tvPhone = binding.tvPhone
        fun bind(item: Users) {
            tvUser.text = item.fullname
            tvPhone.text = item.phone
        }
    }

}