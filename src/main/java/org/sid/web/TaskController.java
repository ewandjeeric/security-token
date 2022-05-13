package org.sid.web;

import java.util.List;

import org.sid.dao.TaskRepository;
import org.sid.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
	@Autowired
	TaskRepository taskrepo;

	@GetMapping("/tasks")
	@PostAuthorize("hasAuthority('USER')")
	public List<Task> alltask() {
		return taskrepo.findAll();
	}

	@PostMapping("/tasks")
	@PostAuthorize("hasAuthority('ADMIN')")
	public Task savetask(@RequestBody Task t) {
		return taskrepo.save(t);
	}

}
