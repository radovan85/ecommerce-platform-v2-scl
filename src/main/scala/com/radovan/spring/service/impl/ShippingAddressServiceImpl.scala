package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.repository.ShippingAddressRepository
import com.radovan.spring.service.ShippingAddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ShippingAddressServiceImpl extends ShippingAddressService {

  @Autowired
  private val addressRepository:ShippingAddressRepository = null

  @Autowired
  private val tempConverter:TempConverter = null

  override def addShippingAddress(shippingAddress: ShippingAddressDto): ShippingAddressDto = {
    val addressEntity = tempConverter.shippingAddressDtoToEntity(shippingAddress)
    val storedAddress = addressRepository.save(addressEntity)
    val returnValue = tempConverter.shippingAddressEntityToDto(storedAddress)
    returnValue
  }

  override def updateShippingAddress(id: Integer, shippingAddress: ShippingAddressDto): ShippingAddressDto = {
    val tempAddress = addressRepository.findById(id).get
    val addressEntity = tempConverter.shippingAddressDtoToEntity(shippingAddress)
    addressEntity.setShippingAddressId(id)
    addressEntity.setCustomer(tempAddress.getCustomer)
    val updatedAddress = addressRepository.saveAndFlush(addressEntity)
    val returnValue = tempConverter.shippingAddressEntityToDto(updatedAddress)
    returnValue
  }

  override def getShippingAddress(addressId: Integer): ShippingAddressDto = {
    val addressEntity = addressRepository.findById(addressId)
    var returnValue:ShippingAddressDto = null
    if (addressEntity.isPresent) returnValue = tempConverter.shippingAddressEntityToDto(addressEntity.get)
    returnValue
  }

  override def deleteShippingAddress(shippingAddressId: Integer): Unit = {
    addressRepository.deleteById(shippingAddressId)
    addressRepository.flush()
  }
}

