package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TypesVO {
    private int typeId;         // TYPE_ID (Primary Key)
    private String typeName;    // TYPE_NAME
}
