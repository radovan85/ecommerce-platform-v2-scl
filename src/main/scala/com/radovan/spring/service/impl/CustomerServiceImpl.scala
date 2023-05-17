package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.{BillingAddressDto, CustomerDto, ShippingAddressDto, UserDto}
import com.radovan.spring.entity.{BillingAddressEntity, CartEntity, CustomerEntity, RoleEntity, ShippingAddressEntity, UserEntity}
import com.radovan.spring.exceptions.{ExistingEmailException, InvalidUserException}
import com.radovan.spring.form.RegistrationForm
import com.radovan.spring.repository.{BillingAddressRepository, CartRepository, CustomerRepository, RoleRepository, ShippingAddressRepository, UserRepository}
import com.radovan.spring.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.Optional

@Service
@Transactional
class CustomerServiceImpl extends CustomerService {

  @Autowired
  private val customerRepository: CustomerRepository = null

  @Autowired
  private val tempConverter: TempConverter = null

  @Autowired
  private val cartRepository: CartRepository = null

  @Autowired
  private val billAddressRepository: BillingAddressRepository = null

  @Autowired
  private val shippAddressRepository: ShippingAddressRepository = null

  @Autowired
  private val userRepository: UserRepository = null

  @Autowired
  private val roleRepository: RoleRepository = null

  @Autowired
  private val passwordEncoder: BCryptPasswordEncoder = null

  override def getAllCustomers: util.List[CustomerDto] = {
    val allCustomers: util.List[CustomerEntity] = customerRepository.findAll
    val returnValue: util.List[CustomerDto] = new util.ArrayList[CustomerDto]
    allCustomers.forEach((customer: CustomerEntity) => {
      def foo(customer: CustomerEntity) = {
        val customerDto: CustomerDto = tempConverter.customerEntityToDto(customer)
        returnValue.add(customerDto)
      }

      foo(customer)
    })
    returnValue
  }

  override def getCustomer(id: Integer): CustomerDto = {
    val customerOpt: Optional[CustomerEntity] = customerRepository.findById(id)
    var returnValue: CustomerDto = new CustomerDto
    if (customerOpt.isPresent) returnValue = tempConverter.customerEntityToDto(customerOpt.get)
    else {
      val error: Error = new Error("Invalid Customer")
      throw new InvalidUserException(error)
    }
    returnValue
  }

  override def getCustomerByUserId(userId: Integer): CustomerDto = {
    val customerOpt: Optional[CustomerEntity] = Optional.ofNullable(customerRepository.findByUserId(userId))
    var returnValue: CustomerDto = new CustomerDto
    if (customerOpt.isPresent) returnValue = tempConverter.customerEntityToDto(customerOpt.get)
    else {
      val error: Error = new Error("Invalid Customer")
      throw new InvalidUserException(error)
    }
    returnValue
  }

  override def getCustomerByCartId(cartId: Integer): CustomerDto = {
    val customerOpt: Optional[CustomerEntity] = Optional.ofNullable(customerRepository.findByCartId(cartId))
    var returnValue: CustomerDto = new CustomerDto
    if (customerOpt.isPresent) returnValue = tempConverter.customerEntityToDto(customerOpt.get)
    else {
      val error: Error = new Error("Invalid Customer")
      throw new InvalidUserException(error)
    }
    returnValue
  }

  override def updateCustomer(customerId: Integer, customer: CustomerDto): CustomerDto = {
    val customerOpt: Optional[CustomerEntity] = customerRepository.findById(customerId)
    var returnValue: CustomerDto = null
    if (customerOpt.isPresent) {
      customer.setCustomerId(customerId)
      val customerEntity: CustomerEntity = tempConverter.customerDtoToEntity(customer)
      val updatedCustomer: CustomerEntity = customerRepository.saveAndFlush(customerEntity)
      returnValue = tempConverter.customerEntityToDto(updatedCustomer)
    }
    returnValue
  }

  override def storeCustomer(form: RegistrationForm): CustomerDto = {
    val userDto: UserDto = form.getUser
    val testUser: Optional[UserEntity] = Optional.ofNullable(userRepository.findByEmail(userDto.getEmail))
    if (testUser.isPresent) {
      val error: Error = new Error("Email exists")
      throw new ExistingEmailException(error)
    }
    val role: RoleEntity = roleRepository.findByRole("ROLE_USER")
    userDto.setPassword(passwordEncoder.encode(userDto.getPassword))
    userDto.setEnabled(1.toByte)
    val roles: util.List[RoleEntity] = new util.ArrayList[RoleEntity]
    roles.add(role)
    val userEntity: UserEntity = tempConverter.userDtoToEntity(userDto)
    userEntity.setRoles(roles)
    userEntity.setEnabled(1.toByte)
    val storedUser: UserEntity = userRepository.save(userEntity)
    val users: util.List[UserEntity] = new util.ArrayList[UserEntity]
    users.add(storedUser)
    role.setUsers(users)
    roleRepository.saveAndFlush(role)
    val billAddressDto: BillingAddressDto = form.getBillingAddress
    val billAddressEntity: BillingAddressEntity = tempConverter.billingAddressDtoToEntity(billAddressDto)
    val storedBillAddress: BillingAddressEntity = billAddressRepository.save(billAddressEntity)
    val shippAddressDto: ShippingAddressDto = form.getShippingAddress
    val shippAddressEntity: ShippingAddressEntity = tempConverter.shippingAddressDtoToEntity(shippAddressDto)
    val storedShippAddress: ShippingAddressEntity = shippAddressRepository.save(shippAddressEntity)
    val cartEntity: CartEntity = new CartEntity
    cartEntity.setCartPrice(0f)
    val storedCart: CartEntity = cartRepository.save(cartEntity)
    val customerDto: CustomerDto = form.getCustomer
    customerDto.setUserId(storedUser.getId)
    customerDto.setCartId(storedCart.getCartId)
    customerDto.setBillingAddressId(storedBillAddress.getBillingAddressId)
    customerDto.setShippingAddressId(storedShippAddress.getShippingAddressId)
    val customerEntity: CustomerEntity = tempConverter.customerDtoToEntity(customerDto)
    val storedCustomer: CustomerEntity = customerRepository.save(customerEntity)
    storedCart.setCustomer(storedCustomer)
    cartRepository.saveAndFlush(storedCart)
    storedBillAddress.setCustomer(storedCustomer)
    billAddressRepository.saveAndFlush(storedBillAddress)
    storedShippAddress.setCustomer(storedCustomer)
    shippAddressRepository.saveAndFlush(storedShippAddress)
    val returnValue: CustomerDto = tempConverter.customerEntityToDto(storedCustomer)
    returnValue
  }

  override def addCustomer(customer: CustomerDto): CustomerDto = {
    val customerEntity: CustomerEntity = tempConverter.customerDtoToEntity(customer)
    val storedCustomer: CustomerEntity = customerRepository.save(customerEntity)
    val returnValue: CustomerDto = tempConverter.customerEntityToDto(storedCustomer)
    returnValue
  }

  override def deleteCustomer(customerId: Integer): Unit = {
    customerRepository.deleteById(customerId)
    customerRepository.flush()
  }

  override def resetCustomer(customerId: Integer): Unit = {
    val customerOptional: Optional[CustomerEntity] = customerRepository.findById(customerId)
    if (customerOptional.isPresent) {
      val cartOptional: Optional[CartEntity] = Optional.ofNullable(customerOptional.get.getCart)
      if (cartOptional.isPresent) {
        val cartEntity: CartEntity = cartOptional.get
        cartEntity.setCustomer(null)
        cartRepository.saveAndFlush(cartEntity)
        val customerEntity: CustomerEntity = customerOptional.get
        customerEntity.setCart(null)
        customerEntity.setBillingAddress(null)
        customerEntity.setShippingAddress(null)
        customerEntity.setUser(null)
        customerRepository.saveAndFlush(customerEntity)
      }
    }
  }
}

