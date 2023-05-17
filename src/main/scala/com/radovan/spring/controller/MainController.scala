package com.radovan.spring.controller

import com.radovan.spring.entity.UserEntity
import com.radovan.spring.exceptions.{InvalidUserException, SuspendedUserException}
import com.radovan.spring.form.RegistrationForm
import com.radovan.spring.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{GetMapping, ModelAttribute, PostMapping}

import java.security.Principal
import java.util.Optional

//noinspection ScalaUnusedSymbol
@Controller
class MainController {

  @Autowired
  private val customerService:CustomerService = null

  @GetMapping(value = Array("/"))
  def sayIndex = "index"

  @GetMapping(value = Array("/home"))
  def home = "fragments/homePage :: ajaxLoadedContent"

  @GetMapping(value = Array("/login"))
  def login = "fragments/login :: ajaxLoadedContent"

  @GetMapping(value = Array("/userRegistration"))
  def register(map: ModelMap): String = {
    val tempForm = new RegistrationForm
    map.put("tempForm", tempForm)
    "fragments/registration :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/userRegistration"))
  def storeUser(@ModelAttribute("tempForm") form: RegistrationForm): String = {
    customerService.storeCustomer(form)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/registerComplete"))
  def registrationCompl = "fragments/registration_completed :: ajaxLoadedContent"

  @GetMapping(value = Array("/registerFail"))
  def registrationFail = "fragments/registration_failed :: ajaxLoadedContent"

  @PostMapping(value = Array("/loggedout"))
  def logout: String = {
    val context = SecurityContextHolder.getContext
    context.setAuthentication(null)
    SecurityContextHolder.clearContext()
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/loginErrorPage"))
  def logError(map: ModelMap): String = {
    map.put("alert", "Invalid username or password!")
    "fragments/login :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/suspensionPage"))
  def suspensionAlert(map: ModelMap): String = {
    map.put("alert", "Account suspended!")
    "fragments/login :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/loginPassConfirm"))
  def confirmLoginPass(principal: Principal): String = {
    val authPrincipal = Optional.ofNullable(principal)
    if (!authPrincipal.isPresent) {
      val error = new Error("Invalid user")
      throw new InvalidUserException(error)
    }
    "fragments/homePage :: ajaxLoadedContent"
  }

  @PostMapping(value = Array("/suspensionChecker"))
  def checkForSuspension: String = {
    val authUser = SecurityContextHolder.getContext.getAuthentication.getPrincipal.asInstanceOf[UserEntity]
    if (authUser.getEnabled == 0.toByte) {
      val error = new Error("Account suspended!")
      throw new SuspendedUserException(error)
    }
    "fragments/homePage :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/aboutUs"))
  def aboutPage = "fragments/about :: ajaxLoadedContent"
}

