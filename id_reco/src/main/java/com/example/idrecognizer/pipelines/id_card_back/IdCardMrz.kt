package com.example.idrecognizer.pipelines.id_card_back

import com.google.firebase.ml.vision.text.FirebaseVisionText
import android.graphics.Rect
import com.example.idrecognizer.common.MrzValidityStatus

class IdCardMrz(
    val lines: List<FirebaseVisionText.Line>,
    val mrzRect: Rect,
    val validityStatus: MrzValidityStatus
)