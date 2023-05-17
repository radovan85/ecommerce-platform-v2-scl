package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ProductDto
import com.radovan.spring.entity.{CartEntity, CartItemEntity, ProductEntity}
import com.radovan.spring.repository.{CartItemRepository, CartRepository, ProductRepository}
import com.radovan.spring.service.{CartService, ProductService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.Optional

@Service
@Transactional
class ProductServiceImpl extends ProductService {

  @Autowired
  private val productRepository: ProductRepository = null

  @Autowired
  private val tempConverter: TempConverter = null

  @Autowired
  private val cartItemRepository: CartItemRepository = null

  @Autowired
  private val cartRepository: CartRepository = null

  @Autowired
  private val cartService: CartService = null

  override def listAll: util.List[ProductDto] = {
    val allProducts: util.List[ProductEntity] = productRepository.findAll
    val returnValue: util.List[ProductDto] = new util.ArrayList[ProductDto]
    allProducts.forEach((productEntity: ProductEntity) => {
      def foo(productEntity: ProductEntity) = {
        val productDto: ProductDto = tempConverter.productEntityToDto(productEntity)
        returnValue.add(productDto)
      }

      foo(productEntity)
    })
    returnValue
  }

  override def getProduct(id: Integer): ProductDto = {
    val productOpt: Optional[ProductEntity] = productRepository.findById(id)
    var returnValue: ProductDto = null
    if (productOpt.isPresent) returnValue = tempConverter.productEntityToDto(productOpt.get)
    returnValue
  }

  override def deleteProduct(id: Integer): Unit = {
    productRepository.deleteById(id)
    productRepository.flush()
  }

  override def addProduct(product: ProductDto): ProductDto = {
    val productId: Optional[Integer] = Optional.ofNullable(product.getProductId)
    val productEntity: ProductEntity = tempConverter.productDtoToEntity(product)
    val storedProduct: ProductEntity = productRepository.save(productEntity)
    val returnValue: ProductDto = tempConverter.productEntityToDto(storedProduct)
    if (productId.isPresent) {
      val allCartItems: util.List[CartItemEntity] = cartItemRepository.findAllByProductId(productId.get)
      allCartItems.forEach((itemEntity: CartItemEntity) => {
        def foo(itemEntity: CartItemEntity) = {
          var price: Float = returnValue.getProductPrice
          price = (price - ((price / 100) * returnValue.getDiscount)) * itemEntity.getQuantity
          itemEntity.setPrice(price)
          cartItemRepository.saveAndFlush(itemEntity)
        }

        foo(itemEntity)
      })
      val allCarts: util.List[CartEntity] = cartRepository.findAll
      allCarts.forEach((cartEntity: CartEntity) => {
        def foo(cartEntity: CartEntity): Unit = {
          cartService.refreshCartState(cartEntity.getCartId)
        }

        foo(cartEntity)
      })
    }
    returnValue
  }

  override def listAllByKeyword(keyword: String): util.List[ProductDto] = {
    val listResult: util.List[ProductEntity] = productRepository.findAllByKeyword(keyword)
    val returnValue: util.List[ProductDto] = new util.ArrayList[ProductDto]
    listResult.forEach((productEntity: ProductEntity) => {
      def foo(productEntity: ProductEntity) = {
        val productDto: ProductDto = tempConverter.productEntityToDto(productEntity)
        returnValue.add(productDto)
      }

      foo(productEntity)
    })
    returnValue
  }
}

