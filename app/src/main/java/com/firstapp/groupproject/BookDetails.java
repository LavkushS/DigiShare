package com.firstapp.groupproject;

import android.net.Uri;

public class BookDetails {
    //variables for storing the data
    private String book_name,book_author,book_pages,book_genre,book_desc,fileurl;

    public BookDetails(){
        //creating default constructor for Firebase FireStore

    }
    public BookDetails(String book_name, String book_author, String book_pages, String book_genre, String book_desc,String fileurl){
        //Parameterized Constructor for the book details fields
        this.book_name=book_name;
        this.book_author=book_author;
        this.book_pages=book_pages;
        this.book_genre=book_genre;
        this.book_desc=book_desc;
        this.fileurl=fileurl;
    }
    //Creating getter and setter methods for all the input fields
    public String getBook_name(){
        return book_name;
    }
    public void setBook_name(String book_name){
        this.book_name=book_name;
    }
    public String getBook_author(){
        return book_author;
    }
    public void setBook_author(String book_author){
        this.book_author=book_author;
    }
    public String getBook_pages(){
        return book_pages;
    }
    public void setBook_pages(String book_pages){
        this.book_pages=book_pages;
    }
    public String getBook_genre(){
        return book_genre;
    }
    public void setBook_genre(String book_genre){
        this.book_genre=book_genre;
    }
    public String getBook_desc(){
        return book_desc;
    }
    public void setBook_desc(String book_desc){
        this.book_desc=book_desc;
    }
    public String getFileurl(){
        return fileurl;
    }
    public void setFileurl(String fileurl){
        this.fileurl=fileurl;
    }

}
