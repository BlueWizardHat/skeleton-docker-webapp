package net.bluewizardhat.dockerwebapp.rest;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import net.bluewizardhat.dockerwebapp.database.entities.BananaFish;
import net.bluewizardhat.dockerwebapp.database.repositories.BananaFishRepository;

@RestController
@RequestMapping(path = "/bananaFish")
public class BananaFishRestController extends BaseRestController {

	@Autowired
	private BananaFishRepository bananaFishRepository;

	@GetMapping(path = "/findAll", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public DeferredResult<List<BananaFish>> findAll(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "50") int pageSize) {
		return submit(() -> bananaFishRepository.findAll(new PageRequest(pageNumber, pageSize, Direction.ASC, "id")).getContent());
	}

	@PostMapping(path = "/save", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BananaFish save(@RequestBody BananaFish newFish, String _csrf) {
		newFish.setCreated(OffsetDateTime.now());
		return bananaFishRepository.save(newFish);
	}
}
