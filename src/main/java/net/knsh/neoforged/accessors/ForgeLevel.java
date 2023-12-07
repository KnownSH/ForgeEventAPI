package net.knsh.neoforged.accessors;

import net.knsh.neoforged.neoforge.common.util.BlockSnapshot;

import java.util.ArrayList;

public interface ForgeLevel {
    void setCaptureBlockSnapshots(boolean bool);

    void setRestoringBlockSnapshots(boolean bool);

    boolean getCaptureBlockSnapshots();

    boolean getRestoringBlockSnapshots();

    ArrayList<BlockSnapshot> getCapturedBlockSnapshots();
}
