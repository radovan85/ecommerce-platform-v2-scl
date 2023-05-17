package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.CartDto
import com.radovan.spring.exceptions.InvalidCartException
import com.radovan.spring.repository.{CartItemRepository, CartRepository}
import com.radovan.spring.service.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat
import java.util.Optional

@Service
@Transactional
class CartServiceImpl extends CartService {

  @Autowired
  private val cartRepository:CartRepository = null

  @Autowired
  private val tempConverter:TempConverter = null

  @Autowired
  private val cartItemRepository:CartItemRepository = null

  private val decfor = new DecimalFormat("0.00")

  override def getCartByCartId(cartId: Integer): CartDto = {
    val cartEntity = cartRepository.findById(cartId)
    var returnValue = new CartDto
    if (cartEntity.isPresent) returnValue = tempConverter.cartEntityToDto(cartEntity.get)
    else {
      val error = new Error("Invalid cart")
      throw new InvalidCartException(error)
    }
    returnValue
  }

  override def refreshCartState(cartId: Integer): Unit = {
    val cartEntity = cartRepository.findById(cartId).get
    val priceOpt = Optional.ofNullable(cartItemRepository.calculateGrandTotal(cartId))
    if (priceOpt.isPresent) {
      var price = priceOpt.get
      price = decfor.format(price).toFloat
      cartEntity.setCartPrice(price)
    }
    else cartEntity.setCartPrice(0f)
    cartRepository.saveAndFlush(cartEntity)
  }

  override def calculateGrandTotal(cartId: Integer): Float = {
    val grandTotalOpt = Optional.ofNullable(cartItemRepository.calculateGrandTotal(cartId))
    var returnValue = 0f
    if (grandTotalOpt.isPresent) {
      val grandTotal = grandTotalOpt.get
      returnValue = decfor.format(grandTotal).toFloat
    }
    returnValue
  }

  override def calculateFullPrice(cartId: Integer): Float = {
    val fullPriceOpt = Optional.ofNullable(cartItemRepository.calculateFullPrice(cartId))
    var returnValue = 0f
    if (fullPriceOpt.isPresent) {
      val fullPrice = fullPriceOpt.get
      returnValue = decfor.format(fullPrice).toFloat
    }
    returnValue
  }

  override def validateCart(cartId: Integer): CartDto = {
    val cartEntity = cartRepository.findById(cartId)
    var returnValue = new CartDto
    val error = new Error("Invalid Cart")
    if (cartEntity.isPresent) {
      if (cartEntity.get.getCartItems.size == 0) throw new InvalidCartException(error)
      returnValue = tempConverter.cartEntityToDto(cartEntity.get)
    }
    else throw new InvalidCartException(error)
    returnValue
  }

  override def deleteCart(cartId: Integer): Unit = {
    cartRepository.deleteById(cartId)
    cartRepository.flush()
  }
}

