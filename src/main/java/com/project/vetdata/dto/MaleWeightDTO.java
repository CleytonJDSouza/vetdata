package com.project.vetdata.dto;

public class MaleWeightDTO {

    private Double max;
    private Double min;

    public MaleWeightDTO() {
    }

    public MaleWeightDTO(Double min, Double max) {
        this.min = min;
        this.max = max;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public void setMin(Double min) {
        this.min = min;
    }
}
