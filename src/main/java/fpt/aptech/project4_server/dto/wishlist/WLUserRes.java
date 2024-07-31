/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.wishlist;

import fpt.aptech.project4_server.entities.book.Author;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 *
 * @author macos
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WLUserRes {

    private String bookname;
    private int bookid;
    private double rating;
    private int ratingQuantity;
    private int wishId;
    private double price;

    @Lob
    private byte[] fileImage;

    // public WLUserRes(String bookname, int bookid, double rating, int
    // ratingQuantity, int wishId, byte[] fileImage) {
    // this.bookname = bookname;
    // this.bookid = bookid;
    // this.rating = rating;
    // this.ratingQuantity = ratingQuantity;
    // this.wishId = wishId;
    // this.fileImage = fileImage;
    // }

    // // Getter and setters (omitted for brevity, but you need to have them)
    // // Default constructor (required by some frameworks like Spring)
    // public WLUserRes() {
    // }

    // // Getters and setters
    // public String getBookname() {
    // return bookname;
    // }

    // public void setBookname(String bookname) {
    // this.bookname = bookname;
    // }

    // public int getBookid() {
    // return bookid;
    // }

    // public void setBookid(int bookid) {
    // this.bookid = bookid;
    // }

    // public int getWishId() {
    // return wishId;
    // }

    // public void setWishId(int wishId) {
    // this.wishId = wishId;
    // }

    // public double getRating() {
    // return rating;
    // }

    // public void setRating(double rating) {
    // this.rating = rating;
    // }

    // public int getRatingQuantity() {
    // return ratingQuantity;
    // }

    // public void setRatingQuantity(int bookid) {
    // this.ratingQuantity = ratingQuantity;
    // }

    // public byte[] getFileImage() {
    // return fileImage;
    // }

    // public void setFileImage(byte[] fileImage) {
    // this.fileImage = fileImage;
    // }

}
