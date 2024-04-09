package TeamRed.TimeManagementBE;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import TeamRed.TimeManagementBE.domain.Project;
import TeamRed.TimeManagementBE.domain.ProjectRepository;
import TeamRed.TimeManagementBE.domain.Role;
import TeamRed.TimeManagementBE.domain.UserProjectRole;
import TeamRed.TimeManagementBE.domain.UserProjectRoleRepository;
import TeamRed.TimeManagementBE.domain.AppUser;
import TeamRed.TimeManagementBE.domain.AppUserRepository;
import TeamRed.TimeManagementBE.domain.Entry;
import TeamRed.TimeManagementBE.domain.EntryRepository;

@SpringBootApplication
public class TimeManagementBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeManagementBeApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(ProjectRepository projectRepo, EntryRepository entryRepo,
			AppUserRepository appUserRepo, UserProjectRoleRepository roleRepo) {
		return (args) -> {
			//Lisätään kaksi käyttäjää:
			// 1) password: AppUser1
			AppUser testUser = new AppUser("Ensimmäinen", "Käyttäjä", "new_user1", "$2a$12$JoEvKPN77YLvGw/vqLpKeO4A.CW/1LbSweTpmwfUNfQCWC62DL/4q");
			appUserRepo.save(testUser);
			// 2) password: AppUser2
			AppUser testUser2 = new AppUser("Toinen", "Käyttäjä", "new_user2", "$2a$12$faaHwhorn90N15gUoeXLxeqeP7Iv3Xn1Z9BnoPnTajKy.KEA2esm.");
			appUserRepo.save(testUser2);
			
			//Lisätään kolme projektia:
			Project testiprojekti = new Project("Testproject 1");
			projectRepo.save(testiprojekti);
			Project testiprojekti2 = projectRepo.save(new Project("Testproject 2"));
			
			//Lisätään yhteydet projektien ja usereiden välille: 
			// 1) Lisätään testUser owneriksi projektiin testiprojekti
			UserProjectRole role = new UserProjectRole();
			role.setRole(Role.OWNER);
			role.setAppUser(testUser);
			role.setProject(testiprojekti);
			roleRepo.save(role);
			testiprojekti.getRoles().add(role);
			testUser.getRoles().add(role);
			// 2) Lisätään testUser2 useriksi projektiin testiprojekti
			UserProjectRole role2 = new UserProjectRole();
			role2.setRole(Role.USER);
			role2.setAppUser(testUser2);
			role2.setProject(testiprojekti);
			roleRepo.save(role2);
			testiprojekti.getRoles().add(role2);
			testUser2.getRoles().add(role2);
			// 3) Lisätään testUser2 owneriksi projektiin testiprojekti2
			UserProjectRole role3 = new UserProjectRole();
			role3.setRole(Role.OWNER);
			role3.setAppUser(testUser2);
			role3.setProject(testiprojekti2);
			roleRepo.save(role3);
			testiprojekti.getRoles().add(role3);
			testUser.getRoles().add(role3);
			
			//Lisätään ensimmäiseen testiprojektiin kaksi työaikakirjausta:
			entryRepo.save(new Entry("Test entry 1", LocalDate.parse("2022-02-02"),
					LocalTime.parse("10:05"), LocalTime.parse("15:15"), testiprojekti));
			entryRepo.save(new Entry("Test entry 2", LocalDate.parse("2022-02-01"),
					LocalTime.parse("08:00"), LocalTime.parse("13:35"), testiprojekti));
		};
	}

}
