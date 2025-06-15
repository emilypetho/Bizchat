package com.pethoemilia.client.entity;

import com.pethoemilia.client.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {

	public User() {
	}

	public User(String email, String password, String name, String address, String tel, Company company, Role role, String lang) {
		this.email = email;
		this.name = name;
		this.address = address;
		this.password = password;
		this.phone_number = tel;
		this.company = company;
		this.role = role;
		this.lang = lang;
	}
	protected Long id;
	private String email;
	private String name;
	private String address;
	private String phone_number;
	private Company company;
	private String password;

    public User(long senderId) {
    }

    public enum Role {
		GUEST, USER, ADMIN
	}
	private Role role;
	private List<Group> groupk = new ArrayList<>();
	private String lang;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Group> getGroupk() {
		return groupk;
	}

	public void setGroupk(List<Group> groupk) {
		this.groupk = groupk;
	}

	public void sortGroupsByLastMessage() {
		if (groupk != null && !groupk.isEmpty()) {
			Collections.sort(groupk, new Comparator<Group>() {
				@Override
				public int compare(Group g1, Group g2) {
					Long timestamp1 = g1.getLastMessageTimestamp();
					Long timestamp2 = g2.getLastMessageTimestamp();

					if (timestamp1 == null && timestamp2 == null) return 0;
					if (timestamp1 == null) return 1;
					if (timestamp2 == null) return -1;

					return timestamp2.compareTo(timestamp1);
				}
			});
		}
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
}