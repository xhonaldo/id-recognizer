package com.example.idrecognizer.common

import com.example.idrecognizer.common.models.IdInfo

interface IdScanCallback {
    fun onIdScanFinished(idInfo: IdInfo)
}