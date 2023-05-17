package com.radovan.spring.entity

import jakarta.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, OneToOne, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "billing_addresses")
@SerialVersionUID(1L)
class BillingAddressEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var billingAddressId:Integer = _

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

  @OneToOne(mappedBy = "billingAddress")
  @BeanProperty var customer:CustomerEntity = _

}

