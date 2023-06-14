package com.example.idrecognizer.pipelines.id_card_back

import android.util.Log
import com.example.idrecognizer.common.CommonSubstringExtractor
import com.example.idrecognizer.common.MrzInfoType
import com.example.idrecognizer.common.MrzInfoValidator.isValid
import com.example.idrecognizer.common.helpers.Utils.mostCommon
import com.example.idrecognizer.common.models.IdInfo
import com.example.idrecognizer.common.models.IdType
import com.example.idrecognizer.common.result_assembler.ResultAssemblerBase
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.util.*
import kotlin.collections.ArrayList

class IdCardBackResultAssembler: ResultAssemblerBase<IdCardMrz, IdInfo>() {

    private var results: HashMap<MrzInfoType, ArrayList<String>> = hashMapOf()
    private val commonSubstringExtractor = CommonSubstringExtractor()

    init {
        MrzInfoType.values().forEach {  results[it] = ArrayList() }
    }

    override fun onSampleAdded(sample: IdCardMrz) {
        val lowestProgress = lowestProgress
        val hasEnoughResults = lowestProgress >= 1
        if (!hasEnoughResults) {
            addResultsFromLines(sample.lines)
            printResultSet()
        } else {
            onFinalResultAssembledListener.onFinalResultAssembled(makeIdInfo())
        }
        onFinalResultAssembledListener.onScanProgressMade(lowestProgress)
    }

    private fun printResultSet() {
        val resultSetStr = StringBuilder("Results: \n")
        for ((key, value1) in results) {
            val entryStr = StringBuilder("$key: ")
            for (value in value1) {
                entryStr.append(value).append(" ")
            }
            resultSetStr.append(entryStr).append("\n")
        }
        Log.d("lmaoresults", resultSetStr.toString())
    }

    private fun addResultsFromLines(lines: List<FirebaseVisionText.Line>) {
        val mrzParser = IdCardMrzParser(lines)
        addResult(MrzInfoType.PERSONAL_NO, mrzParser.extractPersonalNo())
        addResult(MrzInfoType.GENDER, mrzParser.extractGender())
        addResult(MrzInfoType.BIRTHDATE, mrzParser.extractBirthdate())
        addResult(MrzInfoType.EXPIRY_DATE, mrzParser.extractExpiryDate())
        addResult(MrzInfoType.FIRST_NAME, mrzParser.extractFirstName())
        addResult(MrzInfoType.LAST_NAME, mrzParser.extractLastName())
        if (commonSubstringExtractor.sampleSize >= 5) {
            val nameLastName = commonSubstringExtractor.extract()
            Log.d("lmaoresultsname", nameLastName.second + " " + nameLastName.first)
            addResult(MrzInfoType.FIRST_NAME, nameLastName.second)
            addResult(MrzInfoType.LAST_NAME, nameLastName.first)
        } else {
            commonSubstringExtractor.addValue(mrzParser.rawNameAndSurname)
        }
    }

    private val lowestProgress: Float get() {
        val progresses = TreeSet<Float>()
        for ((key, value) in results) {
            val gatheredSamples = value.size
            val requiredSamples = key.requiredSampleSize
            progresses.add(gatheredSamples.toFloat() / requiredSamples)
        }
        return progresses.first()
    }

    private fun addResult(infoType: MrzInfoType, value: String?) {
        if (value != null && isValid(infoType, value)) {
            results[infoType]!!.add(value)
        }
    }

    private fun makeIdInfo(): IdInfo {
        val idInfo = IdInfo()
        idInfo.idType = IdType.ID_CARD_BACK
        idInfo.birthdate = mostCommon(results[MrzInfoType.BIRTHDATE]!!)
        idInfo.name = mostCommon(results[MrzInfoType.FIRST_NAME]!!)
        idInfo.lastname = mostCommon(results[MrzInfoType.LAST_NAME]!!)
        idInfo.expiryDate = mostCommon(results[MrzInfoType.EXPIRY_DATE]!!)
        idInfo.personalNo = mostCommon(results[MrzInfoType.PERSONAL_NO]!!)
        idInfo.gender = mostCommon(results[MrzInfoType.GENDER]!!)
        return idInfo
    }
}