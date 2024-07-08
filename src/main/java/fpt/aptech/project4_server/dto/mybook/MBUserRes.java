/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.mybook;

import jakarta.persistence.Lob;

/**
 *
 * @author macos
 */
public class MBUserRes {
    private String bookname;
    private int bookid;
    private String bookAuthor;
    @Lob
    private byte[] fileImage;
    
    public MBUserRes(String bookname, int bookid, String bookAuthor, byte[] fileImage) {
        this.bookname = bookname;
        this.bookid = bookid;
        this.bookAuthor = bookAuthor;
        this.fileImage = fileImage;
    }

    // Getter and setters (omitted for brevity, but you need to have them)

    // Default constructor (required by some frameworks like Spring)
    public MBUserRes() {
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

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public byte[] getFileImage() {
        return fileImage;
    }

    public void setFileImage(byte[] fileImage) {
        this.fileImage = fileImage;
    }
    
}
