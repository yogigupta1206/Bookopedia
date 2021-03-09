package com.test.bookapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.VoiceInteractor
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import com.test.bookapp.R
import com.test.bookapp.util.ConnectionManager
import org.json.JSONObject
import org.w3c.dom.Text

class DescriptionActivity : AppCompatActivity() {

    lateinit var toolbarDescription: Toolbar
    lateinit var imgBookDescription: ImageView
    lateinit var txtBookNameDescription: TextView
    lateinit var txtBookAuthorDescription: TextView
    lateinit var txtBookPriceDescription: TextView
    lateinit var txtRatingDescription: TextView
    lateinit var txtAboutBookDescription: TextView
    lateinit var btnAddToFavourites: Button
    lateinit var rlProgressDesription: RelativeLayout
    lateinit var pbDescription: ProgressBar

    var bookId: String = "100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        toolbarDescription = findViewById(R.id.toolbarDescription)
        imgBookDescription = findViewById(R.id.imgBookDescription)
        txtBookNameDescription = findViewById(R.id.txtBookNameDescription)
        txtBookAuthorDescription = findViewById(R.id.txtBookAuthorDescription)
        txtBookPriceDescription = findViewById(R.id.txtBookPriceDescription)
        txtRatingDescription = findViewById(R.id.txtRatingDescription)
        txtAboutBookDescription = findViewById(R.id.txtAboutBookDescription)
        btnAddToFavourites = findViewById(R.id.btnAddToFavourites)
        rlProgressDesription = findViewById(R.id.rlProgressDescription)
        pbDescription = findViewById(R.id.pbDescription)

        setSupportActionBar(toolbarDescription)
        supportActionBar?.title = "Book Details"


        rlProgressDesription.visibility = View.VISIBLE
        pbDescription.visibility = View.VISIBLE

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            Toast.makeText(this@DescriptionActivity, "Intent is Null", Toast.LENGTH_SHORT).show()
        }

        if (bookId == "100") {
            finish()
            Toast.makeText(this@DescriptionActivity, "Book Id is 100 Error", Toast.LENGTH_SHORT)
                .show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        val put = jsonParams.put("book_id", bookId)

        if(ConnectionManager().checkConnection(this@DescriptionActivity)){
            try {
                val jsonRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                        val success = it.getBoolean("success")
                        if (success) {
                            rlProgressDesription.visibility = View.GONE

                            val bookJsonObject = it.getJSONObject("book_data")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.img_book_cover).into(imgBookDescription)
                            txtBookNameDescription.text = bookJsonObject.getString("name")
                            txtBookAuthorDescription.text = bookJsonObject.getString("author")
                            txtBookPriceDescription.text = bookJsonObject.getString("price")
                            txtRatingDescription.text = bookJsonObject.getString("rating")
                            txtAboutBookDescription.text = bookJsonObject.getString("description")

                        }

                    }, Response.ErrorListener {
                        Toast.makeText(this@DescriptionActivity, "Error is $it" , Toast.LENGTH_SHORT)
                    }) {

                        override fun getHeaders(): MutableMap<String, String> {
                            var headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "9bf534118365f1"
                            return headers
                        }
                    }

                queue.add(jsonRequest)

            } catch (e: Exception) {
                Toast.makeText(
                    this@DescriptionActivity,
                    "JSON Error Occured",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }else{

            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Failed!")
            dialog.setMessage("Network is not connected")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit App") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }

            dialog.create()
            dialog.show()
        }




    }
}