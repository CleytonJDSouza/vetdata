package com.project.vetdata.dto;

public class LifeDTO {

    private Integer max;
    private Integer min;

    public LifeDTO() {
    }

    public LifeDTO(Integer min, Integer max) {
        this.min = min;
        this.max = max;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }
}

