package org.sid.service;

import java.util.List;

import javax.transaction.Transactional;

import org.sid.dao.AppRoleRepository;
import org.sid.dao.AppUserRepository;
import org.sid.entities.AppRole;
import org.sid.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
	@Autowired
	AppRoleRepository approlerepo;
	@Autowired
	AppUserRepository appuserrepo;

	@Override
	public AppUser saveUser(AppUser u) {
		u.setPassword(new BCryptPasswordEncoder().encode(u.getPassword()));
		return appuserrepo.save(u);
	}

	@Override
	public AppRole saveRole(AppRole r) {
		return approlerepo.save(r);
	}

	@Override
	public AppUser findByUsername(String username) {
		return appuserrepo.findByUsername(username);
	}

	@Override
	public void AddRoleUser(String username, String r) {
		AppUser user = appuserrepo.findByUsername(username);
		AppRole role = approlerepo.findByRole(r);
		user.getRoles().add(role);

	}

	@Override
	public List<AppUser> listUser() {
		return appuserrepo.findAll();
	}

}
