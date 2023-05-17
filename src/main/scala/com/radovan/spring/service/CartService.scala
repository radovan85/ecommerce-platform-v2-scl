package com.radovan.spring.service

import com.radovan.spring.dto.CartDto

trait CartService {

  def getCartByCartId(cartId: Integer): CartDto

  def refreshCartState(cartId: Integer): Unit

  def calculateGrandTotal(cartId: Integer): Float

  def validateCart(cartId: Integer): CartDto

  def calculateFullPrice(cartId: Integer): Float

  def deleteCart(cartId: Integer): Unit
}

