package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.CartItemDto
import com.radovan.spring.entity.{CartEntity, CartItemEntity}
import com.radovan.spring.repository.{CartItemRepository, CartRepository}
import com.radovan.spring.service.{CartItemService, CartService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.Optional

@Service
@Transactional
class CartItemServiceImpl extends CartItemService {

  @Autowired
  private val cartItemRepository: CartItemRepository = null

  @Autowired
  private val tempConverter: TempConverter = null

  @Autowired
  private val cartService: CartService = null

  @Autowired
  private val cartRepository: CartRepository = null

  override def addCartItem(cartItem: CartItemDto): CartItemDto = {
    val cartItemEntity: CartItemEntity = tempConverter.cartItemDtoToEntity(cartItem)
    val storedItem: CartItemEntity = cartItemRepository.save(cartItemEntity)
    val returnValue: CartItemDto = tempConverter.cartItemEntityToDto(storedItem)
    returnValue
  }

  override def removeCartItem(cartId: Integer, itemId: Integer): Unit = {
    cartItemRepository.removeCartItem(itemId)
    cartItemRepository.flush()
    cartService.refreshCartState(cartId)
  }

  override def eraseAllCartItems(cartId: Integer): Unit = {
    cartItemRepository.removeAllByCartId(cartId)
    cartItemRepository.flush()
    cartService.refreshCartState(cartId)
  }

  override def listAllByCartId(cartId: Integer): util.List[CartItemDto] = {
    val cartItems: util.List[CartItemEntity] = cartItemRepository.findAllByCartId(cartId)
    val returnValue: util.List[CartItemDto] = new util.ArrayList[CartItemDto]
    cartItems.forEach((item: CartItemEntity) => {
      def foo(item: CartItemEntity) = {
        val itemDto: CartItemDto = tempConverter.cartItemEntityToDto(item)
        returnValue.add(itemDto)
      }

      foo(item)
    })
    returnValue
  }

  override def getCartItem(id: Integer): CartItemDto = {
    val cartItemEntity: Optional[CartItemEntity] = cartItemRepository.findById(id)
    var returnValue: CartItemDto = null
    if (cartItemEntity.isPresent) returnValue = tempConverter.cartItemEntityToDto(cartItemEntity.get)
    returnValue
  }

  override def eraseAllByProductId(productId: Integer): Unit = {
    cartItemRepository.removeAllByProductId(productId)
    cartItemRepository.flush()
    val allCarts: util.List[CartEntity] = cartRepository.findAll
    allCarts.forEach((cartEntity: CartEntity) => {
      def foo(cartEntity: CartEntity): Unit = {
        cartService.refreshCartState(cartEntity.getCartId)
      }

      foo(cartEntity)
    })
  }
}

