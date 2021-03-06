package com.uniovi.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;

public interface OffersRepository extends CrudRepository<Offer, Long> {

	@Modifying
	@Transactional
	@Query("UPDATE Offer SET purchased = ?1, purchaser=?3 WHERE id = ?2 ")
	void updatePurchase(Boolean resend, Long id, User user);

	@Query("SELECT r FROM Offer r WHERE (LOWER(r.title) LIKE LOWER(?1))")
	Page<Offer> searchByTitle(Pageable pageable, String seachtext);

	@Query("SELECT r FROM Offer r WHERE r.user = ?1 ORDER BY r.id ASC ")
	List<Offer> findAllByUser(User user);

	Page<Offer> findAll(Pageable pageable);

	List<Offer> findAll();
	
	@Query("SELECT r FROM Offer r WHERE r.purchaser = ?1 ORDER BY r.id ASC ")
	List<Offer> findAllByPurchaser(User user);
}
