package coms309.Profile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
	Profile findById(int id);
	
	List<Profile> findByIsActiveTrue();
	
	List<Profile> findByLobbyHostId(int lobbyHostId);
	
	boolean existsByUsername(String username);
	
	boolean existsById(int id);
	
	@Transactional
	void deleteById(int id);
	
	Profile findByUsername(String username);
}
