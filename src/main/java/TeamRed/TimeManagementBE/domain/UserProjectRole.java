package TeamRed.TimeManagementBE.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class UserProjectRole {
	@EmbeddedId
	@JsonIgnore
	private ProjectRoleKey id = new ProjectRoleKey();
	
	@ManyToOne
	@MapsId("projectId")
	@JoinColumn(name = "project_id")
	//@JsonIgnoreProperties({"roles"})
	private Project project;
	
	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	@JsonIgnoreProperties({"password_hash", "roles"})
	private AppUser appUser;
	
	private Role role;

	public UserProjectRole() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserProjectRole(ProjectRoleKey id, Project project, AppUser appUser, Role role) {
		super();
		this.id = id;
		this.project = project;
		this.appUser = appUser;
		this.role = role;
	}

	public ProjectRoleKey getId() {
		return id;
	}

	public void setId(ProjectRoleKey id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserProjectRole [id=" + id + ", role=" + role + "]";
	}

}
