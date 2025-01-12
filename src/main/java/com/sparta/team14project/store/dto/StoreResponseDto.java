package com.sparta.team14project.store.dto;

import com.sparta.team14project.store.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class StoreResponseDto implements Serializable {
    private Long id;
    private String storeName;
    private String storeDetails;
    private String storeAddress;
    private double avgStar;
    private int storePoint;


    public StoreResponseDto(Store store){
        this.id = store.getId();
        this.storeName = store.getStoreName();
        this.storeDetails = store.getStoreDetails();
        this.storeAddress = store.getStoreAddress();
        this.avgStar = store.getReviewedPeople()!=0?store.getStar()/store.getReviewedPeople():0;
        this.storePoint = store.getStorePoint();
    }
}
