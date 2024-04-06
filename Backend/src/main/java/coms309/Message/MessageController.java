package coms309.Message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
	
	@Autowired
	MessageRepository messageRepository;
	
	@GetMapping("/message/history/{username}")
	public List<Message> getMessageHistory(@PathVariable("username") String username) {
		return messageRepository.getMessageHistory(username);
	}
	
	@DeleteMapping("/message/all/{participant1}/{participant2}")
	public void deleteAllActivity(@PathVariable("participant1") String p1, @PathVariable("participant2") String p2) {
		messageRepository.deleteAllActivity(p1, p2);
	}
	
	@DeleteMapping("/message/{id}")
	public String deleteById(@PathVariable("id") String id) {
		if (messageRepository.existsById(Long.parseLong(id))) {
			messageRepository.deleteById(Long.parseLong(id));
			return "success";
		}
		return "failure";
	}
}
