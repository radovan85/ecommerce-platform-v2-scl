package com.radovan.spring.repository

import com.radovan.spring.entity.ProductEntity
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.util

@Repository
trait ProductRepository extends JpaRepository[ProductEntity, Integer] {

  @Query(value = "select * from products where product_name ilike CONCAT('%', :keyword, '%') or product_brand ilike CONCAT('%' ,:keyword, '%') or product_model ilike CONCAT('%', :keyword, '%') or category ilike CONCAT('%', :keyword, '%');", nativeQuery = true)
  def findAllByKeyword(@Param("keyword") keyword: String): util.List[ProductEntity]
}
