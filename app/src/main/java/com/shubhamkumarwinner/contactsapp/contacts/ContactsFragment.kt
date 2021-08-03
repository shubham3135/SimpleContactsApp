package com.shubhamkumarwinner.contactsapp.contacts

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.shubhamkumarwinner.contactsapp.adapter.ContactAdapter
import com.shubhamkumarwinner.contactsapp.adapter.OnClickListener
import com.shubhamkumarwinner.contactsapp.databinding.ContactsFragmentBinding


class ContactsFragment : Fragment() {

    private lateinit var binding: ContactsFragmentBinding
    private lateinit var viewModel: ContactsViewModel
    private lateinit var viewModelProvider: ContactsViewModelProvider

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = ContactsFragmentBinding.inflate(layoutInflater, container, false)
        viewModelProvider = ContactsViewModelProvider(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelProvider).get(ContactsViewModel::class.java)
        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { result ->
            if (result){
                loadContact()
            }else if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
                val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("You have permanently denied contact permission")
                alertDialog.setMessage("Please allow it in your settings")
                alertDialog.setPositiveButton(
                    "Settings"
                ) { _, _ ->
                    startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .apply {
                                data = Uri.fromParts("package",
                                    requireActivity().packageName, null)
                            })
                }
                alertDialog.setNegativeButton(
                    "Not now"
                ) { _, _ ->

                }
                val alert: AlertDialog = alertDialog.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()
            }
            else{
                Toast.makeText(requireContext(),
                    "Permission is denied",
                    Toast.LENGTH_SHORT).show()
            }
        }

        activityResultLauncher.launch(Manifest.permission.READ_CONTACTS)

        binding.addContact.setOnClickListener {
            addContact("shubhamkumarwinner@gmail.com", "8083126126")
//            addOrEdit()
        }

        return binding.root
    }

    private fun addOrEdit(){
        // Creates a new Intent to insert or edit a contact
        val intentInsertEdit = Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
            // Sets the MIME type
            type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
        }
        // Add code here to insert extended data, if desired

        // Sends the Intent with an request ID
        startActivity(intentInsertEdit)
    }

    private fun addContact(emailAddress: String, phoneNumber: String){
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            // Sets the MIME type to match the Contacts Provider
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }
        intent.apply {
            // Inserts an email address
            putExtra(ContactsContract.Intents.Insert.EMAIL, emailAddress)
            /*
             * In this example, sets the email type to be a work email.
             * You can set other email types as necessary.
             */
            putExtra(
                ContactsContract.Intents.Insert.EMAIL_TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_WORK
            )
            // Inserts a phone number
            putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
            /*
             * In this example, sets the phone type to be a work phone.
             * You can set other phone types as necessary.
             */
            putExtra(
                ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_WORK
            )
        }
        startActivity(intent)
    }

    private fun loadContact(){
        viewModel.loadContacts()
        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            contactAdapter = ContactAdapter(
                OnClickListener()
            )
            binding.contactRecyclerView.adapter = contactAdapter
            contactAdapter.submitList(it)
        })

    }

}