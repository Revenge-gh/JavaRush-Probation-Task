package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PlayerService {
    List<Player> getAllRegisteredPlayersList(Specification<Player> specification);
    Page<Player> getAllRegisteredPlayersList(Specification<Player> specification, Pageable sortedByName);
    Player createPlayer(Player player);
    Player updatePlayer(Long id, Player player);
    void deletePlayer(Long id);
    Player getPlayer(Long id);
    Long validateId(String id);
    Specification<Player> nameFilter(String name);
    Specification<Player> titleFilter(String title);
    Specification<Player> raceFilter(Race race);
    Specification<Player> professionFilter(Profession profession);
    Specification<Player> birthdayFilter(Long after, Long before);
    Specification<Player> bannedFilter(Boolean banned);
    Specification<Player> experienceFilter(Integer minExperience, Integer maxExperience);
    Specification<Player> levelFilter(Integer minLevel, Integer maxLevel);
}
