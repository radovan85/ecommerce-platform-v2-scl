package com.radovan.spring.service

import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.form.RegistrationForm

import java.util

trait CustomerService {

  def storeCustomer(form: RegistrationForm): CustomerDto

  def getAllCustomers: util.List[CustomerDto]

  def getCustomer(id: Integer): CustomerDto

  def getCustomerByUserId(userId: Integer): CustomerDto

  def getCustomerByCartId(cartId: Integer): CustomerDto

  def updateCustomer(customerId: Integer, customer: CustomerDto): CustomerDto

  def addCustomer(customer: CustomerDto): CustomerDto

  def deleteCustomer(customerId: Integer): Unit

  def resetCustomer(customerId: Integer): Unit

}

