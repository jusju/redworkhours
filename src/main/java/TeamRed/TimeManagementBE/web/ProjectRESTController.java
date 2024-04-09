 package TeamRed.TimeManagementBE.web;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

//import com.fasterxml.jackson.annotation.JsonView;

import TeamRed.TimeManagementBE.domain.ProjectRepository;
import TeamRed.TimeManagementBE.domain.ProjectRoleKey;
import TeamRed.TimeManagementBE.domain.Role;
import TeamRed.TimeManagementBE.domain.UserProjectRole;
import TeamRed.TimeManagementBE.domain.UserProjectRoleRepository;
import jakarta.validation.Valid;
import TeamRed.TimeManagementBE.domain.AppUser;
import TeamRed.TimeManagementBE.domain.AppUserRepository;
import TeamRed.TimeManagementBE.domain.Project;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@CrossOrigin
@RestController
@RequestMapping("/projects")
public class ProjectRESTController {

    @Autowired
	private ProjectRepository repository;
    
    @Autowired
    private AppUserRepository userRepository;
    
    @Autowired
    private UserProjectRoleRepository roleRepository;
    
    private AppUser getUserDetails() {    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username);
    }
    
    private Role getUserRole(Long id) {
		ProjectRoleKey key = new ProjectRoleKey(id, getUserDetails().getId());
		UserProjectRole userProjectRole = roleRepository.findById(key);
		if (userProjectRole != null) {
			return userProjectRole.getRole();
		} else {
			return null;
		}
    }

    //Kaikkien tietyn käyttäjän projektien haku
	@GetMapping
	//@JsonView(Project.ProjectOverview.class)
	public ResponseEntity<?> getProjects() {
		try {
			AppUser user = getUserDetails();
			Set<UserProjectRole> projects = user.getRoles();
			if (((Set<UserProjectRole>) projects).isEmpty()) {
				return new ResponseEntity<>("Projekteja ei löytynyt", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(projects, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Palauttaa projektin haetulla id:llä, jos kyseessä käyttäjän oma projekti
	@GetMapping("/{projectId}")
	//@JsonView(Project.DetailedProjectView.class)
	public ResponseEntity<?> getProjectById(@PathVariable("projectId") Long id) {
		try {
			if (getUserRole(id) != null) {
				return new ResponseEntity<>(repository.findById(id), HttpStatus.OK);
			}
			return new ResponseEntity<>("Annetulla id:llä ei löytynyt projektia", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
			
	//Uuden projektin lisääminen
	@PostMapping
	public ResponseEntity<?> addProject(@Valid @RequestBody Project project, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>("Invalid data", HttpStatus.UNPROCESSABLE_ENTITY);
	    }
		try {
			Project newProject = new Project(project.getTitle());
			repository.save(newProject);
			AppUser user = getUserDetails();
			UserProjectRole role = new UserProjectRole();
			role.setRole(Role.OWNER);
			role.setAppUser(user);
			role.setProject(newProject);
			roleRepository.save(role);		
			return new ResponseEntity<>(newProject, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Projektin muokkaus
	@PutMapping("/{projectId}")
	public ResponseEntity<?> editProject(@Valid @RequestBody Project updatedProject, @PathVariable("projectId") Long id, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>("Invalid data", HttpStatus.UNPROCESSABLE_ENTITY);
	    }
		try {
			Optional<Project> toBeEdited = repository.findById(id);
			if (!toBeEdited.isEmpty() && getUserRole(id).equals(Role.OWNER)) {
				Project project = toBeEdited.get();
				project.setTitle(updatedProject.getTitle());
				repository.save(project);
				Set<UserProjectRole> roles = updatedProject.getRoles();
				for (UserProjectRole role : roles) {
					AppUser user = role.getAppUser();
					ProjectRoleKey key = new ProjectRoleKey(id, user.getId());
					UserProjectRole userProjectRole = roleRepository.findById(key);
					if (userProjectRole == null) {
						UserProjectRole newProjectRole = new UserProjectRole();
						newProjectRole.setRole(role.getRole());
						newProjectRole.setAppUser(user);
						newProjectRole.setProject(project);
						roleRepository.save(newProjectRole);
					} else if (role.getRole() == null) {
						roleRepository.delete(userProjectRole);
					} else {
						userProjectRole.setRole(role.getRole());
						roleRepository.save(userProjectRole);
					}
				}
				return new ResponseEntity<>(project, HttpStatus.OK);
			}
			return new ResponseEntity<>("Annetulla id:llä ei löytynyt projektia", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Projektin poisto
	@DeleteMapping("/{projectId}")
	public ResponseEntity<String> removeProject(@PathVariable("projectId") Long id) {
		try {
			if (getUserRole(id).equals(Role.OWNER)) {
				repository.deleteById(id);
				return new ResponseEntity<>("Projekti poistettu onnistuneesti", HttpStatus.OK);
			}
			return new ResponseEntity<>("Annetulla id:llä ei löytynyt projektia", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}