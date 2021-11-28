package com.yyj.frecord

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecordData(var id: Int, var score: Int, var title: String?,
                      var content: String?, val date: Long, var locked: Int,
                      var simple: Int, var where: String?, var what: String?,
                      var feeling: String?, var why: String?, var checked: Boolean) : Parcelable
