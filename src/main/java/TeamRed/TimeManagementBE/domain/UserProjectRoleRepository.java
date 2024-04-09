package TeamRed.TimeManagementBE.domain;

import org.springframework.data.repository.CrudRepository;

public interface UserProjectRoleRepository extends CrudRepository<UserProjectRole, Long>{

	UserProjectRole findById(ProjectRoleKey key);

}
