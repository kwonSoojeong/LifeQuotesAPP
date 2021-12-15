package com.crystal.lifequotesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val viewPager: ViewPager2 by lazy{
        findViewById(R.id.viewPager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
    }

    private fun initData() {
     //기본 12시간 케싱, 테스트용으로 킬때마다 패치하도록
        val remoteCongif = Firebase.remoteConfig
        remoteCongif.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteCongif.fetchAndActivate().addOnCompleteListener{
            if(it.isSuccessful){
                val quotes = parseQuotesJson(remoteCongif.getString("quotes"))
                val isNameRevealed = remoteCongif.getBoolean("is_name_revealed")
                displayquotesPager(quotes, isNameRevealed)

            }
        }
    }

    private fun displayquotesPager(quotes: List<Quote>, isNameRevealed:Boolean) {
        viewPager.adapter = QuotesPagerAdapter(quotes, isNameRevealed)
    }

    private fun parseQuotesJson(json: String): List<Quote> {

        val jsonArr = JSONArray(json)
        var jsonList = emptyList<JSONObject>()
        for (index in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(index)
            jsonObj?.let{
                jsonList = jsonList + it
            }
        }
        return jsonList.map {
            Quote(
            quote = it.getString("quote"),
            name = it.getString("name")
        ) }
    }
}