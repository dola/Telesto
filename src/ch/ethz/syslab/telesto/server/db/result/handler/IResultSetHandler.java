package ch.ethz.syslab.telesto.server.db.result.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;

public interface IResultSetHandler {
    List<DatabaseResultEntry> handleResultSet(ResultSet dbResults) throws SQLException;
}
