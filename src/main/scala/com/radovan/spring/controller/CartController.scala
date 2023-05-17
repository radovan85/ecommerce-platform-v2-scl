package com.radovan.spring.controller

import com.radovan.spring.dto.{CartDto, CartItemDto, CustomerDto, ProductDto, UserDto}
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.service.{CartItemService, CartService, CustomerService, ProductService, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestMapping}

import java.util

@Controller
@RequestMapping(value = Array("/cart"))
class CartController {

  @Autowired
  private val userService: UserService = null

  @Autowired
  private val customerService: CustomerService = null

  @Autowired
  private val cartService: CartService = null

  @Autowired
  private val cartItemService: CartItemService = null

  @Autowired
  private val productService: ProductService = null

  @GetMapping(value = Array("/viewCart"))
  def getCart(map: ModelMap): String = {
    val authUser: UserDto = userService.getCurrentUser
    val customer: CustomerDto = customerService.getCustomerByUserId(authUser.getId)
    val cart: CartDto = cartService.getCartByCartId(customer.getCartId)
    val allCartItems: util.List[CartItemDto] = cartItemService.listAllByCartId(cart.getCartId)
    val allProducts: util.List[ProductDto] = productService.listAll
    val fullPrice: Float = cartService.calculateFullPrice(cart.getCartId)
    map.put("allCartItems", allCartItems)
    map.put("allProducts", allProducts)
    map.put("fullPrice", fullPrice.asInstanceOf[java.lang.Float])
    map.put("cart", cart)
    "fragments/cart :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/add/{productId}"))
  def addCartItem(@PathVariable(value = "productId") productId: Integer): String = {
    val user: UserEntity = SecurityContextHolder.getContext.getAuthentication.getPrincipal.asInstanceOf[UserEntity]
    val customer: CustomerDto = customerService.getCustomerByUserId(user.getId)
    val cart: CartDto = cartService.getCartByCartId(customer.getCartId)
    val cartItemIds: util.List[Integer] = cart.getCartItemsIds
    val product: ProductDto = productService.getProduct(productId)
    for (i <- 0 until cartItemIds.size) {
      val itemId: Integer = cartItemIds.get(i)
      val cartItem: CartItemDto = cartItemService.getCartItem(itemId)
      if (product.getProductId.intValue == cartItem.getProductId.intValue) {
        cartItem.setQuantity(cartItem.getQuantity + 1)
        val tempProduct: ProductDto = productService.getProduct(cartItem.getProductId)
        cartItem.setPrice(cartItem.getQuantity * tempProduct.getProductPrice)
        cartItemService.addCartItem(cartItem)
        cartService.refreshCartState(cart.getCartId)
        return "fragments/homePage :: ajaxContentLoaded"
      }
    }
    val cartItem: CartItemDto = new CartItemDto
    cartItem.setQuantity(1)
    cartItem.setProductId(productId)
    cartItem.setPrice(product.getProductPrice * 1)
    cartItem.setCartId(cart.getCartId)
    cartItemService.addCartItem(cartItem)
    cartService.refreshCartState(cart.getCartId)
    "fragments/homePage :: ajaxContentLoaded"
  }

  @GetMapping(value = Array("/removeCartItem/{cartId}/{itemId}"))
  def removeCartItem(@PathVariable(value = "cartId") cartId: Integer, @PathVariable(value = "itemId") itemId: Integer): String = {
    cartItemService.removeCartItem(cartId, itemId)
    "fragments/homePage :: ajaxContentLoaded"
  }

  @GetMapping(value = Array("/removeAllItems/{cartId}"))
  def removeAllCartItems(@PathVariable(value = "cartId") cartId: Integer): String = {
    cartItemService.eraseAllCartItems(cartId)
    "fragments/homePage :: ajaxContentLoaded"
  }
}

