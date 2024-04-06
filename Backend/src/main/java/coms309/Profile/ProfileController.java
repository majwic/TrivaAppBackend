package coms309.Profile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
class ProfileController {
	
	@Autowired
	ProfileRepository profileRepository;
	
	/**
	 *         end point for creating account
	 * 
	 * @param  user: JSON containing new profile info
	 * @return "success" if profile was created
	 * 		   "failure" if user name or password was not included
	 *         "exists" if user name is already in use 
	 */
	@PostMapping("/profile")
	public String createProfile(@RequestBody Profile user) {
		if (user.getUsername() == null || user.getPassword() == null) {
			return "failure";
		} else if (profileRepository.existsByUsername(user.getUsername())) {
			return "exists";
		}
		user.setDisplayname(user.getUsername());
		profileRepository.save(user);
		
		return "success";
	}
	
	/**
	 * 		   end point for getting all active profiles
	 * 
	 * @return list of JSON profiles that are active
	 */
	@GetMapping("/profile/active")
	public List<Profile> getActiveProfiles() {
		return profileRepository.findByIsActiveTrue();
	}

	/**
	 * 		   end point getting profile by id
	 *
	 * @param  id: primary key lookup
	 * @return JSON with profile on success
	 * 		   null on failure to log in
	 */
	@GetMapping("/profile/{id}")
	public Profile getProfileById(@PathVariable String id) {
		if (profileRepository.existsById(Integer.parseInt(id))) {
			return profileRepository.findById(Integer.parseInt(id));
		} else {
			return null;
		}
	}

	/**
	 * 		   end point used for logging in
	 * 
	 * @param  username: unique user name
	 * @param  password: user names password
	 * @return JSON with profile on success
	 * 		   null on failure to log in
	 */
	@GetMapping("/profile/{username}/{password}")
	public Profile getProfile(@PathVariable String username, @PathVariable String password) {
		Profile user = profileRepository.findByUsername(username);
		
		if (user.getPassword().equals(password)) {
			profileRepository.save(user);
			
			return user;
		} else {
			return null;
		}
	}
	
	/**
	 * 		   end point used for updating profile
	 * 		   fields 
	 * 
	 * @param  id: primary key lookup
	 * @param  request: JSON containing value to change
	 * @return "success" on profile update
	 * 		   "failure" if request is null
	 */
	@PutMapping("/profile/{id}")
	public String putProfile(@PathVariable String id, @RequestBody Profile request) {
		Profile user = profileRepository.findById(Integer.parseInt(id));
		
		if(request == null) {
			return "failure";
		}
		
		user.updateProfile(request);
		profileRepository.save(user);
		
		return "success";
	}
	
	/**
	 * 		   end point used for deleting a profile from
	 * 	       database
	 * 
	 * @param  id: primary key lookup
	 * @return "success" on profile deletion 
	 * 		   "failure" if id does not link to profile
	 */
	@DeleteMapping("/profile/{id}")
	public String deleteProfile(@PathVariable String id) {
		if (profileRepository.existsById(Integer.parseInt(id))) {
			profileRepository.deleteById(Integer.parseInt(id));
			
			return "success";
		} else {
			return "failure";
		}
	}
	
}
