package coms309.Friends;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import coms309.Profile.Profile;
import coms309.Profile.ProfileRepository;

@RestController
public class FriendsController {
	
	@Autowired
	FriendsRepository friendsRepository;
	
	@Autowired
	ProfileRepository profileRepository;
	
	@PostMapping("/friends/{userId}/{friendUsername}")
	public String createFriends(@PathVariable String userId, @PathVariable String friendUsername) {
		Profile user = profileRepository.findById(Integer.parseInt(userId));
		Profile friend = profileRepository.findByUsername(friendUsername);
		
		if (user == friend) {
			return "failure:cannot friend self";
		}
		
		FriendsKey id = new FriendsKey(friend.getId(), user.getId());
		
		if (friendsRepository.existsById(id)) {
			Friends friends = friendsRepository.findById(id);
			friends.setAreFriends();
			friendsRepository.save(friends);
		} else if (!friendsRepository.existsById(id.reverse())) {
			friendsRepository.save(new Friends(user, friend));
		}
		
		return "success";
	}
	
	@GetMapping("/friends/{userId}")
	public List<Profile> getFriends(@PathVariable String userId) {
		int id = Integer.parseInt(userId);
		
		return friendsRepository.findByUserIdOrFriendIdAndAreFriendsTrue(id)
			.stream().map(f -> f.getUnMatchingProfile(id))
			.collect(Collectors.toList());
	}
	
	@GetMapping("/friends/requests/{userId}")
	public List<Profile> getFriendsRequests(@PathVariable String userId) {
		int id = Integer.parseInt(userId);
		
		return friendsRepository.findByFriendIdAndAreFriendsFalse(id)
			.stream().map(f-> f.getUser())
			.collect(Collectors.toList());
	}
	
	@DeleteMapping("/friends/{userId}/{friendId}")
	public String deleteFriends(@PathVariable String userId, @PathVariable String friendId) {
		FriendsKey id = new FriendsKey(Integer.parseInt(userId), Integer.parseInt(friendId));

		friendsRepository.deleteById(id);
		friendsRepository.deleteById(id.reverse());
		
		return "success";
	}
	
}

