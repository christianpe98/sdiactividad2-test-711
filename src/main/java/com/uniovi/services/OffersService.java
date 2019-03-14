package com.uniovi.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import com.uniovi.repositories.OffersRepository;

@Service
public class OffersService {

	@Autowired
	private OffersRepository offersRepository;

	@Autowired
	private HttpSession httpSession;

	public Offer getOffer(Long id) {
		Set<Offer> consultedList = (Set<Offer>) httpSession.getAttribute("consultedList");
		if (consultedList == null) {
			consultedList = new HashSet<Offer>();
		}
		Offer offerObtained = offersRepository.findById(id).get();
		consultedList.add(offerObtained);
		httpSession.setAttribute("consultedList", consultedList);
		return offerObtained;
	}

	public void addOffer(Offer offer) {
		// Si en Id es null le asignamos el ultimo + 1 de la lista
		offersRepository.save(offer);
	}

	public void deleteOffer(Long id) {
		offersRepository.deleteById(id);
	}

//	public void setMarkResend(boolean revised, Long id) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		String dni = auth.getName();
//		Bid mark = marksRepository.findById(id).get();
//		if (mark.getUser().getEmail().equals(dni)) {
//			marksRepository.updateResend(revised, id);
//		}
//
//	}

	public List<Offer> getOffersForUser(User user) {
		return offersRepository.findAllByUser(user);
	}
//
//	public Page<Bid> searchMarksByDescriptionAndNameForUser(Pageable pageable, String searchText, User user) {
//		Page<Bid> marks = new PageImpl<Bid>(new LinkedList<Bid>());
//		searchText = "%" + searchText + "%";
//		if (user.getRole().equals("ROLE_STUDENT")) {
//			marks = marksRepository.searchByDescriptionNameAndUser(pageable, searchText, user);
//		}
//		if (user.getRole().equals("ROLE_PROFESSOR")) {
//			marks = marksRepository.searchByDescriptionAndName(pageable, searchText);
//		}
//		return marks;
//	}
//
//	public Page<Bid> getMarks(Pageable pageable) {
//		Page<Bid> marks = marksRepository.findAll(pageable);
//		return marks;
//	}

}