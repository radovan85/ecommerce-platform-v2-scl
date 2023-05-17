package com.radovan.spring.dto

import java.util
import scala.beans.BeanProperty

@SerialVersionUID(1L)
class ProductDto() extends Serializable {

  @BeanProperty var productId:Integer = _
  @BeanProperty var productCategory:String = _
  @BeanProperty var productDescription:String = _
  @BeanProperty var productStatus:String = _
  @BeanProperty var productBrand:String = _
  @BeanProperty var productModel:String = _
  @BeanProperty var productName:String = _
  @BeanProperty var productPrice:Float = _
  @BeanProperty var unitStock:Integer = _
  @BeanProperty var discount:Float = _
  @BeanProperty var imageUrl:String = _
  @BeanProperty var categoryList:util.List[String] = _

  categoryList = new util.ArrayList[String]
  categoryList.add("Laptop")
  categoryList.add("Mobile")
  categoryList.add("Camera")
  categoryList.add("TV")
  categoryList.add("Refrigerator")
  categoryList.add("Tablet")
  categoryList.add("Micro Oven")
  categoryList.add("DVD Player")
  categoryList.add("Fan")
  categoryList.add("Printer")
  categoryList.add("Desktop")
  categoryList.add("Washing Machine")
  categoryList.add("ipad")
  categoryList.add("Game console")
  categoryList.add("Router")

}

