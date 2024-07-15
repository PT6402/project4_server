/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.wishlist;

import fpt.aptech.project4_server.entities.book.Author;
import jakarta.persistence.Lob;
import java.util.List;

/**
 *
 * @author macos
 */
public class WLUserRes {
    private String bookname;
    private int bookid;
    private List<Author> bookAuthor;
    @Lob
    private byte[] fileImage;
   public WLUserRes(String bookname, int bookid, List<Author> bookAuthor, byte[] fileImage) {
        this.bookname = bookname;
        this.bookid = bookid;
        this.bookAuthor = bookAuthor;
        this.fileImage = fileImage;
    }

    // Getter and setters (omitted for brevity, but you need to have them)

    // Default constructor (required by some frameworks like Spring)
    public WLUserRes() {
    }

    // Getters and setters
    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

   public List<Author> getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(List<Author> bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public byte[] getFileImage() {
        return fileImage;
    }

    public void setFileImage(byte[] fileImage) {
        this.fileImage = fileImage;
    }
     
}
