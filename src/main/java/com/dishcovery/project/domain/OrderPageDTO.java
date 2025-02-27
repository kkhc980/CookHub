package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderPageDTO {
    private List<OrderPageItemDTO> orders;
}
