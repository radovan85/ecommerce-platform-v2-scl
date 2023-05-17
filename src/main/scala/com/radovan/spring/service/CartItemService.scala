package com.radovan.spring.service

import com.radovan.spring.dto.CartItemDto

import java.util

trait CartItemService {

  def addCartItem(cartItem: CartItemDto): CartItemDto

  def removeCartItem(cartId: Integer, itemId: Integer): Unit

  def eraseAllCartItems(cartId: Integer): Unit

  def listAllByCartId(cartId: Integer): util.List[CartItemDto]

  def getCartItem(id: Integer): CartItemDto

  def eraseAllByProductId(productId: Integer): Unit
}
