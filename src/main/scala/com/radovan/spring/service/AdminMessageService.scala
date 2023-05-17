package com.radovan.spring.service

import com.radovan.spring.dto.AdminMessageDto

import java.util

trait AdminMessageService {
  
  def addMessage(review: AdminMessageDto): AdminMessageDto

  def getMessage(messageId: Integer): AdminMessageDto

  def listAll: util.List[AdminMessageDto]

  def deleteMessage(messageId: Integer): Unit

  def deleteAllByCustomerId(customerId: Integer): Unit
}
