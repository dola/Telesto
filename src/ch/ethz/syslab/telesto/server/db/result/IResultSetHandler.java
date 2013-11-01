package ch.ethz.syslab.telesto.server.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IResultSetHandler {
    List<DatabaseResultEntry> handleResultSet(ResultSet dbResults) throws SQLException;
}
