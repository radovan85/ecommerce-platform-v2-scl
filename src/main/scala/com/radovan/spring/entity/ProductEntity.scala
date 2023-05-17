package com.radovan.spring.entity

import jakarta.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, Table, Transient}

import java.util
import scala.beans.BeanProperty

@Entity
@Table(name = "products")
@SerialVersionUID(1L)
class ProductEntity extends Serializable {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var productId:Integer = _

  @Column(name = "category", nullable = false, length = 40)
  @BeanProperty var productCategory:String = _

  @Column(name = "description", nullable = false, length = 90)
  @BeanProperty var productDescription:String = _

  @Column(name = "product_status", nullable = false, length = 40)
  @BeanProperty var productStatus:String = _

  @Column(name = "product_brand", nullable = false, length = 40)
  @BeanProperty var productBrand:String = _

  @Column(name = "product_model", nullable = false, length = 40)
  @BeanProperty var productModel:String = _

  @Column(name = "product_name", nullable = false, length = 40)
  @BeanProperty var productName:String = _

  @Column(name = "price", nullable = false)
  @BeanProperty var productPrice:Float = _

  @Column(name = "unit", nullable = false)
  @BeanProperty var unitStock:Integer = _

  @Column(nullable = false)
  @BeanProperty var discount:Float = _

  @Column(name = "image_url", nullable = false, length = 255)
  @BeanProperty var imageUrl:String = _

  @Transient
  @BeanProperty var categoryList:util.List[String] = _

}

