package ch.ethz.syslab.telesto.server.db.procedure;

import ch.ethz.syslab.telesto.server.db.ReturnType;

public interface StoredProcedure {
    public String getMethodName();

    public int getArgumentCount();

    public int[] getArgumentTypes();

    public ReturnType getReturnType();

    public boolean hasReturnValue();

    public boolean hasSingleReturnValue();
}
