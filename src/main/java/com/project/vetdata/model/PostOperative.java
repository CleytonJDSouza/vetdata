package com.project.vetdata.model;

import jakarta.persistence.*;

@Entity
@Table(name = "post_operative")
public class PostOperative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    public PostOperative() {
    }

    public PostOperative(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
