/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.NotePage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author macos
 */
public interface NotePageRepository extends JpaRepository<NotePage, Integer> {
    
}
