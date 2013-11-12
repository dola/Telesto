package ch.ethz.syslab.telesto.server.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.syslab.telesto.common.model.Queue;

public class QueueResultSetHandler implements IResultSetHandler {

    @Override
    public List<DatabaseResultEntry> handleResultSet(ResultSet dbResults) throws SQLException {
        // QueueRow:
        // [queue_id, queue_name]

        ArrayList<DatabaseResultEntry> res = new ArrayList<>(1);

        while (dbResults.next()) {
            Queue r = new Queue(dbResults.getInt(1), dbResults.getString(2));
            res.add(r);
        }

        return res;
    }

}
