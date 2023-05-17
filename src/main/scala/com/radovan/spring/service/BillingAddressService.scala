package com.radovan.spring.service

import com.radovan.spring.dto.BillingAddressDto

trait BillingAddressService {

  def addBillingAddress(billingAddress: BillingAddressDto): BillingAddressDto

  def updateBillingAddress(id: Integer, billingAddress: BillingAddressDto): BillingAddressDto

  def getBillingAddress(addressId: Integer): BillingAddressDto

  def deleteBillingAddress(billingAddressId: Integer): Unit
}