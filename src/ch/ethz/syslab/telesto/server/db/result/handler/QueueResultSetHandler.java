package ch.ethz.syslab.telesto.server.db.result.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;
import ch.ethz.syslab.telesto.server.db.result.QueueRow;

public class QueueResultSetHandler implements IResultSetHandler {

    @Override
    public ArrayList<DatabaseResultEntry> handleResultSet(ResultSet dbResults) throws SQLException {
        // QueueRow:
        // [queue_id, queue_name]

        ArrayList<DatabaseResultEntry> res = new ArrayList<>(1);

        while (dbResults.next()) {
            QueueRow r = new QueueRow(dbResults.getInt(1), dbResults.getString(2));
            res.add(r);
        }

        return res;
    }

}
