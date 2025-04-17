package com.project.vetdata.mappers;

public interface MapperDTOToEntity <P,R> {
    R map(P p);
}
