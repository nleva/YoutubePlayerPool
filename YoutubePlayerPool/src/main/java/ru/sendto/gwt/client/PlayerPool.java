package ru.sendto.gwt.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.DOM;

import ru.sendto.gwt.client.html.Div;
import ru.sendto.gwt.client.util.Bus;
import ru.sendto.gwt.yt.client.Player;
import ru.sendto.gwt.yt.client.YT;
import ru.sendto.gwt.yt.client.YT.PlayerReadyEvent;
import ru.sendto.gwt.yt.client.YT.PlayerVars;

public class PlayerPool {
	public List<Player> playerPool = new ArrayList<>();
	public Set<Player> busyPlayers = new HashSet<>();
	int poolInitSize = 4;
	int readyCount = 0;
	int currentPlayer=0;
	
	public PlayerPool(int poolSize) {
		
		poolSize=(poolSize<2)?2:poolSize;
		
		this.poolInitSize=poolSize;
		
		for(int i=0; i<poolSize;i++) {
			createPlayer();
		}
	}

	private void createPlayer() {
		String uid = DOM.createUniqueId();
		Div div = new Div();
		div.addClass("player");
		div.addClass("hidePlayer");
		div.setId(uid);
		Bus.getBy("playerPool").fire(div);
		Player p = YT.get().player(uid, "", PlayerVars.getMinimal());
		playerPool.add(p);
		Bus.getBy(uid).listen(PlayerReadyEvent.class, this::playerReady);
	}
	
	void playerReady(PlayerReadyEvent e) {
		readyCount++;
		if(readyCount<poolInitSize) 
			return;
		Bus.getBy("playerPool").fire("ready");
	}
	
	public Player get() {
		if(playerPool.size()<=1) {
			createPlayer();
			createPlayer();
		}
		Player pl = playerPool.remove(currentPlayer=(currentPlayer++%playerPool.size()));
		busyPlayers.add(pl);
		return pl;
	}
	
	public Player retrieve(Player pl) {
		busyPlayers.remove(pl);
		playerPool.add(pl);
		return pl;
	}
	
	
	
}
