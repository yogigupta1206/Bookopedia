package com.test.bookapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {

    @Insert
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * FROM books")
    fun getAllBooks() : List<BookEntity>

    @Query("SELECT * FROM books WHERE book_id = :bookId")  //  This sign ":" before bookId indicate bookId is just present below next line.
    fun getBookById(bookId : String) : BookEntity
}