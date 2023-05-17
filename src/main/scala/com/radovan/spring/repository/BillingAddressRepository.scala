package com.radovan.spring.repository

import com.radovan.spring.entity.BillingAddressEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait BillingAddressRepository extends JpaRepository[BillingAddressEntity, Integer] {

}
