package com.test.bookapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.test.bookapp.R
import com.test.bookapp.activity.DescriptionActivity
import com.test.bookapp.model.Book
import org.w3c.dom.Text

class DashboardRecyclerAdapter (val context : Context, val itemList: ArrayList<Book>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtBookAuthor : TextView = view.findViewById(R.id.txtAuthorName)
        val txtRating : TextView =view.findViewById(R.id.txtRating)
        val txtRate : TextView = view.findViewById(R.id.txtRate)
        val imgBook : ImageView = view.findViewById(R.id.imgBook)
        val rlBookContent: RelativeLayout = view.findViewById(R.id.rlBookContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_recycler_single_row, parent, false)

        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        //holder.imgBook.setImageResource(book.bookImage)
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtRating.text = book.bookRating
        holder.txtRate.text = book.bookPrice
        Picasso.get().load(book.bookImage).error(R.drawable.img_book_cover).into(holder.imgBook);

        holder.rlBookContent.setOnClickListener{
            //Toast.makeText(context,"Clicked on ${holder.txtBookName.text}",Toast.LENGTH_SHORT).show()
            val intent = Intent(context as Activity, DescriptionActivity::class.java)
            intent.putExtra("book_id", book.bookId)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}