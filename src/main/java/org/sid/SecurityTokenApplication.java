package org.sid;

import org.sid.dao.AppRoleRepository;
import org.sid.dao.AppUserRepository;
import org.sid.dao.TaskRepository;
import org.sid.entities.AppRole;
import org.sid.entities.AppUser;
import org.sid.entities.Task;
import org.sid.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityTokenApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityTokenApplication.class, args);
	}

	@Bean
	CommandLineRunner start(AppUserRepository appuserrepo, AppRoleRepository approlerepo, AccountService accs,
			TaskRepository taskrepo) {
		return arg -> {
			AppRole role1 = new AppRole(null, "USER");
			AppRole role2 = new AppRole(null, "ADMIN");
			AppRole role3 = new AppRole(null, "CUSTOMER");
			AppUser u1 = new AppUser(null, "admin", "admin");
			AppUser u2 = new AppUser(null, "user", "user");
			AppUser u3 = new AppUser(null, "customer", "custumer");
			accs.saveUser(u1);
			accs.saveUser(u2);
			accs.saveUser(u3);
			accs.saveRole(role1);
			accs.saveRole(role2);
			accs.saveRole(role3);
			accs.AddRoleUser("admin", "ADMIN");
			accs.AddRoleUser("admin", "USER");
			accs.AddRoleUser("admin", "CUSTOMER");
			accs.AddRoleUser("user", "USER");
			accs.AddRoleUser("customer", "CUSTOMER");
			taskrepo.save(new Task(null, "Odinateur", 180000, 125, true));
			taskrepo.save(new Task(null, "Telephone", 150255, 500, true));
			taskrepo.save(new Task(null, "Voiture", 250000, 10, false));

		};
	}

}
