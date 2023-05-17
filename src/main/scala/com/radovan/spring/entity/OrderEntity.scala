package com.radovan.spring.entity

import jakarta.persistence.{CascadeType, Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, OneToMany, OneToOne, Table}
import org.hibernate.annotations.{Fetch, FetchMode}

import java.util
import java.sql.Timestamp
import scala.beans.BeanProperty

@Entity
@Table(name = "orders")
@SerialVersionUID(1L)
class OrderEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var orderId:Integer = _

  @ManyToOne
  @JoinColumn(name = "cart_id")
  @BeanProperty var cart:CartEntity = _

  @ManyToOne
  @JoinColumn(name = "customer_id")
  @BeanProperty var customer:CustomerEntity = _

  @OneToMany(mappedBy = "order", cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  @BeanProperty var orderedItems:util.List[OrderItemEntity] = _

  @OneToOne(cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER)
  @JoinColumn(name = "address_id")
  @BeanProperty var address:OrderAddressEntity = _

  @Column(name = "created_at", nullable = false)
  @BeanProperty var createdAt:Timestamp = _

}

