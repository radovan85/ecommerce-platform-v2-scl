package com.radovan.spring.service

import com.radovan.spring.dto.OrderItemDto

import java.util

trait OrderItemService {

  def listAllByOrderId(orderId: Integer): util.List[OrderItemDto]

  def eraseAllByOrderId(orderId: Integer): Unit

}
