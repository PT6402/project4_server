/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 *
 * @author macos
 */
@Service
public class ForbiddenWordsService {
    
    private static final String FORBIDDEN_WORDS = "abuse,damn,dick,fuck,shit,bitch,bastard,crap,asshole,slut,whore,faggot,cunt,nigger,motherfucker,piss,prick,twat,wanker,douche,retard";

    private String[] forbiddenWordsArray;

    @PostConstruct
    public void init() {
        forbiddenWordsArray = FORBIDDEN_WORDS.split(",");
    }

    public boolean containsForbiddenWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (String word : forbiddenWordsArray) {
            if (text.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
