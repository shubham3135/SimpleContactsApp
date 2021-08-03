package com.shubhamkumarwinner.contactsapp.contacts

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.lifecycle.*
import com.shubhamkumarwinner.contactsapp.data.Contacts
import kotlinx.coroutines.launch



class ContactsViewModel(private val application: Application) : ViewModel() {
    private val _contacts = MutableLiveData<List<Contacts>>()
    val contacts: LiveData<List<Contacts>> get() = _contacts

    private var contentObserver: ContentObserver? = null


    fun loadContacts(){
        viewModelScope.launch {
            _contacts.value= queryContact()
            if (contentObserver == null) {
                contentObserver = application.applicationContext.contentResolver.registerObserver(
                    ContactsContract.Contacts.CONTENT_URI
                ) {
                    loadContacts()
                }
            }
        }
    }

    private fun  queryContact(): List<Contacts>{
        val contactList = mutableListOf<Contacts>()
        val contentResolver = application.applicationContext.contentResolver
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY
        )
        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY.uppercase()} ASC"

        viewModelScope.launch {
            val query = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder,
            )

            query?.use { cursor ->
                // Cache column indices.
                val nameColumn =
                    cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                val numberColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val thumbnailColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)

                val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                // Gets the LOOKUP_KEY index
                val lookupKeyColumn = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)
                // Gets a content URI for the contact


                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameColumn)
                    var number = cursor.getString(numberColumn)
                    number = number.replace("[()\\s-]+", "")
                    val thumbnail = cursor.getString(thumbnailColumn) ?: ""
                    val contactUri = ContactsContract.Contacts.getLookupUri(
                        cursor.getLong(idColumn),
                        cursor.getString(lookupKeyColumn)
                    )
                    if (number.length>=10) {
                        contactList += Contacts(name, number, thumbnail, contactUri)
                    }
                }
            }
        }
        return contactList
    }

    private fun ContentResolver.registerObserver(
        uri: Uri,
        observer: (selfChange: Boolean) -> Unit
    ): ContentObserver {
        val contentObserver = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                observer(selfChange)
            }
        }
        registerContentObserver(uri, true, contentObserver)
        return contentObserver
    }
}

class ContactsViewModelProvider(private val application: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}