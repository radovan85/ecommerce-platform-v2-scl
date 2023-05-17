package com.radovan.spring.controller

import com.radovan.spring.dto.AdminMessageDto
import com.radovan.spring.service.AdminMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{GetMapping, ModelAttribute, PostMapping, RequestMapping}

@Controller
@RequestMapping(value = Array("/message"))
class MessageController {

  @Autowired
  private val messageService:AdminMessageService = null

  @GetMapping(value = Array("/sendMessage"))
  def renderMessageForm(map: ModelMap): String = {
    val message = new AdminMessageDto
    map.put("message", message)
    "fragments/messageForm :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/sendMessage"))
  def sendMessage(@ModelAttribute("message") message: AdminMessageDto): String = {
    messageService.addMessage(message)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/messageSent"))
  def messageResult = "fragments/messageSent :: ajaxLoadedContent"
}

