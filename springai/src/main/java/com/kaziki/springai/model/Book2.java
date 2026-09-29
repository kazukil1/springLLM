package com.kaziki.springai.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


public record Book2 (@JsonPropertyDescription("书名") String name,
                     @JsonPropertyDescription("作者")String author,
                     @JsonPropertyDescription("描述") String description,
                     @JsonPropertyDescription("价格")BigDecimal price) {
}
