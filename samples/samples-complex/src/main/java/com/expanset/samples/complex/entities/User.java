package com.expanset.samples.complex.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({
	@NamedQuery(name="User.findByLogin", query="select u from User u where u.login = :login"), 
	@NamedQuery(name="User.isExists", query="select count(u.id) from User u where u.login = :login") 
})
public class User {

	private long id;
	
	private Date created;
	
	private String login;
	
	private String password;
	
	private Date stockQuoteDate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name="stock_quotes_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getStockQuoteDate() {
		return stockQuoteDate;
	}

	public void setStockQuoteDate(Date stockQuoteDate) {
		this.stockQuoteDate = stockQuoteDate;
	}
}
