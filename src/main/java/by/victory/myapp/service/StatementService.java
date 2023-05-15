package by.victory.myapp.service;

import by.victory.myapp.domain.Statement;
import by.victory.myapp.domain.Trip;
import by.victory.myapp.repository.StatementRepository;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class StatementService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final StatementRepository statementRepository;

    public StatementService(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    public List<Statement> setTripForAllStatement(Trip trip) {
        log.debug("Set trip for all statement : {}", trip);
        setTripToNullByIdForAllStatement(trip);
        Set<Statement> statements = trip.getStatements();
        statements.forEach(statement -> statement.setTrip(trip));

        return statementRepository.saveAll(statements);
    }

    public void setTripToNullByIdForAllStatement(Trip trip) {
        log.debug("Set trip to null by id for all statement : {}", trip);
        List<Statement> statements = statementRepository.findAll();
        statements.forEach(statement -> {
            if (statement.getTrip() != null && (statement.getTrip().equals(trip) || statement.getTrip() == trip)) {
                statement.setTrip(null);
            }
        });

        statementRepository.saveAll(statements);
    }
}
