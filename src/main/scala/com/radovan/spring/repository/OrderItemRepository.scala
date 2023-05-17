package com.radovan.spring.repository

import com.radovan.spring.entity.OrderItemEntity
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.util

@Repository
trait OrderItemRepository extends JpaRepository[OrderItemEntity, Integer] {

  @Query(value = "select sum(price) from order_items where order_id = :orderId", nativeQuery = true)
  def calculateGrandTotal(@Param("orderId") orderId: Integer): java.lang.Float

  @Query(value = "select * from order_items where order_id = :orderId", nativeQuery = true)
  def findAllByOrderId(@Param("orderId") orderId: Integer): util.List[OrderItemEntity]

  @Modifying
  @Query(value = "delete from order_items where order_id = :orderId", nativeQuery = true)
  def deleteAllByOrderId(@Param("orderId") orderId: Integer): Unit

}

