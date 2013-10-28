package ch.ethz.syslab.telesto.server.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.postgresql.ds.PGPoolingDataSource;

import ch.ethz.syslab.telesto.server.config.CONFIG;
import ch.ethz.syslab.telesto.server.controller.PacketProcessingException;
import ch.ethz.syslab.telesto.server.db.procedure.ClientProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.MessageProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.QueueProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.StoredProcedure;
import ch.ethz.syslab.telesto.server.db.result.ClientRow;
import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;
import ch.ethz.syslab.telesto.server.db.result.MessageRow;
import ch.ethz.syslab.telesto.server.db.result.QueueRow;
import ch.ethz.syslab.telesto.server.db.result.handler.ClientResultSetHandler;
import ch.ethz.syslab.telesto.server.db.result.handler.IResultSetHandler;
import ch.ethz.syslab.telesto.server.db.result.handler.MessageResultSetHandler;
import ch.ethz.syslab.telesto.server.db.result.handler.QueueResultSetHandler;
import ch.ethz.syslab.telesto.util.Log;

public class Database {
    private static Log LOGGER = new Log(Database.class);

    private PGPoolingDataSource connectionPool;

    private Map<ReturnType, IResultSetHandler> resultSetHandler = new HashMap<ReturnType, IResultSetHandler>();

    public Database() {
        resultSetHandler.put(ReturnType.CLIENT_TABLE, new ClientResultSetHandler());
        resultSetHandler.put(ReturnType.QUEUE_TABLE, new QueueResultSetHandler());
        resultSetHandler.put(ReturnType.MESSAGE_TABLE, new MessageResultSetHandler());
    }

    public void initialize() {
        if (connectionPool == null) {
            connectionPool = new PGPoolingDataSource();
            connectionPool.setApplicationName(CONFIG.DB_SERVER_NAME);
            connectionPool.setServerName(CONFIG.DB_SERVER_NAME);
            connectionPool.setPortNumber(CONFIG.DB_PORT_NUMBER);
            connectionPool.setDatabaseName(CONFIG.DB_NAME);
            connectionPool.setUser(CONFIG.DB_USER);
            connectionPool.setPassword(CONFIG.DB_PASSWORD);
            connectionPool.setMaxConnections(CONFIG.DB_MAX_CONNECTIONS);

            // add more?
            // e.g. log writer and log level
        }
    }

    public Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            initialize();
        }
        return connectionPool.getConnection();
    }

    public static String buildCallStatement(String methodName, int arg_count, boolean hasReturnValue) {
        LOGGER.fine("Building call string to method %s", methodName);
        StringBuilder sb = new StringBuilder("{ ");

        if (hasReturnValue) {
            sb.append("? = ");
        }
        sb.append("call ").append(methodName).append("(");
        if (arg_count > 0) {
            sb.append(" ? ");
            for (int i = 1; i < arg_count; i++) {
                sb.append(", ? ");
            }
        }
        sb.append(") }");

        return sb.toString();
    }

    private CallableStatement prepareCallableStatement(StoredProcedure proc, Object... arguments) throws SQLException, PacketProcessingException {
        if (proc.getArgumentCount() != arguments.length) {
            // number of arguments does not match procedure
            throw new PacketProcessingException("Argument count does not match to procedure definition for procedure " + proc.getMethodName());
        }

        Connection conn = getConnection();
        String call = buildCallStatement(proc.getMethodName(), proc.getArgumentCount(), proc.hasSingleReturnValue());
        CallableStatement statement = conn.prepareCall(call);

        // set arguments
        int argIdx = 0;
        int parameterIndex = proc.hasSingleReturnValue() ? 2 : 1;
        for (int argType : proc.getArgumentTypes()) {
            statement.setObject(parameterIndex, arguments[argIdx], argType);

            parameterIndex++;
            argIdx++;
        }

        if (proc.hasSingleReturnValue()) {
            statement.registerOutParameter(1, proc.getReturnType().getSqlType());
        }

        return statement;
    }

    public void callProcedure(StoredProcedure proc, Object... arguments) throws PacketProcessingException {
        if (proc.hasReturnValue()) {
            throw new PacketProcessingException("Procedure is not allowed to have a return value to be used with this method");
        }

        CallableStatement statement = null;

        try {
            statement = prepareCallableStatement(proc, arguments);

            statement.execute();

        } catch (SQLException e) {
            throw new PacketProcessingException("Error during database interaction", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }

        return;
    }

    /**
     * Procedure that returns a single integer value
     * 
     * @param proc
     * @param arguments
     * @return
     * @throws PacketProcessingException
     */
    public int callSimpleProcedure(StoredProcedure proc, Object... arguments) throws PacketProcessingException {
        if (!proc.hasReturnValue() || proc.getReturnType() != ReturnType.INTEGER) {
            throw new PacketProcessingException("Procedure must have returnType INTEGER");
        }

        CallableStatement statement = null;
        int result = 0;

        try {
            statement = prepareCallableStatement(proc, arguments);

            statement.execute();
            result = statement.getInt(1);

        } catch (SQLException e) {
            throw new PacketProcessingException("Error during database interaction", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }

        return result;
    }

    public List<DatabaseResultEntry> callSelectingProcedure(StoredProcedure proc, Object... arguments) throws PacketProcessingException {
        if (!proc.hasReturnValue()) {
            throw new PacketProcessingException("Procedure has to have return value to be usable with this method");
        }

        CallableStatement statement = null;
        List<DatabaseResultEntry> result = new ArrayList<>();

        try {
            statement = prepareCallableStatement(proc, arguments);

            if (statement.execute()) {
                // result set retrieved
                if (resultSetHandler.containsKey(proc.getReturnType())) {
                    // find handler for result set
                    ResultSet dbResults = statement.getResultSet();
                    result = resultSetHandler.get(proc.getReturnType()).handleResultSet(dbResults);
                } else {
                    // no handler registered: should have called other method
                    LOGGER.warning("No handler registered for prcedure return type %s in procedure %s", proc.getReturnType(), proc.getMethodName());
                    throw new PacketProcessingException("No DatabaseResultHandler found");
                }
            }

        } catch (SQLException e) {
            throw new PacketProcessingException("Error during database interaction", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }

        return result;
    }

    // USE EVERYTHING BELOW THIS:
    // TODO: optimize methods below share more code

    public List<MessageRow> callMessageProcedure(MessageProcedure proc, Object... arguments) throws PacketProcessingException {
        if (!proc.hasReturnValue()) {
            throw new PacketProcessingException("Procedure has to have return value to be usable with this method");
        }

        CallableStatement statement = null;
        List<MessageRow> result = new ArrayList<>();

        try {
            statement = prepareCallableStatement(proc, arguments);

            if (statement.execute()) {
                // result set retrieved
                ResultSet dbResults = statement.getResultSet();

                // MessageRow:
                // [message_id, queue_id, sender_id, receiver_id, context, priority, time_of_arrival, message]
                while (dbResults.next()) {
                    MessageRow r = new MessageRow(dbResults.getInt(1),
                            dbResults.getInt(2),
                            dbResults.getInt(3),
                            dbResults.getInt(4),
                            dbResults.getInt(5),
                            dbResults.getByte(6),
                            dbResults.getTimestamp(7),
                            dbResults.getString(8));
                    result.add(r);
                }
            }

        } catch (SQLException e) {
            throw new PacketProcessingException("Error during database interaction", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }

        return result;
    }

    public List<QueueRow> callQueueProcedure(QueueProcedure proc, Object... arguments) throws PacketProcessingException {
        if (!proc.hasReturnValue()) {
            throw new PacketProcessingException("Procedure has to have return value to be usable with this method");
        }

        CallableStatement statement = null;
        List<QueueRow> result = new ArrayList<>();

        try {
            statement = prepareCallableStatement(proc, arguments);

            if (statement.execute()) {
                // result set retrieved
                ResultSet dbResults = statement.getResultSet();

                // QueueRow:
                // [queue_id, queue_name]

                while (dbResults.next()) {
                    QueueRow r = new QueueRow(dbResults.getInt(1), dbResults.getString(2));
                    result.add(r);
                }
            }

        } catch (SQLException e) {
            throw new PacketProcessingException("Error during database interaction", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }

        return result;
    }

    public List<ClientRow> callClientProcedure(ClientProcedure proc, Object... arguments) throws PacketProcessingException {
        if (!proc.hasReturnValue()) {
            throw new PacketProcessingException("Procedure has to have return value to be usable with this method");
        }

        CallableStatement statement = null;
        List<ClientRow> result = new ArrayList<>();

        try {
            statement = prepareCallableStatement(proc, arguments);

            if (statement.execute()) {
                // result set retrieved
                ResultSet dbResults = statement.getResultSet();

                // ClientRow:
                // [client_id, client_name, operation_mode]

                while (dbResults.next()) {
                    ClientRow r = new ClientRow(dbResults.getInt(1), dbResults.getString(2), dbResults.getByte(3));
                    result.add(r);
                }

            }

        } catch (SQLException e) {
            throw new PacketProcessingException("Error during database interaction", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }

        return result;
    }
}
