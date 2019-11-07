package ru.textanalysis.tawt.ms.interfaces.awf;

import ru.textanalysis.tawt.ms.interfaces.InitializationModule;
import ru.textanalysis.tawt.ms.internal.sp.BearingPhraseSP;

public interface IAWF extends InitializationModule {
    void applyAwfForBearingPhrase(BearingPhraseSP bearingPhraseSP);
}
