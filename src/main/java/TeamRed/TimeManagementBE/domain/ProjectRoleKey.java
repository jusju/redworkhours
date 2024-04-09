package TeamRed.TimeManagementBE.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProjectRoleKey implements Serializable {
	private static final long serialVersionUID = 123L;
	@Column(name="project_id")
	private long projectId;
	@Column(name="user_id")
	private long userId;
	
	public ProjectRoleKey() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ProjectRoleKey(long projectId, long userId) {
		super();
		this.projectId = projectId;
		this.userId = userId;
	}
	
	public long getProjectId() {
		return projectId;
	}
	
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(projectId, userId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		ProjectRoleKey projectRoleKey = (ProjectRoleKey) obj;
		return userId == projectRoleKey.userId && Objects.equals(projectId, projectRoleKey.projectId);
	}

	@Override
	public String toString() {
		return "ProjectRoleKey [projectId=" + projectId + ", userId=" + userId + "]";
	}
}
