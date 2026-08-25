package com.app.gighub
.models;

import jakarta.persistence.*;

@Entity
@Table(name = "feedback")  // ✅ Prevent SQL conflicts
public class Feedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "bid_id", nullable = false)  // ✅ Feedback should always be linked to a Bid
	private Bid bid;
	
	@Column(nullable = false)
	private Integer clientRate;

	@Column(length = 1000)  // ✅ Limit text size to avoid database performance issues
	private String clientFeedback;

	@Column(nullable = false)
	private Integer contractorRate;

	@Column(length = 1000)
	private String contractorFeedback;

	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Bid getBid() {
		return bid;
	}

	public void setBid(Bid bid) {
		this.bid = bid;
	}

	public Integer getClientRate() {
		return clientRate;
	}

	public void setClientRate(Integer clientRate) {
		this.clientRate = clientRate;
	}

	public String getClientFeedback() {
		return clientFeedback;
	}

	public void setClientFeedback(String clientFeedback) {
		this.clientFeedback = clientFeedback;
	}

	public Integer getContractorRate() {
		return contractorRate;
	}

	public void setContractorRate(Integer contractorRate) {
		this.contractorRate = contractorRate;
	}

	public String getContractorFeedback() {
		return contractorFeedback;
	}

	public void setContractorFeedback(String contractorFeedback) {
		this.contractorFeedback = contractorFeedback;
	}

	@Override
	public String toString() {  // ✅ Properly override toString()
		return "Feedback{" +
				"id=" + id +
				", clientRate=" + clientRate +
				", contractorRate=" + contractorRate +
				", clientFeedback='" + clientFeedback + '\'' +
				", contractorFeedback='" + contractorFeedback + '\'' +
				'}';
	}
}
