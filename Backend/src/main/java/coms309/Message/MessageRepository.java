package coms309.Message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MessageRepository extends JpaRepository<Message, Long> { 
	
	@Query(value = "SELECT * FROM trivia.message m WHERE m.receiver_name = ?1 or m.sender_name = ?1 ORDER BY m.sent", nativeQuery = true)
	List<Message> getMessageHistory(String username);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "DELETE FROM trivia.message WHERE receiver_name = ?1 and sender_name = ?2 or receiver_name = ?2 and sender_name = ?1", nativeQuery = true)
	void deleteAllActivity(String participant1, String participant2);
	
	boolean existsById(Long id);
	
	@Transactional
	void deleteById(Long id);
}
