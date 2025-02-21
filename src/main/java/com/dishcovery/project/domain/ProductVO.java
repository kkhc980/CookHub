package com.dishcovery.project.domain;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductVO {
    private int productId;
    private String productName;
    private int productPrice;
    private String productDescription;
    private int ingredientId;
    private String productImagePath;
    private int stock;
    private Date createdAt;
    private Date updatedAt;
    private String ingredientName;
}
