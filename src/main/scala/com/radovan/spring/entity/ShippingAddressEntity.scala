package com.radovan.spring.entity

import jakarta.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, OneToOne, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "shipping_addresses")
@SerialVersionUID(1L)
class ShippingAddressEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var shippingAddressId:Integer = _

  @Column(nullable = false, length = 75)
  @BeanProperty var address:String = _

  @Column(nullable = false, length = 40)
  @BeanProperty var city:String = _

  @Column(nullable = false, length = 40)
  @BeanProperty var state:String = _

  @Column(name = "post_code", nullable = false, length = 10)
  @BeanProperty var postcode:String = _

  @Column(nullable = false, length = 40)
  @BeanProperty var country:String = _

  @OneToOne(mappedBy = "shippingAddress")
  @BeanProperty var customer:CustomerEntity = _

}

