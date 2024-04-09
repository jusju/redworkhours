package TeamRed.TimeManagementBE.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import TeamRed.TimeManagementBE.domain.EntryRepository;
import TeamRed.TimeManagementBE.domain.Entry;
import TeamRed.TimeManagementBE.domain.ProjectRepository;
import jakarta.validation.Valid;
import TeamRed.TimeManagementBE.domain.Project;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin
@RestController
public class EntryRESTController {

	@Autowired
	private EntryRepository repository;

	@Autowired
	private ProjectRepository pRepository;

	// Kaikkien työaikakirjausten haku
	@GetMapping("entries")
	public ResponseEntity<?> getEntries() {
		try {
			Iterable<Entry> entries = repository.findAll();
			if (((List<Entry>) entries).isEmpty()) {
				return new ResponseEntity<>("Työaikakirjauksia ei löytynyt", HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(entries, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Uuden työaikakirjauksen lisääminen
	@PostMapping("projects/{projectId}/entries")
	public ResponseEntity<?> addEntry(@Valid @RequestBody Entry entry, @PathVariable("projectId") Long id, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>("Invalid data", HttpStatus.UNPROCESSABLE_ENTITY);
	    }
		try {
			Optional<Project> project = pRepository.findById(id);
			if (project.isEmpty()) {
				return new ResponseEntity<>("Projektia ei löytynyt", HttpStatus.NOT_FOUND);
			}
			entry.setProject(project.get());
			Entry newEntry = repository.save(entry);
			return new ResponseEntity<>(newEntry, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Työaikakirjauksen muokkaus
	@PutMapping("projects/{projectId}/entries/{entryId}")
	public ResponseEntity<?> editEntry(@Valid @RequestBody Entry updatedEntry, @PathVariable("entryId") Long id, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>("Invalid data", HttpStatus.UNPROCESSABLE_ENTITY);
	    }
		try {
			Optional<Entry> toBeEdited = repository.findById(id);
			if (toBeEdited.isEmpty()) {
				return new ResponseEntity<>("Työaikakirjausta ei löytynyt", HttpStatus.NOT_FOUND);
			}
			Entry entry = toBeEdited.get();
			entry.setComment(updatedEntry.getComment());
			entry.setEntry_date(updatedEntry.getEntry_date());
			entry.setStart_time(updatedEntry.getStart_time());
			entry.setEnd_time(updatedEntry.getEnd_time());
			repository.save(entry);
			return new ResponseEntity<>(entry, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Työaikakirjauksen poisto
	@DeleteMapping("entries/{id}")
	public ResponseEntity<String> removeEntry(@PathVariable("id") Long id) {
		try {
			Optional<Entry> removableEntry = repository.findById(id);
			if (removableEntry.isEmpty()) {
				return new ResponseEntity<>("Työaikakirjausta ei löytynyt", HttpStatus.NOT_FOUND);
			}
			repository.delete(removableEntry.get());
			return new ResponseEntity<>("Työaikakirjaus poistettu onnistuneesti", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}