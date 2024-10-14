package com.project.vetdata.dto;

import java.util.List;

public class DogBreedResponseDTO {

    private List<DogBreedExternalDTO> data;
    private Meta meta;

    public List<DogBreedExternalDTO> getData() {
        return data;
    }

    public void setData(List<DogBreedExternalDTO> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public static class Meta {
        private int totalPages;

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
}
