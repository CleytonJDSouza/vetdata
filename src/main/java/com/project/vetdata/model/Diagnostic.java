package com.project.vetdata.model;

import jakarta.persistence.*;

@Entity
@Table(name = "diagnostic")
public class Diagnostic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String observation;

    public Diagnostic(){
    }

    public Diagnostic(String description, String observation) {
        this.description = description;
        this.observation = observation;
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

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
