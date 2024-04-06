package coms309.Lobby;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LobbyController {
	
	@Autowired
	LobbyRepository lobbyRepository;
	
	/*
	 * returns list of all lobby names
	 */
	@GetMapping("/lobby/all")
	public List<String> getLobbys() {
		return lobbyRepository.getAllLobbys();
	}
	
	/*
	 * returns "notAvailable" when lobby name is taken
	 * returns "available" when lobby name is not take
	 */
	@GetMapping("/lobby/available/{lobbyName}")
	public String isAvaiblable(@PathVariable String lobbyName) {
		if (lobbyRepository.existsByLobbyName(lobbyName)) {
			return "notAvailable";
		} else {
			return "available";
		}
	}
	 /*
	  * returns list of all members in lobby (excludes host)
	  */
	@GetMapping("/lobby/members/{lobbyName}")
	public List<String> getMembers(@PathVariable String lobbyName) {
		return lobbyRepository.findByLobbyName(lobbyName)
				.getMembers().stream()
				.map(m -> m.getDisplayname())
				.collect(Collectors.toList());
	}
}
