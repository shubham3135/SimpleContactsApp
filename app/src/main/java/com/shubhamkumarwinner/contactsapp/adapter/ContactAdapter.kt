package com.shubhamkumarwinner.contactsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shubhamkumarwinner.contactsapp.data.Contacts
import com.shubhamkumarwinner.contactsapp.databinding.ContactItemBinding


class ContactAdapter(private val onClickListener: OnClickListener): ListAdapter<Contacts, ContactAdapter.ContactViewHolder>(REPO_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(ContactItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(contact, holder.itemView.context)
        }
    }


    class ContactViewHolder(private val binding: ContactItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(contact: Contacts){
            binding.name.text = contact.name
            binding.number.text = contact.number
            binding.quickContact.setImageURI(contact.imgUri.toUri())
            binding.quickContact.assignContactUri(contact.contactUri)
        }
    }
    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Contacts>() {
            override fun areItemsTheSame(oldItem: Contacts, newItem: Contacts): Boolean{
                return oldItem.number == newItem.number
            }

            override fun areContentsTheSame(oldItem: Contacts, newItem: Contacts): Boolean =
                oldItem == newItem
        }
    }


}

class OnClickListener {
    fun onClick(contact: Contacts, context: Context){
        Toast.makeText(context, "Name is ${contact.name}", Toast.LENGTH_LONG).show()
    }
}