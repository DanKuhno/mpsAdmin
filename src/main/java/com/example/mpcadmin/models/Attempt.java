package com.example.mpcadmin.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "attempt")
public class Attempt {
    @Id
    public int id_attempt;
    public long idUsers;
    public boolean status;

    public int getIdAttempt() {
        return id_attempt;
    }
}
