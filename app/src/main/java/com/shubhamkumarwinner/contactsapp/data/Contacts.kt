package com.shubhamkumarwinner.contactsapp.data

import android.net.Uri

data class Contacts(
    val name: String,
    val number: String,
    val imgUri: String,
    val contactUri: Uri
)
