package com.game.service;

import com.game.entity.Player;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;


@Service
public class PlayerServiceImpl implements PlayerService {

    private PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    private void validateFields(Player player) {

        if (player.getName() != null && (player.getName().length() < 1 || player.getName().length() > 12)) {
            throw new BadRequestException("Character name is too long or missing.");
        }

        if (player.getTitle() != null && (player.getTitle().length() < 1 || player.getTitle().length() > 30)) {
            throw new BadRequestException("Character title is too long or missing.");
        }

        if (player.getExperience() != null && (player.getExperience() < 1 || player.getExperience() > 10000000)) {
            throw new BadRequestException("Character experience out of range.");
        }

        if (player.getBirthday() != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(player.getBirthday());

            if (date.get(Calendar.YEAR) < 2000 || date.get(Calendar.YEAR) > 3000) {
                throw new BadRequestException("Registration date out of range.");
            }
        }
    }

    public Long validateId(String id) {
        try {
            Long idLong = Long.parseLong(id);

            if (idLong <= 0) {
                throw new BadRequestException("ID is incorrect.");
            }

            return idLong;
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID isn't a number", e);
        }
    }

    private Integer calculateCurrentLevel(Player player) {
        return (int) ((Math.sqrt(2500 + 200 * player.getExperience())) - 50) / 100;
    }

    private Integer calculateUntilNextLevel(Player player) {
        return 50 * (calculateCurrentLevel(player) + 1) * (calculateCurrentLevel(player) + 2) - player.getExperience();

    }

    @Override
    public List<Player> getCount(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Page<Player> getAllPlayers(Specification<Player> specification, Pageable sortedByName) {
        return playerRepository.findAll(specification, sortedByName);
    }

    @Override
    public Player createPlayer(Player player) {
        if (player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null)
            throw new BadRequestException("Please fill in all required fields");

        validateFields(player);

        if (player.getBanned() == null)
            player.setBanned(false);

        player.setLevel(calculateCurrentLevel(player));
        player.setUntilNextLevel(calculateUntilNextLevel(player));

        return playerRepository.saveAndFlush(player);
    }

    @Override
    public Player updatePlayer(String idString, Player player) {
        Long id = validateId(idString);

        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException("Player is not found.");
        } else {
            Player editablePlayer = playerRepository.findById(id).get();

            if (player.getName() == null
                    && player.getTitle() == null
                    && player.getRace() == null
                    && player.getProfession() == null
                    && player.getBirthday() == null
                    && player.getBanned() == null
                    && player.getExperience() == null) {
                return editablePlayer;
            } else {

                if ((player.getName() != null && (player.getName().length() < 1 || player.getName().length() > 12))
                        || (player.getTitle() != null && (player.getTitle().length() < 1 || player.getTitle().length() > 30))
                        || (player.getExperience() != null && (player.getExperience() < 1 || player.getExperience() > 10000000))
                        || (player.getBirthday() != null && (player.getBirthday().getYear() + 1900 < 2000 || player.getBirthday().getYear() + 1900 > 3000))) {
                    throw new BadRequestException("Invalid fields.");
                }

                Optional.ofNullable(player.getName()).ifPresent(editablePlayer::setName);
                Optional.ofNullable(player.getTitle()).ifPresent(editablePlayer::setTitle);
                Optional.ofNullable(player.getRace()).ifPresent(editablePlayer::setRace);
                Optional.ofNullable(player.getProfession()).ifPresent(editablePlayer::setProfession);
                Optional.ofNullable(player.getBirthday()).ifPresent(editablePlayer::setBirthday);
                Optional.ofNullable(player.getBanned()).ifPresent(editablePlayer::setBanned);
                Optional.ofNullable(player.getExperience()).ifPresent(editablePlayer::setExperience);
                editablePlayer.setLevel(calculateCurrentLevel(editablePlayer));
                editablePlayer.setUntilNextLevel(calculateUntilNextLevel(editablePlayer));

            }
            return playerRepository.save(editablePlayer);
        }
    }

    @Override
    public void deletePlayer(String idString) {
        Long id = validateId(idString);

        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
        } else {
            throw new PlayerNotFoundException("Player is not found.");
        }
    }

    @Override
    public Player getPlayer(String idString) {
        Long id = validateId(idString);

        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException("Player is not found.");
        }
        return playerRepository.findById(id).get();
    }


}
