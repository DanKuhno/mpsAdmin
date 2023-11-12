package com.example.mpcadmin.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class Users {
    @Id
    public long id_users;
    public String first_name;
    public String last_name;
    public String username;
    public Integer age;
    public Boolean gender;

}
