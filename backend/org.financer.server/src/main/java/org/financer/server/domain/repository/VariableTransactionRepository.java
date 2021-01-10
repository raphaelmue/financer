package org.financer.server.domain.repository;

import org.financer.server.domain.model.transaction.VariableTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface VariableTransactionRepository extends JpaRepository<VariableTransaction, Long> {

    @Query("select t from VariableTransaction t where t.category.user.id = :userId")
    Page<VariableTransaction> findByCategoryUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select sum(p.amount.amount * p.quantity.numberOfItems) from Product p " +
            "where p.variableTransaction.category.user.id = :userId and p.variableTransaction.valueDate.date between :startDate and :endDate")
    Double getUsersBalanceByMonth(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
