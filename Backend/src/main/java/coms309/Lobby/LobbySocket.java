package coms309.Lobby;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import coms309.Profile.Profile;
import coms309.Profile.ProfileRepository;

@Controller
@ServerEndpoint(value = "/lobby/{username}/{lobbyName}")
public class LobbySocket {
	private static LobbyRepository lbyRepo;
	
	@Autowired
	public void setLobbyRepository(LobbyRepository repo) {
		lbyRepo = repo;
	}
	
	private static ProfileRepository pflRepo;
	
	@Autowired
	public void setProfileRepository(ProfileRepository repo) {
		pflRepo = repo;
	}
	
	private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
	private static Map<String, Session> usernameSessionMap = new Hashtable<>();
	private static Map<Session, String> sessionLobbyNameMap = new Hashtable<>();
	
	private final Logger logger = LoggerFactory.getLogger(LobbySocket.class);
	
	@OnOpen
	public void onOpen(Session session, @PathParam("lobbyName") String lobbyName, @PathParam("username") String username) {
		logger.info("Entered into Open");
		
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);
		sessionLobbyNameMap.put(session, lobbyName);
		
		Profile pfl = pflRepo.findByUsername(username);
		Lobby lby;
		
		if (lbyRepo.existsByLobbyName(lobbyName)) {
			lby = lbyRepo.findByLobbyName(lobbyName);
			assignMemberToLobby(pfl, lby);
		} else {
			lby = new Lobby(pfl.getId(), lobbyName);
			lbyRepo.saveAndFlush(lby);
		}
		
		sendMessageToParticularUser(username, "Lobby => " + lby.getLobbyName());
		sendMessageToParticularUser(username, "Members => " + lby.getMembersString());
		sendMessageToParticularUser(username, "Host => " + pflRepo.findById(lby.getHostId()).getDisplayname());
		broadcast(lby, pfl.getDisplayname() + " joined the lobby");
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		logger.info("Entered into Message: Got Message: " + message);
		String username = sessionUsernameMap.get(session);
		
		Lobby lby = lbyRepo.findByLobbyName(sessionLobbyNameMap.get(session));
		Profile pfl = pflRepo.findByUsername(username);
		
		if (message.equals("/members")) {
			sendMessageToParticularUser(username, "Members => " + lby.getMembersString());
		} else {
			broadcast(lby, pfl.getDisplayname() + ": " + message);
		}
	}
	
	@OnClose
	public void onClose(Session session, @PathParam("lobbyName") String lobbyName) {
		logger.info("Entered into Close");
		
		String username = sessionUsernameMap.get(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);
		sessionLobbyNameMap.remove(session);
		
		Profile pfl = pflRepo.findByUsername(username);
		Lobby lby = lbyRepo.findByLobbyName(lobbyName);
		
		if (pfl.getId() == lby.getHostId()) {
			lby.getMembers().stream()
			.map(p -> p.getUsername())
			.map(p -> usernameSessionMap.get(p))
			.forEach(s -> {
				try {
					s.close();
				} catch (IOException e) {
					logger.info("Failed to close sessions from host");
					e.printStackTrace();
				}
			});;
			lbyRepo.deleteByHostId(pfl.getId());
		} else {
			removeMemberFromLobby(pfl, lby);
			broadcast(lby, pfl.getDisplayname() + " left the lobby");
		}
	}
	
	@OnError
	public void onError(Throwable throwable) {
		logger.info("Entered into Erro");
		throwable.printStackTrace();
	}
	
	private void assignMemberToLobby(Profile pfl, Lobby lby) {
		if (pfl == null || lby == null) {
			logger.info("Failed on assignMemberToLobby");
		}
		lby.addMembers(pfl);
		lbyRepo.saveAndFlush(lby);
		pfl.setLobby(lby);
		pflRepo.saveAndFlush(pfl);
	}
	
	private void removeMemberFromLobby(Profile pfl, Lobby lby) {
		pfl.setLobby(null);
		pflRepo.saveAndFlush(pfl);
		lby.removeMembers(pfl.getId());
		lbyRepo.saveAndFlush(lby);
	}
	
	private void broadcast(Lobby lby, String broadcast) {
		lby.getMembers().forEach(m -> sendMessageToParticularUser(m.getUsername(), broadcast));
		sendMessageToParticularUser(pflRepo.findById(lby.getHostId()).getUsername(), broadcast);
	}
	
	private void sendMessageToParticularUser(String username, String message) {
		try {
			usernameSessionMap.get(username).getBasicRemote().sendText(message);
		} catch(IOException e) {
			logger.info("Exception: " + e.getMessage().toString());
			e.printStackTrace();
		}
	}
}	

