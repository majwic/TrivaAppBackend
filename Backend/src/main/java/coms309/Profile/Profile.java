package coms309.Profile;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import coms309.Friends.Friends;
import coms309.Lobby.Lobby;

import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties( {"friends", "friendOf",} )
public class Profile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(unique = true)
	private String username;
	
	private String displayname;
	private String password;
	private int highScore;
	private boolean isActive;
	
	@OneToMany(mappedBy = "user")
	public List<Friends> friends = new ArrayList<>();
	
	@OneToMany(mappedBy = "friend")
	public List<Friends> friendOf = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "host_id")
	@JsonIgnore
	private Lobby lobby;
	
	public Profile(String username, String password) {
		this.username = username;
		this.displayname = username;
		this.password = password;
		this.highScore = 0;
	}
	
	public Profile() {
	}
	
	public Profile updateProfile(Profile request) {
		String displayname = request.getDisplayname();
		String password = request.getPassword();
		int highScore = request.getHighScore();
		boolean isActive = request.getIsActive();
		
		if (!(displayname == null)) {
			this.displayname = displayname;
		}
		
		if (!(password == null)) {
			this.password = password;
		}
		
		if (this.highScore < highScore) {
			this.highScore = highScore;
		}
		
		return this;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getDisplayname() {
		return displayname;
	}
	
	public String getPassword() {
		return password;
	}

	public int getHighScore() {
		return highScore;
	}
	
	public boolean getIsActive() {
		return isActive;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void setLobby(Lobby lobby) {
		this.lobby = lobby;
	}
	
	public Lobby getLobby() {
		return lobby;
	}
}
