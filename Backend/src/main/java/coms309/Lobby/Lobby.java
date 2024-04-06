package coms309.Lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import coms309.Profile.Profile;

@Entity
public class Lobby {

	@Id
	private int hostId;
	
	@Column(unique = true)
	private String lobbyName;
	
	@OneToMany(fetch = FetchType.EAGER)
	private List<Profile> members;
	
	public Lobby(int hostId, String lobbyName) {
		this.hostId = hostId;
		this.lobbyName = lobbyName;
		members = new ArrayList<>();
	}
	
	public Lobby() {
		members = new ArrayList<>();
	}
	
	public int getHostId() {
		return hostId;
	}
	
	public String getLobbyName() {
		return lobbyName;
	}
	
	public List<Profile> getMembers() {
		return members;
	}
	
	public String getMembersString() {
		return members.stream().map(m -> m.getDisplayname()).collect(Collectors.joining(", "));
	}
	
	public void setMembers(List<Profile> members) {
		this.members = members;
	}
	
	public void addMembers(Profile member) {
		members.add(member);
	}
	
	public void removeMembers(int memberId) {
		for (int i = 0; i < members.size(); ++i) {
			if (members.get(i).getId() == memberId) {
				members.remove(i);
				break;
			}
		}
	}
}
