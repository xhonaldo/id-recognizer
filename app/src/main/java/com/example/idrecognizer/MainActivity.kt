package com.example.idrecognizer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.idrecognizer.common.IdScanCallback
import com.example.idrecognizer.common.models.IdInfo
import com.example.idrecognizer.common.models.IdType
import com.example.idrecognizer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), IdScanCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var idRecognizer: IdRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idRecognizer = IdRecognizer(
            binding.scanView,
            IdType.ID_CARD_FRONT
        ).apply {
            idScanCallback = this@MainActivity
        }
    }

    override fun onStart() {
        super.onStart()
        idRecognizer.start()
    }

    override fun onStop() {
        super.onStop()
        idRecognizer.stop()
    }

    override fun onIdScanFinished(idInfo: IdInfo) {
        Log.d("XHONI", idInfo.toString())
    }
}