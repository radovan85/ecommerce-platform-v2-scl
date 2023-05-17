package com.radovan.spring.controller

import com.radovan.spring.dto.{BillingAddressDto, CartDto, CartItemDto, CustomerDto, OrderDto, ProductDto, ShippingAddressDto, UserDto}
import com.radovan.spring.service.{BillingAddressService, CartItemService, CartService, CustomerService, OrderService, ProductService, ShippingAddressService, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{GetMapping, ModelAttribute, PostMapping, RequestMapping}

import java.util

@Controller
@RequestMapping(value = Array("/order"))
class OrderController {

  @Autowired
  private val cartService: CartService = null

  @Autowired
  private val customerService: CustomerService = null

  @Autowired
  private val billingAddressService: BillingAddressService = null

  @Autowired
  private val shippingAddressService: ShippingAddressService = null

  @Autowired
  private val userService: UserService = null

  @Autowired
  private val cartItemService: CartItemService = null

  @Autowired
  private val productService: ProductService = null

  @Autowired
  private val orderService: OrderService = null

  @GetMapping(value = Array("/cancel"))
  def cancelOrder: String = "fragments/checkout/checkOutCancelled :: ajaxLoadedContent"

  @GetMapping(value = Array("/cartError"))
  def cartError: String = "fragments/checkout/invalidCartWarning :: ajaxLoadedContent"

  @GetMapping(value = Array("/billingConfirmation"))
  def checkout(map: ModelMap): String = {
    val authUser: UserDto = userService.getCurrentUser
    val customer: CustomerDto = customerService.getCustomerByUserId(authUser.getId)
    cartService.validateCart(customer.getCartId)
    val billingAddress: BillingAddressDto = new BillingAddressDto
    val currentBillingAddress: BillingAddressDto = billingAddressService.getBillingAddress(customer.getBillingAddressId)
    map.put("billingAddress", billingAddress)
    map.put("currentBillingAddress", currentBillingAddress)
    "fragments/checkout/confirm_billing_details :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/storeBillingAddress"))
  def storeBilling(@ModelAttribute("billingAddress") billingAddress: BillingAddressDto): String = {
    billingAddressService.addBillingAddress(billingAddress)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/shippingConfirmation"))
  def confirmShipping(map: ModelMap): String = {
    val shippingAddress: ShippingAddressDto = new ShippingAddressDto
    val authUser: UserDto = userService.getCurrentUser
    val customer: CustomerDto = customerService.getCustomerByUserId(authUser.getId)
    val currentShippingAddress: ShippingAddressDto = shippingAddressService.getShippingAddress(customer.getShippingAddressId)
    map.put("shippingAddress", shippingAddress)
    map.put("currentShippingAddress", currentShippingAddress)
    "fragments/checkout/confirm_shipping_details :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/storeShippingAddress"))
  def storeShipping(@ModelAttribute("shippingAddress") shippingAddress: ShippingAddressDto): String = {
    shippingAddressService.addShippingAddress(shippingAddress)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/phoneConfirmation"))
  def confirmPhone(map: ModelMap): String = {
    val authUser: UserDto = userService.getCurrentUser
    val customer: CustomerDto = new CustomerDto
    val currentCustomer: CustomerDto = customerService.getCustomerByUserId(authUser.getId)
    map.put("customer", customer)
    map.put("currentCustomer", currentCustomer)
    "fragments/checkout/confirm_customer_phone :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/storeCustomer"))
  def storeCustomer(@ModelAttribute("customer") customer: CustomerDto): String = {
    customerService.addCustomer(customer)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/orderConfirmation"))
  def confirmOrder(map: ModelMap): String = {
    val authUser: UserDto = userService.getCurrentUser
    val customer: CustomerDto = customerService.getCustomerByUserId(authUser.getId)
    val cart: CartDto = cartService.getCartByCartId(customer.getCartId)
    val billingAddress: BillingAddressDto = billingAddressService.getBillingAddress(customer.getBillingAddressId)
    val shippingAddress: ShippingAddressDto = shippingAddressService.getShippingAddress(customer.getShippingAddressId)
    val order: OrderDto = new OrderDto
    val allCartItems: util.List[CartItemDto] = cartItemService.listAllByCartId(cart.getCartId)
    val allProducts: util.List[ProductDto] = productService.listAll
    map.put("cart", cart)
    map.put("billingAddress", billingAddress)
    map.put("shippingAddress", shippingAddress)
    map.put("order", order)
    map.put("allCartItems", allCartItems)
    map.put("allProducts", allProducts)
    "fragments/checkout/orderConfirmation :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/createOrder"))
  def createOrder: String = {
    orderService.addCustomerOrder()
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/stockProblem"))
  def stockProblem: String = "fragments/checkout/stock_exception :: ajaxLoadedContent"

  @GetMapping(value = Array("/orderExecuted"))
  def orderCompleted: String = "fragments/checkout/thankCustomer :: ajaxLoadedContent"

}

