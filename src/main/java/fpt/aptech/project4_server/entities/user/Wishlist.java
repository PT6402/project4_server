/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.entities.user;

import fpt.aptech.project4_server.entities.BaseEntity;
import fpt.aptech.project4_server.entities.book.Book;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author macos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_wishlist")
public class Wishlist extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "userdetail_id")
    private UserDetail userDetail;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
