package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.AdminMessageDto
import com.radovan.spring.entity.{AdminMessageEntity, CustomerEntity, UserEntity}
import com.radovan.spring.repository.{AdminMessageRepository, CustomerRepository}
import com.radovan.spring.service.AdminMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}
import java.util
import java.util.Optional

@Service
@Transactional
class AdminMessageServiceImpl extends AdminMessageService {

  @Autowired
  private val messageRepository: AdminMessageRepository = null

  @Autowired
  private val tempConverter: TempConverter = null

  @Autowired
  private val customerRepository: CustomerRepository = null

  override def addMessage(message: AdminMessageDto): AdminMessageDto = {
    val authUser: UserEntity = SecurityContextHolder.getContext.getAuthentication.getPrincipal.asInstanceOf[UserEntity]
    val customerEntity: CustomerEntity = customerRepository.findByUserId(authUser.getId)
    val messageEntity: AdminMessageEntity = tempConverter.messageDtoToEntity(message)
    messageEntity.setCustomer(customerEntity)
    val currentTime: ZonedDateTime = LocalDateTime.now.atZone(ZoneId.of("Europe/Belgrade"))
    val createdAt: Timestamp = new Timestamp(currentTime.toInstant.getEpochSecond * 1000L)
    messageEntity.setCreatedAt(createdAt)
    val storedMessage: AdminMessageEntity = messageRepository.save(messageEntity)
    val returnValue: AdminMessageDto = tempConverter.messageEntityToDto(storedMessage)
    returnValue
  }

  override def deleteMessage(messageId: Integer): Unit = {
    messageRepository.deleteById(messageId)
    messageRepository.flush()
  }

  override def getMessage(messageId: Integer): AdminMessageDto = {
    val messageOpt: Optional[AdminMessageEntity] = messageRepository.findById(messageId)
    var returnValue: AdminMessageDto = null
    if (messageOpt.isPresent) returnValue = tempConverter.messageEntityToDto(messageOpt.get)
    returnValue
  }

  override def listAll: util.List[AdminMessageDto] = {
    val allMessages: util.List[AdminMessageEntity] = messageRepository.findAll
    val returnValue: util.List[AdminMessageDto] = new util.ArrayList[AdminMessageDto]
    allMessages.forEach((messageEntity: AdminMessageEntity) => {
      def foo(messageEntity: AdminMessageEntity) = {
        val messageDto: AdminMessageDto = tempConverter.messageEntityToDto(messageEntity)
        returnValue.add(messageDto)
      }

      foo(messageEntity)
    })
    returnValue
  }

  override def deleteAllByCustomerId(customerId: Integer): Unit = {
    messageRepository.deleteAllByCustomerId(customerId)
    messageRepository.flush()
  }
}

