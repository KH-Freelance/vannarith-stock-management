package com.hfsolution.feature.stockmanagement.dto.cache;

import com.hfsolution.app.dto.BaseCache;
import com.hfsolution.feature.stockmanagement.entity.Customer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerCacheDto extends BaseCache {
    Customer customer;
}
