package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.OrderDto
import com.radovan.spring.entity.{CartEntity, CartItemEntity, CustomerEntity, OrderAddressEntity, OrderEntity, OrderItemEntity, ProductEntity, ShippingAddressEntity, UserEntity}
import com.radovan.spring.exceptions.InsufficientStockException
import com.radovan.spring.repository.{CartItemRepository, CustomerRepository, OrderAddressRepository, OrderItemRepository, OrderRepository, ProductRepository}
import com.radovan.spring.service.{CartService, OrderService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp
import java.text.DecimalFormat
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}
import java.util
import java.util.Optional

@Service
@Transactional
class OrderServiceImpl extends OrderService {

  @Autowired
  private val orderRepository: OrderRepository = null

  @Autowired
  private val customerRepository: CustomerRepository = null

  @Autowired
  private val tempConverter: TempConverter = null

  @Autowired
  private val productRepository: ProductRepository = null

  @Autowired
  private val orderItemRepository: OrderItemRepository = null

  @Autowired
  private val cartItemRepository: CartItemRepository = null

  @Autowired
  private val cartService: CartService = null

  @Autowired
  private val orderAddressRepository: OrderAddressRepository = null

  private val decfor: DecimalFormat = new DecimalFormat("0.00")

  override def addCustomerOrder(): OrderDto = { // TODO Auto-generated method stub
    val authUser: UserEntity = SecurityContextHolder.getContext.getAuthentication.getPrincipal.asInstanceOf[UserEntity]
    val customerEntity: CustomerEntity = customerRepository.findByUserId(authUser.getId)
    val shippingAddress: ShippingAddressEntity = customerEntity.getShippingAddress
    val cartEntity: CartEntity = customerEntity.getCart
    val orderEntity: OrderEntity = new OrderEntity
    val orderedItems: util.List[OrderItemEntity] = new util.ArrayList[OrderItemEntity]
    val cartItems: util.List[CartItemEntity] = cartEntity.getCartItems
    if (!cartItems.isEmpty) cartItems.forEach((item: CartItemEntity) => {
      def foo(item: CartItemEntity) = {
        val productEntity: Optional[ProductEntity] = Optional.ofNullable(item.getProduct)
        if (productEntity.isPresent) if (productEntity.get.getUnitStock < item.getQuantity) {
          val error: Error = new Error("Not enough stock")
          throw new InsufficientStockException(error)
        }
        else {
          val tempProduct: ProductEntity = productEntity.get
          val newStock: Integer = tempProduct.getUnitStock - item.getQuantity
          tempProduct.setUnitStock(newStock)
          productRepository.saveAndFlush(tempProduct)
        }
      }

      foo(item)
    })
    if (!cartItems.isEmpty) cartItems.forEach((item: CartItemEntity) => {
      def foo(item: CartItemEntity) = {
        val orderedItem: OrderItemEntity = tempConverter.cartItemToOrderItemEntity(item)
        val storedOrderedItem: OrderItemEntity = orderItemRepository.save(orderedItem)
        orderedItems.add(storedOrderedItem)
      }

      foo(item)
    })
    val orderAddress: OrderAddressEntity = tempConverter.shippingAddressToOrderAddress(shippingAddress)
    val storedOrderAddress: OrderAddressEntity = orderAddressRepository.save(orderAddress)
    orderEntity.setCart(cartEntity)
    orderEntity.setCustomer(customerEntity)
    orderEntity.setOrderedItems(orderedItems)
    orderEntity.setAddress(storedOrderAddress)
    val currentTime: ZonedDateTime = LocalDateTime.now.atZone(ZoneId.of("Europe/Belgrade"))
    val createdAt: Timestamp = new Timestamp(currentTime.toInstant.getEpochSecond * 1000L)
    orderEntity.setCreatedAt(createdAt)
    val storedOrder: OrderEntity = orderRepository.save(orderEntity)
    storedOrderAddress.setOrder(storedOrder)
    orderAddressRepository.saveAndFlush(storedOrderAddress)
    val returnValue: OrderDto = tempConverter.orderEntityToDto(storedOrder)
    storedOrder.getOrderedItems.forEach((item: OrderItemEntity) => {
      def foo(item: OrderItemEntity) = {
        item.setOrder(storedOrder)
        orderItemRepository.saveAndFlush(item)
      }

      foo(item)
    })
    cartItemRepository.removeAllByCartId(cartEntity.getCartId)
    cartService.refreshCartState(cartEntity.getCartId)
    returnValue
  }

  override def listAll: util.List[OrderDto] = {
    val allOrders: util.List[OrderEntity] = orderRepository.findAll
    val returnValue: util.List[OrderDto] = new util.ArrayList[OrderDto]
    allOrders.forEach((order: OrderEntity) => {
      def foo(order: OrderEntity) = {
        val orderDto: OrderDto = tempConverter.orderEntityToDto(order)
        returnValue.add(orderDto)
      }

      foo(order)
    })
    returnValue
  }

  override def calculateOrderTotal(orderId: Integer): Float = {
    val orderTotalOpt: Optional[Float] = Optional.ofNullable(orderItemRepository.calculateGrandTotal(orderId))
    var returnValue: Float = 0f
    if (orderTotalOpt.isPresent) {
      var orderTotal: Float = orderTotalOpt.get
      orderTotal = decfor.format(orderTotal).toFloat
      returnValue = orderTotal
    }
    returnValue
  }

  override def getOrder(orderId: Integer): OrderDto = {
    val orderEntity: OrderEntity = orderRepository.findById(orderId).get
    val returnValue: OrderDto = tempConverter.orderEntityToDto(orderEntity)
    returnValue
  }

  override def deleteOrder(orderId: Integer): Unit = {
    orderRepository.deleteById(orderId)
    orderRepository.flush()
  }

  override def listAllByCustomerId(customerId: Integer): util.List[OrderDto] = {
    val returnValue: util.List[OrderDto] = new util.ArrayList[OrderDto]
    val allOrders: util.List[OrderEntity] = orderRepository.findAllByCustomerId(customerId)
    allOrders.forEach((order: OrderEntity) => {
      def foo(order: OrderEntity) = {
        val orderDto: OrderDto = tempConverter.orderEntityToDto(order)
        returnValue.add(orderDto)
      }

      foo(order)
    })
    returnValue
  }
}

