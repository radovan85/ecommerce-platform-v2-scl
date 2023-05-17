package com.radovan.spring.controller

import com.radovan.spring.dto.ProductDto
import com.radovan.spring.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestMapping, RequestParam}

import java.util

@Controller
@RequestMapping(value = Array("/products"))
class ProductController {

  @Autowired private val productService: ProductService = null

  @GetMapping(value = Array("/allProducts"))
  def getAllProducts(map: ModelMap): String = {
    val allProducts: util.List[ProductDto] = productService.listAll
    map.put("allProducts", allProducts)
    map.put("recordsPerPage", 5.asInstanceOf[Integer])
    "fragments/productList :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/getProduct/{productId}"))
  def getProductDetails(@PathVariable("productId") productId: Integer, map: ModelMap): String = {
    val currentProduct: ProductDto = productService.getProduct(productId)
    map.put("currentProduct", currentProduct)
    "fragments/productDetails :: ajaxLoadedContent"
  }

  @GetMapping(value = Array("/searchProducts"))
  def searchProducts(@RequestParam("keyword") keyword: String, map: ModelMap): String = {
    val searchResult: util.List[ProductDto] = productService.listAllByKeyword(keyword)
    map.put("searchResult", searchResult)
    map.put("recordsPerPage", 5.asInstanceOf[Integer])
    "fragments/searchList :: ajaxLoadedContent"
  }
}

