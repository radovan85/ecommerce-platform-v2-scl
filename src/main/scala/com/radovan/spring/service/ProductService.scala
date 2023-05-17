package com.radovan.spring.service

import com.radovan.spring.dto.ProductDto

import java.util

trait ProductService {

  def listAll: util.List[ProductDto]

  def getProduct(id: Integer): ProductDto

  def deleteProduct(id: Integer): Unit

  def addProduct(product: ProductDto): ProductDto

  def listAllByKeyword(keyword: String): util.List[ProductDto]

}
