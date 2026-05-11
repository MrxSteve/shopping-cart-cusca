package com.prueba.product_service.mapper;

import com.prueba.product_service.dto.ProductResponse;
import com.prueba.product_service.dto.RatingResponse;
import com.prueba.product_service.dto.external.FakeStoreProduct;
import com.prueba.product_service.dto.external.FakeStoreRating;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toProductResponse(FakeStoreProduct product);

    RatingResponse toRatingResponse(FakeStoreRating rating);

    List<ProductResponse> toProductResponseList(List<FakeStoreProduct> products);
}
