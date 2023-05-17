package com.radovan.spring.controller

import com.radovan.spring.dto.{AdminMessageDto, BillingAddressDto, CartDto, CustomerDto, OrderAddressDto, OrderDto, OrderItemDto, ProductDto, ShippingAddressDto, UserDto}
import com.radovan.spring.service.{AdminMessageService, BillingAddressService, CartItemService, CartService, CustomerService, OrderAddressService, OrderItemService, OrderService, ProductService, ShippingAddressService, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{GetMapping, ModelAttribute, PathVariable, PostMapping, RequestMapping}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util

@Controller
@RequestMapping(value = Array("/admin"))
class AdminController {

  @Autowired
  private val productService: ProductService = null

  @Autowired
  private val customerService: CustomerService = null

  @Autowired
  private val userService: UserService = null

  @Autowired
  private val billingAddressService: BillingAddressService = null

  @Autowired
  private val shippingAddressService: ShippingAddressService = null

  @Autowired
  private val messageService: AdminMessageService = null

  @Autowired
  private val orderService: OrderService = null

  @Autowired
  private val orderItemService: OrderItemService = null

  @Autowired
  private val orderAddressService: OrderAddressService = null

  @Autowired
  private val cartItemService: CartItemService = null

  @Autowired
  private val cartService: CartService = null

  @GetMapping(value = Array("/"))
  def adminHome: String = "fragments/admin :: ajaxLoadedContent"

  @GetMapping(value = Array("/addNewProduct"))
  def renderProductForm(map: ModelMap): String = {
    val product: ProductDto = new ProductDto
    map.put("product", product)
    "fragments/addProduct :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/createProduct"))
  def createProduct(@ModelAttribute("product") product: ProductDto): String = {
    productService.addProduct(product)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/deleteProduct/{productId}"))
  def deleteProduct(@PathVariable("productId") productId: Integer): String = {
    cartItemService.eraseAllByProductId(productId)
    productService.deleteProduct(productId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/updateProduct/{productId}"))
  def renderUpdateForm(@PathVariable("productId") productId: Integer, map: ModelMap): String = {
    val product: ProductDto = new ProductDto
    val currentProduct: ProductDto = productService.getProduct(productId)
    map.put("product", product)
    map.put("currentProduct", currentProduct)
    "fragments/updateProduct :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/allCustomers"))
  def customerList(map: ModelMap): String = {
    val allCustomers: util.List[CustomerDto] = customerService.getAllCustomers
    val allUsers: util.List[UserDto] = userService.listAllUsers
    map.put("allCustomers", allCustomers)
    map.put("allUsers", allUsers)
    map.put("recordsPerPage", 8.asInstanceOf[Integer])
    "fragments/customerList :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/getCustomer/{customerId}"))
  def getCustomer(@PathVariable("customerId") customerId: Integer, map: ModelMap): String = {
    val customer: CustomerDto = customerService.getCustomer(customerId)
    val tempUser: UserDto = userService.getUserById(customer.getUserId)
    val shippingAddress: ShippingAddressDto = shippingAddressService.getShippingAddress(customer.getShippingAddressId)
    val billingAddress: BillingAddressDto = billingAddressService.getBillingAddress(customer.getBillingAddressId)
    map.put("tempCustomer", customer)
    map.put("tempUser", tempUser)
    map.put("billingAddress", billingAddress)
    map.put("shippingAddress", shippingAddress)
    "fragments/customerDetails :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/deleteCustomer/{customerId}"))
  def removeCustomer(@PathVariable("customerId") customerId: Integer): String = {
    val customer: CustomerDto = customerService.getCustomer(customerId)
    val cart: CartDto = cartService.getCartByCartId(customer.getCartId)
    val billingAddress: BillingAddressDto = billingAddressService.getBillingAddress(customer.getBillingAddressId)
    val shippingAddress: ShippingAddressDto = shippingAddressService.getShippingAddress(customer.getShippingAddressId)
    val user: UserDto = userService.getUserById(customer.getUserId)
    val allOrders: util.List[OrderDto] = orderService.listAllByCustomerId(customerId)
    allOrders.forEach((order: OrderDto) => {
      def foo(order: OrderDto): Unit = {
        orderItemService.eraseAllByOrderId(order.getOrderId)
        orderService.deleteOrder(order.getOrderId)
      }

      foo(order)
    })
    cartItemService.eraseAllCartItems(cart.getCartId)
    messageService.deleteAllByCustomerId(customerId)
    customerService.resetCustomer(customerId)
    billingAddressService.deleteBillingAddress(billingAddress.getBillingAddressId)
    shippingAddressService.deleteShippingAddress(shippingAddress.getShippingAddressId)
    cartService.deleteCart(cart.getCartId)
    customerService.deleteCustomer(customerId)
    userService.deleteUser(user.getId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/allMessages"))
  def listAllMessages(map: ModelMap): String = {
    val allMessages: util.List[AdminMessageDto] = messageService.listAll
    val allCustomers: util.List[CustomerDto] = customerService.getAllCustomers
    val allUsers: util.List[UserDto] = userService.listAllUsers
    map.put("allCustomers", allCustomers)
    map.put("allUsers", allUsers)
    map.put("allMessages", allMessages)
    map.put("recordsPerPage", 5.asInstanceOf[Integer])
    "fragments/messageList :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/messageDetails/{messageId}"))
  def getMessage(@PathVariable("messageId") messageId: Integer, map: ModelMap): String = {
    val message: AdminMessageDto = messageService.getMessage(messageId)
    val customer: CustomerDto = customerService.getCustomer(message.getCustomerId)
    val user: UserDto = userService.getUserById(customer.getUserId)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val dateTime: LocalDateTime = message.getCreatedAt.toLocalDateTime
    val createdAtStr: String = dateTime.format(formatter)
    map.put("message", message)
    map.put("user", user)
    map.put("createdAtStr", createdAtStr)
    "fragments/messageDetails :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/deleteMessage/{messageId}"))
  def deleteReview(@PathVariable("messageId") messageId: Integer): String = {
    messageService.deleteMessage(messageId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/suspendUser/{userId}"))
  def suspendUser(@PathVariable("userId") userId: Integer): String = {
    userService.suspendUser(userId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/allOrders"))
  def listAllOrders(map: ModelMap): String = {
    val allOrders: util.List[OrderDto] = orderService.listAll
    val allCustomers: util.List[CustomerDto] = customerService.getAllCustomers
    val allUsers: util.List[UserDto] = userService.listAllUsers
    map.put("allOrders", allOrders)
    map.put("allCustomers", allCustomers)
    map.put("allUsers", allUsers)
    map.put("recordsPerPage", 10.asInstanceOf[Integer])
    "fragments/orderList :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/deleteOrder/{orderId}"))
  def deleteOrder(@PathVariable("orderId") orderId: Integer): String = {
    orderItemService.eraseAllByOrderId(orderId)
    orderService.deleteOrder(orderId)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/getOrder/{orderId}"))
  def orderDetails(@PathVariable("orderId") orderId: Integer, map: ModelMap): String = {
    val order: OrderDto = orderService.getOrder(orderId)
    val address: OrderAddressDto = orderAddressService.getAddressById(order.getAddressId)
    val allProducts: util.List[ProductDto] = productService.listAll
    val orderPrice: Float = orderService.calculateOrderTotal(orderId)
    val orderedItems: util.List[OrderItemDto] = orderItemService.listAllByOrderId(orderId)
    map.put("order", order)
    map.put("address", address)
    map.put("allProducts", allProducts)
    map.put("orderPrice", orderPrice.asInstanceOf[java.lang.Float])
    map.put("orderedItems", orderedItems)
    "fragments/orderDetails :: ajaxLoadedContent"
  }
}

