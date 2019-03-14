package com.uniovi.controllers;

import java.security.Principal;
import java.util.*;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import com.uniovi.services.OffersService;
import com.uniovi.services.UsersService;

@Controller
public class OffersController {

	@Autowired
	private OffersService offersService;

	@Autowired
	private UsersService usersService;

	@Autowired
	private HttpSession httpSession;

//	@RequestMapping("/offer/list")
//	public String getList(Model model,Pageable pageable, Principal principal,
//			@RequestParam(value = "", required = false) String searchText) {
//		String dni = principal.getName(); // DNI es el name de la autenticación
//		User user = usersService.getUserByEmail(dni);
//		Page<Bid> offers = new PageImpl<Bid>(new LinkedList<Bid>());
//		if (searchText != null && !searchText.isEmpty()) {
//		offers = offersService
//		.searchoffersByDescriptionAndNameForUser(pageable, searchText, user);
//		} else {
//		offers = offersService.getoffersForUser(pageable, user);
//		}
//		model.addAttribute("offerList", offers.getContent());
//		model.addAttribute("page", offers);
//		return "offer/list";
//	}

	@RequestMapping(value = "/offer/add", method = RequestMethod.POST)
	public String setoffer(@ModelAttribute Offer offer,Principal principal) {
		String email = principal.getName(); // DNI es el name de la autenticación
		User user = usersService.getUserByEmail(email);
		offer.setUser(user);
		offer.setDate(new Date());
		offersService.addOffer(offer);
		return "redirect:/offer/my";
	}

	@RequestMapping("/offer/details/{id}")
	public String getDetail(Model model, @PathVariable Long id) {
		model.addAttribute("offer", offersService.getOffer(id));
		return "offer/details";
	}

//	@RequestMapping("/offer/delete/{id}")
//	public String deleteoffer(@PathVariable Long id) {
//		offersService.deleteOffer(id);
//		return "redirect:/offer/list";
//	}

	@RequestMapping(value = "/offer/add")
	public String getOffer(Model model) {
		return "offer/add";
	}
	
	@RequestMapping(value = "/offer/my")
	public String getMyOffers(Model model,Principal principal) {
		String email = principal.getName(); // DNI es el name de la autenticación
		User user = usersService.getUserByEmail(email);
		
		model.addAttribute("offerList", offersService.getOffersForUser(user));
		return "offer/my";
	}
	

	@RequestMapping(value = "/offer/edit/{id}")
	public String getEdit(Model model, @PathVariable Long id) {
		model.addAttribute("offer", offersService.getOffer(id));
		return "offer/edit";
	}

//	@RequestMapping(value = "/offer/edit/{id}", method = RequestMethod.POST)
//	public String setEdit(Model model, @PathVariable Long id, @ModelAttribute Offer offer) {
//		Offer original = offersService.getOffer(id);
//		// modificar solo score y description
//		original.setPrice(offer.getPrice());
//		original.setDescription(offer.getDescription());
//		offersService.addoffer(original);
//		return "redirect:/offer/details/" + id;
//	}

//	@RequestMapping("/offer/list/update")
//	public String updateList(Model model, Pageable pageable, Principal principal){
//	String dni = principal.getName(); // DNI es el name de la autenticación
//	User user = usersService.getUserByEmail(dni);
//	Page<Bid> offers = offersService.getoffersForUser(pageable, user);
//	model.addAttribute("offerList", offers.getContent() );
//	return "offer/list :: tableoffers";
//	}

//	@RequestMapping(value = "/offer/{id}/resend", method = RequestMethod.GET)
//	public String setResendTrue(Model model, @PathVariable Long id) {
//		offersService.setofferResend(true, id);
//		return "redirect:/offer/list";
//	}
//
//	@RequestMapping(value = "/offer/{id}/noresend", method = RequestMethod.GET)
//	public String setResendFalse(Model model, @PathVariable Long id) {
//		offersService.setofferResend(false, id);
//		return "redirect:/offer/list";
//	}

}