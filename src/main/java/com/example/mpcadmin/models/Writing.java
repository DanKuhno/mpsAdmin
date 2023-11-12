package com.example.mpcadmin.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Writing {
    @Id
    public long id_writing;
    public Integer idAttempt;
    public Integer id_previous_writing;
    public String text_writing_bot;
    public String text_writing_user;
    public boolean asked;
}
