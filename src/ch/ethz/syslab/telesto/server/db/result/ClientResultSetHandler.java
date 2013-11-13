package ch.ethz.syslab.telesto.server.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.ClientMode;

public class ClientResultSetHandler implements IResultSetHandler {

    @Override
    public List<DatabaseResultEntry> handleResultSet(ResultSet dbResults) throws SQLException {
        // ClientRow:
        // [client_id, client_name, operation_mode]

        List<DatabaseResultEntry> res = new ArrayList<>(1);

        while (dbResults.next()) {
            Client r = new Client(dbResults.getInt(1), dbResults.getString(2), ClientMode.fromByteValue(dbResults.getByte(3)));
            res.add(r);
        }

        return res;
    }

}
