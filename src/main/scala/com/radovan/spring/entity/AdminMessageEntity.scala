package com.radovan.spring.entity

import jakarta.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, Table}

import java.sql.Timestamp
import scala.beans.BeanProperty

@Entity
@Table(name = "messages")
@SerialVersionUID(1L)
class AdminMessageEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var adminMessageId:Integer = _

  @Column(nullable = false, length = 255)
  @BeanProperty var text:String = _

  @Column(name = "created_at", nullable = false)
  @BeanProperty var createdAt:Timestamp = _

  @ManyToOne
  @JoinColumn(name = "customer_id")
  @BeanProperty var customer:CustomerEntity = _

}

