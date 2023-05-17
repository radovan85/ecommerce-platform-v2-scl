package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.BillingAddressDto
import com.radovan.spring.repository.BillingAddressRepository
import com.radovan.spring.service.BillingAddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BillingAddressServiceImpl extends BillingAddressService {

  @Autowired
  private val billingAddressRepository:BillingAddressRepository = null

  @Autowired
  private val tempConverter:TempConverter = null

  override def addBillingAddress(billingAddress: BillingAddressDto): BillingAddressDto = {
    val billingAddressEntity = tempConverter.billingAddressDtoToEntity(billingAddress)
    val storedBillingAddress = billingAddressRepository.save(billingAddressEntity)
    val returnValue = tempConverter.billingAddressEntityToDto(storedBillingAddress)
    returnValue
  }

  override def updateBillingAddress(id: Integer, billingAddress: BillingAddressDto): BillingAddressDto = {
    val tempBillingAddress = billingAddressRepository.findById(id).get
    val billingAddressEntity = tempConverter.billingAddressDtoToEntity(billingAddress)
    billingAddressEntity.setCustomer(tempBillingAddress.getCustomer)
    billingAddressEntity.setBillingAddressId(id)
    val updatedBillingAddress = billingAddressRepository.saveAndFlush(billingAddressEntity)
    val returnValue = tempConverter.billingAddressEntityToDto(updatedBillingAddress)
    returnValue
  }

  override def getBillingAddress(addressId: Integer): BillingAddressDto = {
    val addressEntity = billingAddressRepository.findById(addressId)
    var returnValue:BillingAddressDto = null
    if (addressEntity.isPresent) returnValue = tempConverter.billingAddressEntityToDto(addressEntity.get)
    returnValue
  }

  override def deleteBillingAddress(billingAddressId: Integer): Unit = {
    billingAddressRepository.deleteById(billingAddressId)
    billingAddressRepository.flush()
  }
}

