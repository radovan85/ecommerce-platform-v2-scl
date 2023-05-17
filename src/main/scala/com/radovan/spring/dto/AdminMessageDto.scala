package com.radovan.spring.dto

import java.sql.Timestamp
import scala.beans.BeanProperty

@SerialVersionUID(1L)
class AdminMessageDto extends Serializable {

  @BeanProperty var adminMessageId:Integer = _
  @BeanProperty var text:String = _
  @BeanProperty var createdAt:Timestamp = _
  @BeanProperty var createdAtStr:String = _
  @BeanProperty var customerId:Integer = _


}

