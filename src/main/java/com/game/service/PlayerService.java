package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PlayerService {
    List<Player> getCount(Specification<Player> specification);

    Page<Player> getAllPlayers(Specification<Player> specification, Pageable sortedByName);

    Player createPlayer(Player player);

    Player updatePlayer(String id, Player player);

    Player getPlayer(String id);

    void deletePlayer(String id);
}
