package com.example.machinesshop.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDTO {
  Long id;
  String name;
  BigDecimal price;
  String thumbnail;

}
