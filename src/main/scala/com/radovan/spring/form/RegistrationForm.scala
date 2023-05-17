package com.radovan.spring.form

import com.radovan.spring.dto.{BillingAddressDto, CustomerDto, ShippingAddressDto, UserDto}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class RegistrationForm extends Serializable {

  @BeanProperty var user:UserDto = _
  @BeanProperty var billingAddress:BillingAddressDto = _
  @BeanProperty var shippingAddress:ShippingAddressDto = _
  @BeanProperty var customer:CustomerDto = _

}

