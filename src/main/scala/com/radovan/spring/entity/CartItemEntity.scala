package com.radovan.spring.entity

import jakarta.persistence.{CascadeType, Column, Entity, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "cart_items")
@SerialVersionUID(1L)
class CartItemEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var cartItemId:Integer = _

  @BeanProperty var quantity:Integer = _

  @BeanProperty var price:Float = _

  @ManyToOne(cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "product_id")
  @BeanProperty var product:ProductEntity = _

  @ManyToOne
  @JoinColumn(name = "cart_id")
  @BeanProperty var cart:CartEntity = _

}

