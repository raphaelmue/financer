package org.financer.server.domain.repository;

import org.financer.server.domain.model.transaction.FixedTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FixedTransactionRepository extends JpaRepository<FixedTransaction, Long> {

    @Query("select t from FixedTransaction t where t.category.user.id = :userId")
    Page<FixedTransaction> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select t from FixedTransaction t where t.category.user.id = :userId and t.timeRange.endDate = null")
    Page<FixedTransaction> findAllActiveByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select t from FixedTransaction t where t.category.id = :categoryId")
    Page<FixedTransaction> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("select t from FixedTransaction t where t.category.id = :categoryId and t.timeRange.endDate = null")
    Optional<FixedTransaction> findActiveByCategoryId(@Param("categoryId") Long categoryId);
}
