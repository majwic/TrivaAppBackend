package coms309.Lobby;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LobbyRepository extends JpaRepository<Lobby, Long> {
	
	boolean existsByLobbyName(String lobbyName);
	
	Lobby findByLobbyName(String lobbyName);
	
	@Transactional
	void deleteByHostId(int hostId);
	
	@Query(value = "select lobby_name from trivia.lobby", nativeQuery = true)
    List<String> getAllLobbys();
}
