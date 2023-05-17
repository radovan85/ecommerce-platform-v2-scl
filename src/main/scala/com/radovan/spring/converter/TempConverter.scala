package com.radovan.spring.converter

import com.radovan.spring.dto.{AdminMessageDto, BillingAddressDto, CartDto, CartItemDto, CustomerDto, OrderAddressDto, OrderDto, OrderItemDto, ProductDto, RoleDto, ShippingAddressDto, UserDto}
import com.radovan.spring.entity.{AdminMessageEntity, BillingAddressEntity, CartEntity, CartItemEntity, CustomerEntity, OrderAddressEntity, OrderEntity, OrderItemEntity, ProductEntity, RoleEntity, ShippingAddressEntity, UserEntity}
import com.radovan.spring.repository.{BillingAddressRepository, CartItemRepository, CartRepository, CustomerRepository, OrderAddressRepository, OrderItemRepository, OrderRepository, ProductRepository, RoleRepository, ShippingAddressRepository, UserRepository}
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp
import java.text.DecimalFormat
import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util
import java.util.Optional

@Component
class TempConverter {

  @Autowired
  private val mapper: ModelMapper = null

  @Autowired
  private val customerRepository: CustomerRepository = null

  @Autowired
  private val cartRepository: CartRepository = null

  @Autowired
  private val userRepository: UserRepository = null

  @Autowired
  private val billingAddressRepository: BillingAddressRepository = null

  @Autowired
  private val shippingAddressRepository: ShippingAddressRepository = null

  @Autowired
  private val productRepository: ProductRepository = null

  @Autowired
  private val cartItemRepository: CartItemRepository = null

  @Autowired
  private val roleRepository: RoleRepository = null

  @Autowired
  private val orderItemRepository: OrderItemRepository = null

  @Autowired
  private val orderRepository: OrderRepository = null

  @Autowired
  private val orderAddressRepository: OrderAddressRepository = null

  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  private val decfor: DecimalFormat = new DecimalFormat("0.00")

  def messageEntityToDto(message: AdminMessageEntity): AdminMessageDto = {
    val returnValue: AdminMessageDto = mapper.map(message, classOf[AdminMessageDto])
    val customerOpt: Optional[CustomerEntity] = Optional.ofNullable(message.getCustomer)
    if (customerOpt.isPresent) returnValue.setCustomerId(customerOpt.get.getCustomerId)
    val createdAtOpt: Optional[Timestamp] = Optional.ofNullable(message.getCreatedAt)
    if (createdAtOpt.isPresent) {
      val createdAtZoned: ZonedDateTime = createdAtOpt.get.toLocalDateTime.atZone(ZoneId.of("Europe/Belgrade"))
      val createdAtStr: String = createdAtZoned.format(formatter)
      returnValue.setCreatedAtStr(createdAtStr)
    }
    returnValue
  }

  def messageDtoToEntity(message: AdminMessageDto): AdminMessageEntity = {
    val returnValue: AdminMessageEntity = mapper.map(message, classOf[AdminMessageEntity])
    val customerIdOpt: Optional[Integer] = Optional.ofNullable(message.getCustomerId)
    if (customerIdOpt.isPresent) {
      val customerId: Integer = customerIdOpt.get
      val customerEntity: CustomerEntity = customerRepository.findById(customerId).get
      returnValue.setCustomer(customerEntity)
    }
    returnValue
  }

  def cartEntityToDto(cartEntity: CartEntity): CartDto = {
    val returnValue: CartDto = mapper.map(cartEntity, classOf[CartDto])
    val cartPriceOpt: Optional[Float] = Optional.ofNullable(cartEntity.getCartPrice)
    if (!cartPriceOpt.isPresent) returnValue.setCartPrice(0f)
    val customerOpt: Optional[CustomerEntity] = Optional.ofNullable(cartEntity.getCustomer)
    if (customerOpt.isPresent) returnValue.setCustomerId(customerOpt.get.getCustomerId)
    val itemsIds: util.List[Integer] = new util.ArrayList[Integer]
    val cartItems: util.List[CartItemEntity] = cartEntity.getCartItems
    cartItems.forEach((itemEntity: CartItemEntity) => {
      def foo(itemEntity: CartItemEntity) = {
        val itemId: Integer = itemEntity.getCartItemId
        itemsIds.add(itemId)
      }

      foo(itemEntity)
    })
    returnValue.setCartItemsIds(itemsIds)
    returnValue
  }

  def cartDtoToEntity(cartDto: CartDto): CartEntity = {
    val returnValue: CartEntity = mapper.map(cartDto, classOf[CartEntity])
    val cartPriceOpt: Optional[Float] = Optional.ofNullable(cartDto.getCartPrice)
    if (!cartPriceOpt.isPresent) returnValue.setCartPrice(0f)
    val customerIdOpt: Optional[Integer] = Optional.ofNullable(cartDto.getCustomerId)
    if (customerIdOpt.isPresent) {
      val customerId: Integer = customerIdOpt.get
      val customerEntity: CustomerEntity = customerRepository.findById(customerId).get
      returnValue.setCustomer(customerEntity)
    }
    val cartItems: util.List[CartItemEntity] = new util.ArrayList[CartItemEntity]
    val itemIds: util.List[Integer] = cartDto.getCartItemsIds
    itemIds.forEach((itemId: Integer) => {
      def foo(itemId: Integer) = {
        val itemEntity: CartItemEntity = cartItemRepository.findById(itemId).get
        cartItems.add(itemEntity)
      }

      foo(itemId)
    })
    returnValue.setCartItems(cartItems)
    returnValue
  }

  def cartItemEntityToDto(cartItemEntity: CartItemEntity): CartItemDto = {
    val returnValue: CartItemDto = mapper.map(cartItemEntity, classOf[CartItemDto])
    val productOpt: Optional[ProductEntity] = Optional.ofNullable(cartItemEntity.getProduct)
    if (productOpt.isPresent) {
      returnValue.setProductId(productOpt.get.getProductId)
      var price: Float = productOpt.get.getProductPrice
      val discount: Float = productOpt.get.getDiscount
      val quantity: Integer = returnValue.getQuantity
      price = (price - ((price / 100) * discount)) * quantity
      price = decfor.format(price).toFloat
      returnValue.setPrice(price)
    }
    val cartOpt: Optional[CartEntity] = Optional.ofNullable(cartItemEntity.getCart)
    if (cartOpt.isPresent) returnValue.setCartId(cartOpt.get.getCartId)
    returnValue
  }

  def cartItemDtoToEntity(cartItemDto: CartItemDto): CartItemEntity = {
    val returnValue: CartItemEntity = mapper.map(cartItemDto, classOf[CartItemEntity])
    val cartIdOpt: Optional[Integer] = Optional.ofNullable(cartItemDto.getCartId)
    if (cartIdOpt.isPresent) {
      val cartId: Integer = cartIdOpt.get
      val cartEntity: CartEntity = cartRepository.findById(cartId).get
      returnValue.setCart(cartEntity)
    }
    val productIdOpt: Optional[Integer] = Optional.ofNullable(cartItemDto.getProductId)
    if (productIdOpt.isPresent) {
      val productId: Integer = productIdOpt.get
      val productEntity: ProductEntity = productRepository.findById(productId).get
      returnValue.setProduct(productEntity)
      var price: Float = productEntity.getProductPrice
      val discount: Float = productEntity.getDiscount
      val quantity: Integer = returnValue.getQuantity
      price = (price - ((price / 100) * discount)) * quantity
      price = decfor.format(price).toFloat
      returnValue.setPrice(price)
    }
    returnValue
  }

  def productEntityToDto(productEntity: ProductEntity): ProductDto = {
    val returnValue: ProductDto = mapper.map(productEntity, classOf[ProductDto])
    val price: Float = decfor.format(returnValue.getProductPrice).toFloat
    val discount: Float = decfor.format(returnValue.getDiscount).toFloat
    returnValue.setProductPrice(price)
    returnValue.setDiscount(discount)
    returnValue
  }

  def productDtoToEntity(productDto: ProductDto): ProductEntity = {
    val returnValue: ProductEntity = mapper.map(productDto, classOf[ProductEntity])
    val price: Float = decfor.format(returnValue.getProductPrice).toFloat
    val discount: Float = decfor.format(returnValue.getDiscount).toFloat
    returnValue.setProductPrice(price)
    returnValue.setDiscount(discount)
    returnValue
  }

  def customerEntityToDto(customerEntity: CustomerEntity): CustomerDto = {
    val returnValue: CustomerDto = mapper.map(customerEntity, classOf[CustomerDto])
    val billingAddressEntity: Optional[BillingAddressEntity] = Optional.ofNullable(customerEntity.getBillingAddress)
    if (billingAddressEntity.isPresent) returnValue.setBillingAddressId(billingAddressEntity.get.getBillingAddressId)
    val shippingAddressEntity: Optional[ShippingAddressEntity] = Optional.ofNullable(customerEntity.getShippingAddress)
    if (shippingAddressEntity.isPresent) returnValue.setShippingAddressId(shippingAddressEntity.get.getShippingAddressId)
    val cartEntity: Optional[CartEntity] = Optional.ofNullable(customerEntity.getCart)
    if (cartEntity.isPresent) returnValue.setCartId(cartEntity.get.getCartId)
    val userEntity: Optional[UserEntity] = Optional.ofNullable(customerEntity.getUser)
    if (userEntity.isPresent) returnValue.setUserId(userEntity.get.getId)
    returnValue
  }

  def customerDtoToEntity(customerDto: CustomerDto): CustomerEntity = {
    val returnValue: CustomerEntity = mapper.map(customerDto, classOf[CustomerEntity])
    val billingAddressIdOpt: Optional[Integer] = Optional.ofNullable(customerDto.getBillingAddressId)
    if (billingAddressIdOpt.isPresent) {
      val billingAddressId: Integer = billingAddressIdOpt.get
      val billingAddressEntity: BillingAddressEntity = billingAddressRepository.findById(billingAddressId).get
      returnValue.setBillingAddress(billingAddressEntity)
    }
    val shippingAddressIdOpt: Optional[Integer] = Optional.ofNullable(customerDto.getShippingAddressId)
    if (shippingAddressIdOpt.isPresent) {
      val shippingAddressId: Integer = shippingAddressIdOpt.get
      val shippingAddressEntity: ShippingAddressEntity = shippingAddressRepository.findById(shippingAddressId).get
      returnValue.setShippingAddress(shippingAddressEntity)
    }
    val cartIdOpt: Optional[Integer] = Optional.ofNullable(customerDto.getCartId)
    if (cartIdOpt.isPresent) {
      val cartId: Integer = cartIdOpt.get
      val cartEntity: CartEntity = cartRepository.findById(cartId).get
      returnValue.setCart(cartEntity)
    }
    val userIdOpt: Optional[Integer] = Optional.ofNullable(customerDto.getUserId)
    if (userIdOpt.isPresent) {
      val userId: Integer = userIdOpt.get
      val userEntity: UserEntity = userRepository.findById(userId).get
      returnValue.setUser(userEntity)
    }
    returnValue
  }

  def billingAddressEntityToDto(addressEntity: BillingAddressEntity): BillingAddressDto = {
    val returnValue: BillingAddressDto = mapper.map(addressEntity, classOf[BillingAddressDto])
    val customerOpt: Optional[CustomerEntity] = Optional.ofNullable(addressEntity.getCustomer)
    if (customerOpt.isPresent) returnValue.setCustomerId(customerOpt.get.getCustomerId)
    returnValue
  }

  def billingAddressDtoToEntity(addressDto: BillingAddressDto): BillingAddressEntity = {
    val returnValue: BillingAddressEntity = mapper.map(addressDto, classOf[BillingAddressEntity])
    val customerIdOpt: Optional[Integer] = Optional.ofNullable(addressDto.getCustomerId)
    if (customerIdOpt.isPresent) {
      val customerId: Integer = customerIdOpt.get
      val customerEntity: CustomerEntity = customerRepository.findById(customerId).get
      returnValue.setCustomer(customerEntity)
    }
    returnValue
  }

  def shippingAddressEntityToDto(addressEntity: ShippingAddressEntity): ShippingAddressDto = {
    val returnValue: ShippingAddressDto = mapper.map(addressEntity, classOf[ShippingAddressDto])
    val customerOpt: Optional[CustomerEntity] = Optional.ofNullable(addressEntity.getCustomer)
    if (customerOpt.isPresent) returnValue.setCustomerId(customerOpt.get.getCustomerId)
    returnValue
  }

  def shippingAddressDtoToEntity(addressDto: ShippingAddressDto): ShippingAddressEntity = {
    val returnValue: ShippingAddressEntity = mapper.map(addressDto, classOf[ShippingAddressEntity])
    val customerIdOpt: Optional[Integer] = Optional.ofNullable(addressDto.getCustomerId)
    if (customerIdOpt.isPresent) {
      val customerId: Integer = customerIdOpt.get
      val customerEntity: CustomerEntity = customerRepository.findById(customerId).get
      returnValue.setCustomer(customerEntity)
    }
    returnValue
  }

  def orderEntityToDto(orderEntity: OrderEntity): OrderDto = {
    val returnValue: OrderDto = mapper.map(orderEntity, classOf[OrderDto])
    val addressEntity: Optional[OrderAddressEntity] = Optional.ofNullable(orderEntity.getAddress)
    if (addressEntity.isPresent) returnValue.setAddressId(addressEntity.get.getOrderAddressId)
    val customerEntity: Optional[CustomerEntity] = Optional.ofNullable(orderEntity.getCustomer)
    if (customerEntity.isPresent) returnValue.setCustomerId(customerEntity.get.getCustomerId)
    val cartEntity: Optional[CartEntity] = Optional.ofNullable(orderEntity.getCart)
    if (cartEntity.isPresent) returnValue.setCartId(cartEntity.get.getCartId)
    val orderedItemsIds: util.List[Integer] = new util.ArrayList[Integer]
    val orderedItems: util.List[OrderItemEntity] = orderEntity.getOrderedItems
    orderedItems.forEach((itemEntity: OrderItemEntity) => {
      def foo(itemEntity: OrderItemEntity) = {
        val itemId: Integer = itemEntity.getOrderItemId
        orderedItemsIds.add(itemId)
      }

      foo(itemEntity)
    })
    val createdAtOpt: Optional[Timestamp] = Optional.ofNullable(orderEntity.getCreatedAt)
    if (createdAtOpt.isPresent) {
      val createdAtZoned: ZonedDateTime = createdAtOpt.get.toLocalDateTime.atZone(ZoneId.of("Europe/Belgrade"))
      val createdAtStr: String = createdAtZoned.format(formatter)
      returnValue.setCreatedAtStr(createdAtStr)
    }
    returnValue.setOrderedItemsIds(orderedItemsIds)
    returnValue
  }

  def orderDtoToEntity(orderDto: OrderDto): OrderEntity = {
    val returnValue: OrderEntity = mapper.map(orderDto, classOf[OrderEntity])
    val addressIdOpt: Optional[Integer] = Optional.ofNullable(orderDto.getAddressId)
    if (addressIdOpt.isPresent) {
      val addressId: Integer = addressIdOpt.get
      val address: OrderAddressEntity = orderAddressRepository.findById(addressId).get
      returnValue.setAddress(address)
    }
    val customerIdOpt: Optional[Integer] = Optional.ofNullable(orderDto.getCustomerId)
    if (customerIdOpt.isPresent) {
      val customerId: Integer = customerIdOpt.get
      val customerEntity: CustomerEntity = customerRepository.findById(customerId).get
      returnValue.setCustomer(customerEntity)
    }
    val cartIdOpt: Optional[Integer] = Optional.ofNullable(orderDto.getCartId)
    if (cartIdOpt.isPresent) {
      val cartId: Integer = cartIdOpt.get
      val cartEntity: CartEntity = cartRepository.findById(cartId).get
      returnValue.setCart(cartEntity)
    }
    val orderedItems: util.List[OrderItemEntity] = new util.ArrayList[OrderItemEntity]
    val orderedItemsIds: util.List[Integer] = orderDto.getOrderedItemsIds
    orderedItemsIds.forEach((itemId: Integer) => {
      def foo(itemId: Integer) = {
        val itemEntity: OrderItemEntity = orderItemRepository.findById(itemId).get
        orderedItems.add(itemEntity)
      }

      foo(itemId)
    })
    returnValue.setOrderedItems(orderedItems)
    returnValue
  }

  def userEntityToDto(userEntity: UserEntity): UserDto = {
    val returnValue: UserDto = mapper.map(userEntity, classOf[UserDto])
    returnValue.setEnabled(userEntity.getEnabled)
    val rolesOpt: Optional[util.List[RoleEntity]] = Optional.ofNullable(userEntity.getRoles)
    val rolesIds: util.List[Integer] = new util.ArrayList[Integer]
    if (!rolesOpt.isEmpty) rolesOpt.get.forEach((roleEntity: RoleEntity) => {
      def foo(roleEntity: RoleEntity) = rolesIds.add(roleEntity.getId)

      foo(roleEntity)
    })
    returnValue.setRolesIds(rolesIds)
    returnValue
  }

  def userDtoToEntity(userDto: UserDto): UserEntity = {
    val returnValue: UserEntity = mapper.map(userDto, classOf[UserEntity])
    val roles: util.List[RoleEntity] = new util.ArrayList[RoleEntity]
    val rolesIdsOpt: Optional[util.List[Integer]] = Optional.ofNullable(userDto.getRolesIds)
    if (!rolesIdsOpt.isEmpty) rolesIdsOpt.get.forEach((roleId: Integer) => {
      def foo(roleId: Integer) = {
        val role: RoleEntity = roleRepository.findById(roleId).get
        roles.add(role)
      }

      foo(roleId)
    })
    returnValue.setRoles(roles)
    returnValue
  }

  def roleEntityToDto(roleEntity: RoleEntity): RoleDto = {
    val returnValue: RoleDto = mapper.map(roleEntity, classOf[RoleDto])
    val users: util.List[UserEntity] = roleEntity.getUsers
    val userIds: util.List[Integer] = new util.ArrayList[Integer]
    users.forEach((user: UserEntity) => {
      def foo(user: UserEntity) = userIds.add(user.getId)

      foo(user)
    })
    returnValue.setUsersIds(userIds)
    returnValue
  }

  def roleDtoToEntity(roleDto: RoleDto): RoleEntity = {
    val returnValue: RoleEntity = mapper.map(roleDto, classOf[RoleEntity])
    val usersIds: util.List[Integer] = roleDto.getUsersIds
    val users: util.List[UserEntity] = new util.ArrayList[UserEntity]
    usersIds.forEach((userId: Integer) => {
      def foo(userId: Integer) = {
        val userEntity: UserEntity = userRepository.findById(userId).get
        users.add(userEntity)
      }

      foo(userId)
    })
    returnValue.setUsers(users)
    returnValue
  }

  def orderItemEntityToDto(itemEntity: OrderItemEntity): OrderItemDto = {
    val returnValue: OrderItemDto = mapper.map(itemEntity, classOf[OrderItemDto])
    val orderOpt: Optional[OrderEntity] = Optional.ofNullable(itemEntity.getOrder)
    if (orderOpt.isPresent) returnValue.setOrderId(orderOpt.get.getOrderId)
    returnValue
  }

  def orderItemDtoToEntity(itemDto: OrderItemDto): OrderItemEntity = {
    val returnValue: OrderItemEntity = mapper.map(itemDto, classOf[OrderItemEntity])
    val orderIdOpt: Optional[Integer] = Optional.ofNullable(itemDto.getOrderId)
    if (orderIdOpt.isPresent) {
      val orderId: Integer = orderIdOpt.get
      val orderEntity: OrderEntity = orderRepository.findById(orderId).get
      returnValue.setOrder(orderEntity)
    }
    returnValue
  }

  def orderAddressEntityToDto(address: OrderAddressEntity): OrderAddressDto = {
    val returnValue: OrderAddressDto = mapper.map(address, classOf[OrderAddressDto])
    val orderOpt: Optional[OrderEntity] = Optional.ofNullable(address.getOrder)
    if (orderOpt.isPresent) returnValue.setOrderId(orderOpt.get.getOrderId)
    returnValue
  }

  def orderAddressDtoToEntity(address: OrderAddressDto): OrderAddressEntity = {
    val returnValue: OrderAddressEntity = mapper.map(address, classOf[OrderAddressEntity])
    val orderIdOpt: Optional[Integer] = Optional.ofNullable(address.getOrderId)
    if (orderIdOpt.isPresent) {
      val orderId: Integer = orderIdOpt.get
      val orderEntity: OrderEntity = orderRepository.findById(orderId).get
      returnValue.setOrder(orderEntity)
    }
    returnValue
  }

  def shippingAddressToOrderAddress(address: ShippingAddressEntity): OrderAddressEntity = {
    val returnValue: OrderAddressEntity = mapper.map(address, classOf[OrderAddressEntity])
    returnValue
  }

  def cartItemToOrderItemEntity(cartItemEntity: CartItemEntity): OrderItemEntity = {
    val returnValue: OrderItemEntity = mapper.map(cartItemEntity, classOf[OrderItemEntity])
    val productOpt: Optional[ProductEntity] = Optional.ofNullable(cartItemEntity.getProduct)
    if (productOpt.isPresent) {
      returnValue.setProductName(productOpt.get.getProductName)
      returnValue.setProductPrice(productOpt.get.getProductPrice)
      returnValue.setDiscount(productOpt.get.getDiscount)
    }
    returnValue
  }
}

