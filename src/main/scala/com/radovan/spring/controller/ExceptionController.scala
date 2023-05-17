package com.radovan.spring.controller

import com.radovan.spring.exceptions.{ExistingEmailException, InsufficientStockException, InvalidCartException, InvalidUserException, SuspendedUserException}
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}

@ControllerAdvice class ExceptionController {

  @ExceptionHandler(Array(classOf[ExistingEmailException]))
  def handleExistingEmailException(): ResponseEntity[String] = ResponseEntity.internalServerError.body("Email exists already!!!")

  @ExceptionHandler(Array(classOf[InsufficientStockException]))
  def handleInsufficientStockException(): ResponseEntity[String] = ResponseEntity.internalServerError.body("Not enough stock!!!")

  @ExceptionHandler(Array(classOf[InvalidCartException]))
  def handleInvalidCartException(): ResponseEntity[String] = ResponseEntity.internalServerError.body("Invalid cart!!!")

  @ExceptionHandler(Array(classOf[InvalidUserException]))
  def handleInvalidUserException(): ResponseEntity[String] = ResponseEntity.internalServerError.body("Invalid user!!!")

  @ExceptionHandler(Array(classOf[SuspendedUserException]))
  def handleSuspendedUserException(): ResponseEntity[String] = ResponseEntity.internalServerError.body("User suspended!!!")
}

