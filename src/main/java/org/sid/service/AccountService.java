package org.sid.service;

import java.util.List;

import org.sid.entities.AppRole;
import org.sid.entities.AppUser;

public interface AccountService {
	public AppUser saveUser(AppUser u);

	public AppRole saveRole(AppRole r);

	public AppUser findByUsername(String username);

	public void AddRoleUser(String username, String role);

	public List<AppUser> listUser();

}
