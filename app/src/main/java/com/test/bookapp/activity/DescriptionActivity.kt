package com.test.bookapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import com.test.bookapp.R
import com.test.bookapp.database.BookDatabase
import com.test.bookapp.database.BookEntity
import com.test.bookapp.util.ConnectionManager
import org.json.JSONObject

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

        if (ConnectionManager().checkConnection(this@DescriptionActivity)) {
            try {
                val jsonRequest = object :
                    JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                        val success = it.getBoolean("success")
                        if (success) {
                            rlProgressDesription.visibility = View.GONE

                            val bookJsonObject = it.getJSONObject("book_data")

                            val bookImage: String = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.img_book_cover).into(imgBookDescription)
                            txtBookNameDescription.text = bookJsonObject.getString("name")
                            txtBookAuthorDescription.text = bookJsonObject.getString("author")
                            txtBookPriceDescription.text = bookJsonObject.getString("price")
                            txtRatingDescription.text = bookJsonObject.getString("rating")
                            txtAboutBookDescription.text = bookJsonObject.getString("description")


                            //DataBase Checking BookEntity
                            val bookEntity = BookEntity(
                                bookId.toInt(),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("description"),
                                bookImage
                            )

                            val checkFav =
                                DBAsynTask(applicationContext, 1, bookEntity).execute()
                            val isFav = checkFav.get()

                            if (isFav) {
                                btnAddToFavourites.text = "Remove from Favourites"
                                val colorFav =
                                    ContextCompat.getColor(applicationContext, R.color.red)
                                btnAddToFavourites.setBackgroundColor(colorFav)
                            } else {
                                btnAddToFavourites.text = "Add to Favourites"
                                val colorFav =
                                    ContextCompat.getColor(applicationContext, R.color.purple_700)
                                btnAddToFavourites.setBackgroundColor(colorFav)
                            }

                            btnAddToFavourites.setOnClickListener {

                                if (!DBAsynTask(applicationContext, 1, bookEntity).execute().get()){
                                    val async = DBAsynTask(applicationContext, 2, bookEntity).execute()
                                    val result = async.get()
                                    println(result)
                                    if (result) {

                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book Added to Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnAddToFavourites.text = "Remove from Favourites"
                                        val colorFav =
                                            ContextCompat.getColor(applicationContext, R.color.red)
                                        btnAddToFavourites.setBackgroundColor(colorFav)
                                    } else {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Some Error Occured",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    val async = DBAsynTask(applicationContext, 3, bookEntity).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book Removed from Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnAddToFavourites.text = "Add to Favourites"
                                        val colorFav =
                                            ContextCompat.getColor(
                                                applicationContext,
                                                R.color.purple_700
                                            )
                                        btnAddToFavourites.setBackgroundColor(colorFav)
                                    } else {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Some Error Occured",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }

                        }

                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Error is $it",
                            Toast.LENGTH_SHORT
                        )
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

        } else {

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

    class DBAsynTask(
        val context: Context,
        private val mode: Int,
        private val bookEntity: BookEntity
    ) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        Mode 1 -> Check DB if the book is in DB or not
        Mode 2 -> Save the book in DB as favourite
        Mode 3 -> Remove from favourites(DB)
        * */

        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }

                2 -> {
                    println("Mode Activated")
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }

            }

            return false;
        }

    }
}