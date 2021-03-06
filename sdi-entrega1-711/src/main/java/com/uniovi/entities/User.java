package com.uniovi.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class User {
	@Id
	@GeneratedValue
	private long id;

	@Column(unique = true)
	private String email;
	private String name;
	private String lastName;
	private String role = "ROLE_USER";

	private double balance= INIT_MONEY;

	private static final double INIT_MONEY = 100;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Offer> offers= new HashSet<Offer>();

	@OneToMany(mappedBy = "purchaser")
	private Set<Offer> offersPurchased= new HashSet<Offer>();

	private String password;
	@Transient // propiedad que no se almacena e la tabla.
	private String passwordConfirm;

	public User(String email, String name, String lastName) {
		super();
		this.email = email;
		this.name = name;
		this.lastName = lastName;
	}

	public User() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setOffers(Set<Offer> bids) {
		this.offers = bids;
	}

	public Set<Offer> getOffers() {
		return offers;
	}

	public String getFullName() {
		return this.name + " " + this.lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public double getBalance() {
		return balance;
	}

	private void setBalance(double balance) {
		if (balance < 0) {
			throw new IllegalArgumentException("El saldo no puede ser negativo");
		}
		this.balance = balance;
	}

	public void incrementBalance(double money) {
		setBalance(this.balance + money);
	}

	public void decrementBalance(double money) {
		setBalance(this.balance - money);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", name=" + name + ", lastName=" + lastName + ", role=" + role
				+ ", balance=" + balance + ", bids=" + offers + ", password=" + password + "]";
	}

	public Set<Offer> getOffersPurchased() {
		return offersPurchased;
	}

	public void setOffersPurchased(Set<Offer> offersPurchased) {
		this.offersPurchased = offersPurchased;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

}
