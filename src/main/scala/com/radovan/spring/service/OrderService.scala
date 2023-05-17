package com.radovan.spring.service

import com.radovan.spring.dto.OrderDto

import java.util

trait OrderService {

  def addCustomerOrder(): OrderDto

  def listAll: util.List[OrderDto]

  def calculateOrderTotal(orderId: Integer): Float

  def getOrder(orderId: Integer): OrderDto

  def deleteOrder(orderId: Integer): Unit

  def listAllByCustomerId(customerId: Integer): util.List[OrderDto]

}
