package com.test.bookapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.test.bookapp.*
import com.test.bookapp.adapter.DashboardRecyclerAdapter
import com.test.bookapp.model.Book
import com.test.bookapp.util.ConnectionManager
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException


class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    //lateinit var btnCheckInternet: Button
    var bookInfoList = arrayListOf<Book>()
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    /*val bookInfoList = arrayListOf<Book>(
        Book("In Search of Lost Time ", "ISOLT", "Rs.200", "4.1", R.drawable.img_1),
        Book("Ulysses", "ULY", "Rs.345", "4.4", R.drawable.img_2),
        Book("Don Quixote", "DNQU", "Rs.221", "4.8", R.drawable.img_3),
        Book("One Hundred Years of Solitude", "OHYOS", "Rs.201", "4.0", R.drawable.img_4),
        Book("The Great Gatsby", "TGG", "Rs.143", "4.2", R.drawable.img_5),
        Book("Moby Dick", "MDK", "Rs.92", "3.1", R.drawable.img_6),
        Book("War and Peace", "WAP", "Rs.980", "3.2", R.drawable.img_7),
        Book("Hamlet", "HMLT", "Rs.167", "3.7", R.drawable.img_8),
        Book("The Odyssey", "TODYSY", "Rs.786", "3.9", R.drawable.img_9),
        Book("Madame Bovary", "MDMBVR", "Rs.2100", "3.4", R.drawable.img_10)
    )*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerDashboard = view.findViewById(R.id.dashboardRecyclerView)
        layoutManager = LinearLayoutManager(activity)

        progressBar = view.findViewById(R.id.progreesBar)
        progressLayout = view.findViewById(R.id.progreesLayout)
        progressLayout.visibility = View.VISIBLE


        /*btnCheckInternet = view.findViewById(R.id.btnCheckInternet)
        btnCheckInternet.setOnClickListener {
            if (ConnectionManager().checkConnection(activity as Context)) {

                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Suceess")
                dialog.setMessage("Network is connected")
                dialog.setPositiveButton("Ok") { text, listener -> }
                dialog.setNegativeButton("cancel") { text, listener -> }

                dialog.create()
                dialog.show()

            } else {

                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Failed!")
                dialog.setMessage("Network is not connected")
                dialog.setPositiveButton("Ok") { text, listener -> }
                dialog.setNegativeButton("cancel") { text, listener -> }

                dialog.create()
                dialog.show()
            }
        }*/

        if (ConnectionManager().checkConnection(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v1/book/fetch_books"

            try {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                        //Response are dealed here
                        val success = it.getBoolean("success")

                        if (success) {
                            progressLayout.visibility = View.GONE
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJSONObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJSONObject.getString("book_id"),
                                    bookJSONObject.getString("name"),
                                    bookJSONObject.getString("author"),
                                    bookJSONObject.getString("rating"),
                                    bookJSONObject.getString("price"),
                                    bookJSONObject.getString("image")
                                )

                                bookInfoList.add(bookObject)
                                recyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager

                                /*recyclerDashboard.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerDashboard.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )*/
                            }

                        } else
                            Toast.makeText(
                                activity as Context,
                                "VOLLEY Error has been Occured",
                                Toast.LENGTH_SHORT
                            )

                    }, Response.ErrorListener {

                        println("Error is $it")

                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            var headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "9bf534118365f1"
                            return headers
                        }
                    }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {

                Toast.makeText(
                    activity as Context,
                    "JSON Error Occured",
                    Toast.LENGTH_SHORT
                )

            }


        } else {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failed!")
            dialog.setMessage("Network is not connected")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit App") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }

            dialog.create()
            dialog.show()
        }


        return view
    }

}