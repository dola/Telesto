package ch.ethz.syslab.telesto.server.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.syslab.telesto.common.model.Message;

public class MessageResultSetHandler implements IResultSetHandler {

    @Override
    public List<DatabaseResultEntry> handleResultSet(ResultSet dbResults) throws SQLException {
        // MessageRow:
        // [message_id, queue_id, sender_id, receiver_id, context, priority, time_of_arrival, message]

        ArrayList<DatabaseResultEntry> res = new ArrayList<>(1);

        while (dbResults.next()) {
            Message r = new Message(dbResults.getInt(1),
                    dbResults.getInt(2),
                    dbResults.getInt(3),
                    dbResults.getInt(4),
                    dbResults.getInt(5),
                    dbResults.getByte(6),
                    dbResults.getTimestamp(7),
                    dbResults.getString(8));
            res.add(r);
        }

        return res;
    }

}
